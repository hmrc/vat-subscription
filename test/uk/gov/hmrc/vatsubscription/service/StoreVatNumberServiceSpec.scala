/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.vatsubscription.service

import java.util.UUID

import cats.data.NonEmptyList
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.Json
import play.api.test.FakeRequest
import reactivemongo.api.commands.UpdateWriteResult
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.config.featureswitch.{AlreadySubscribedCheck, MTDEligibilityCheck}
import uk.gov.hmrc.vatsubscription.config.mocks.MockConfig
import uk.gov.hmrc.vatsubscription.connectors.mocks.{MockAgentClientRelationshipsConnector, MockKnownFactsAndControlListInformationConnector, MockMandationStatusConnector}
import uk.gov.hmrc.vatsubscription.helpers.TestConstants
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.{ControlListInformationVatNumberNotFound, KnownFactsInvalidVatNumber, MtdEligible, MtdIneligible}
import uk.gov.hmrc.vatsubscription.models._
import uk.gov.hmrc.vatsubscription.models.monitoring.AgentClientRelationshipAuditing.AgentClientRelationshipAuditModel
import uk.gov.hmrc.vatsubscription.models.monitoring.ControlListAuditing._
import uk.gov.hmrc.vatsubscription.repositories.mocks.MockSubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.service.mocks.monitoring.MockAuditService
import uk.gov.hmrc.vatsubscription.services.StoreVatNumberService._
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreVatNumberServiceSpec
  extends UnitSpec with MockAgentClientRelationshipsConnector with MockSubscriptionRequestRepository
    with MockAuditService with MockConfig
    with MockMandationStatusConnector
    with MockKnownFactsAndControlListInformationConnector {

  object TestStoreVatNumberService extends StoreVatNumberService(
    mockSubscriptionRequestRepository,
    mockAgentClientRelationshipsConnector,
    mockMandationStatusConnector,
    mockKnownFactsAndControlListInformationConnector,
    mockAuditService,
    mockConfig
  )

  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(FakeRequest().headers)
  implicit val request = FakeRequest("POST", "testUrl")

  val agentUser = Enrolments(Set(testAgentEnrolment))
  val principalUser = Enrolments(Set(testPrincipalEnrolment))
  val freshUser = Enrolments(Set.empty)

  "storeVatNumber" when {
    "the user is an agent" when {
      "there is an agent-client relationship" when {
        "the vat number is not already subscribed for MTD-VAT" when {
          "the VAT number is eligible for MTD" when {
            "the vat number is stored successfully" should {
              "return a StoreVatNumberSuccess" in {
                enable(AlreadySubscribedCheck)
                enable(MTDEligibilityCheck)

                mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
                mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
                mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))
                mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))

                val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
                res shouldBe Right(StoreVatNumberSuccess)

                verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = true))
              }
            }
            "the vat number is not stored successfully" should {
              "return a VatNumberDatabaseFailure" in {
                enable(AlreadySubscribedCheck)
                enable(MTDEligibilityCheck)

                mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
                mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonDigital)))
                mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))
                mockUpsertVatNumber(testVatNumber)(Future.failed(new Exception))

                val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
                res shouldBe Left(VatNumberDatabaseFailure)

                verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = true))
              }
            }
          }
          "the VAT number is already voluntarily subscribed for MTD-VAT" should {
            "return AlreadySubscribed" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
              mockGetMandationStatus(testVatNumber)(Future.successful(Right(MTDfBVoluntary)))

              val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
              res shouldBe Left(AlreadySubscribed)

              verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = true))
            }
          }
          "the VAT number is already mandated for MTD-VAT" should {
            "return AlreadySubscribed" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
              mockGetMandationStatus(testVatNumber)(Future.successful(Right(MTDfBMandated)))

              val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
              res shouldBe Left(AlreadySubscribed)

              verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = true))
            }
          }
          "the already subscribed check feature switch is turned off" should {
            "always treat the user as not subscribed" in {
              disable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
              mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))
              mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))

              val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
              res shouldBe Right(StoreVatNumberSuccess)

              verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = true))
            }
          }
        }
        "the VAT number is not eligible for MTD" should {
          "return Ineligible" in {
            enable(AlreadySubscribedCheck)
            enable(MTDEligibilityCheck)

            mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
            mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(MtdIneligible(NonEmptyList.one("")))))

            val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
            res shouldBe Left(Ineligible)

            verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = true))
          }
        }
        "the MTDEligibilityCheck feature switch is turned off" should {
          "always treat the user as eligible" in {
            enable(AlreadySubscribedCheck)

            mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
            mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))

            val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
            res shouldBe Right(StoreVatNumberSuccess)

            verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = true))
          }
        }
      }

      "there is not an agent-client-relationship" should {
        "return a RelationshipNotFound" in {
          mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(NoRelationshipResponse)))

          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
          res shouldBe Left(RelationshipNotFound)

          verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber, TestConstants.testAgentReferenceNumber, haveRelationship = false))
        }
      }

      "the call to agent-client-relationships-fails" should {
        "return an AgentServicesConnectionFailure" in {
          mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(
            Future.successful(Left(CheckAgentClientRelationshipResponseFailure(INTERNAL_SERVER_ERROR, Json.obj())))
          )

          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser, None, None))
          res shouldBe Left(AgentServicesConnectionFailure)
        }
      }
    }

    "the user is a principal user" when {
      "the user has an existing HMCE-VAT enrolment" when {
        "the vat number is not already subscribed for MTD-VAT" when {
          "the vat number is stored successfully" should {
            "return StoreVatNumberSuccess" in {
              enable(AlreadySubscribedCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
              mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))

              val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, principalUser, None, None))
              res shouldBe Right(StoreVatNumberSuccess)
            }
          }
          "the vat number is not stored successfully" should {
            "return a VatNumberDatabaseFailure" in {
              enable(AlreadySubscribedCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonDigital)))
              mockUpsertVatNumber(testVatNumber)(Future.failed(new Exception))

              val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, principalUser, None, None))
              res shouldBe Left(VatNumberDatabaseFailure)
            }
          }
        }
        "the VAT number is already subscribed for MTD-VAT" should {
          "return AlreadySubscribed" in {
            enable(AlreadySubscribedCheck)

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(MTDfBVoluntary)))

            val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, principalUser, None, None))
            res shouldBe Left(AlreadySubscribed)
          }
        }
        "the vat number does not match enrolment" should {
          "return DoesNotMatchEnrolment" in {
            enable(AlreadySubscribedCheck)

            val res = await(TestStoreVatNumberService.storeVatNumber(UUID.randomUUID().toString, principalUser, None, None))
            res shouldBe Left(DoesNotMatchEnrolment)
          }
        }
      }

      "the user does not have either enrolment" should {
        "return InsufficientEnrolments" in {
          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, Enrolments(Set.empty), None, None))
          res shouldBe Left(InsufficientEnrolments)
        }
      }
    }

    "the user has a fresh cred" when {

      def call = TestStoreVatNumberService.storeVatNumber(testVatNumber, freshUser, Some(testPostCode filterNot(_.isWhitespace)), Some(testDateOfRegistration))

      "the vat number is not already subscribed for MTD-VAT" when {
        "the vat number is stored successfully" should {
          "return StoreVatNumberSuccess" in {
            enable(AlreadySubscribedCheck)
            enable(MTDEligibilityCheck)

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
            mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))
            mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))

            val res = await(call)
            verifyAudit(ControlListAuditModel(testVatNumber, isSuccess = true))
            res shouldBe Right(StoreVatNumberSuccess)
          }
        }
        "Known facts and control list returned ineligible" should {
          "return a Ineligible" in {
            enable(AlreadySubscribedCheck)
            enable(MTDEligibilityCheck)

            val ineligibilityReason1 = "reason1"
            val ineligibilityReason2 = "reason2"

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
            mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(MtdIneligible(NonEmptyList(ineligibilityReason1, List(ineligibilityReason2))))))

            val res = await(call)
            verifyAudit(ControlListAuditModel(testVatNumber, isSuccess = false, failureReasons = Seq(ineligibilityReason1, ineligibilityReason2)))
            res shouldBe Left(Ineligible)
          }
        }
        "Known facts and control list returned ControlListInformationVatNumberNotFound" should {
          "return a VatNotFound" in {
            enable(AlreadySubscribedCheck)
            enable(MTDEligibilityCheck)

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
            mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(ControlListInformationVatNumberNotFound)))

            val res = await(call)
            verifyAudit(ControlListAuditModel(testVatNumber, isSuccess = false, failureReasons = Seq(vatNumberNotFound)))
            res shouldBe Left(VatNotFound)
          }
        }
        "Known facts and control list returned KnownFactsInvalidVatNumber" should {
          "return a VatInvalid" in {
            enable(AlreadySubscribedCheck)
            enable(MTDEligibilityCheck)

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
            mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(KnownFactsInvalidVatNumber)))

            val res = await(call)
            verifyAudit(ControlListAuditModel(testVatNumber, isSuccess = false, failureReasons = Seq(invalidVatNumber)))
            res shouldBe Left(VatInvalid)
          }
        }
        "known facts returned from api differs from known facts by the user" should {
          "return a KnownFactsMismatch" in {
            enable(AlreadySubscribedCheck)
            enable(MTDEligibilityCheck)

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
            mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode.drop(1), testDateOfRegistration))))

            val res = await(call)
            res shouldBe Left(KnownFactsMismatch)
          }
        }
        "the vat number is not stored successfully" should {
          "return a VatNumberDatabaseFailure" in {
            enable(AlreadySubscribedCheck)
            enable(MTDEligibilityCheck)

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonDigital)))
            mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))
            mockUpsertVatNumber(testVatNumber)(Future.failed(new Exception))

            val res = await(call)
            res shouldBe Left(VatNumberDatabaseFailure)
          }
        }
      }
      "the VAT number is already subscribed for MTD-VAT" should {
        "return AlreadySubscribed" in {
          enable(AlreadySubscribedCheck)
          enable(MTDEligibilityCheck)

          mockGetMandationStatus(testVatNumber)(Future.successful(Right(MTDfBVoluntary)))

          val res = await(call)
          res shouldBe Left(AlreadySubscribed)
        }
      }
      "the user does not have either enrolment and did not provide both known facts" should {
        "return InsufficientEnrolments" in {
          enable(AlreadySubscribedCheck)
          enable(MTDEligibilityCheck)

          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, freshUser, None, None))
          res shouldBe Left(InsufficientEnrolments)
        }
      }
      "the MtdEligibility feature switch is turned off" should {
        "throw an exception" in {
          enable(AlreadySubscribedCheck)

          mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
          mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))
          mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))

          intercept[InternalServerException](await(call))
        }
      }
    }

    "the user does not have either enrolment" should {
      "return InsufficientEnrolments" in {
        val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, freshUser, None, None))
        res shouldBe Left(InsufficientEnrolments)
      }
    }
  }

}
