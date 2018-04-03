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

import org.scalatest.EitherValues
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.Json
import play.api.test.FakeRequest
import reactivemongo.api.commands.UpdateWriteResult
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAgentClientRelationshipsConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.models.monitoring.AgentClientRelationshipAuditing.AgentClientRelationshipAuditModel
import uk.gov.hmrc.vatsubscription.models.{CheckAgentClientRelationshipResponseFailure, HaveRelationshipResponse, NoRelationshipResponse}
import uk.gov.hmrc.vatsubscription.repositories.mocks.MockSubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.service.mocks.monitoring.MockAuditService
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreVatNumberServiceSpec
  extends UnitSpec with MockAgentClientRelationshipsConnector with MockSubscriptionRequestRepository with MockAuditService with EitherValues {

  object TestStoreVatNumberService extends StoreVatNumberService(
    mockSubscriptionRequestRepository,
    mockAgentClientRelationshipsConnector,
    mockAuditService
  )

  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(FakeRequest().headers)
  implicit val request = FakeRequest("POST", "testUrl")

  val agentUser = Enrolments(Set(testAgentEnrolment))
  val principalUser = Enrolments(Set(testPrincipalEnrolment))

  "storeVatNumber" when {
    "the user is an agent" when {
      "there is an agent-client relationship" when {
        "the vat number is stored successfully" should {
          "return a StoreVatNumberSuccess" in {
            mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
            mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))

            val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser))
            res.right.value shouldBe StoreVatNumberSuccess

            verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber,TestConstants.testAgentReferenceNumber, isSuccess = true))
          }
        }
        "the vat number is not stored successfully" should {
          "return a VatNumberDatabaseFailure" in {
            mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(HaveRelationshipResponse)))
            mockUpsertVatNumber(testVatNumber)(Future.failed(new Exception))

            val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser))
            res.left.value shouldBe VatNumberDatabaseFailure

            verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber,TestConstants.testAgentReferenceNumber, isSuccess = true))
          }
        }
      }

      "there is not an agent-client-relationship" should {
        "return a RelationshipNotFound" in {
          mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(Future.successful(Right(NoRelationshipResponse)))

          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser))
          res.left.value shouldBe RelationshipNotFound

          verifyAudit(AgentClientRelationshipAuditModel(TestConstants.testVatNumber,TestConstants.testAgentReferenceNumber, isSuccess = false))
        }
      }

      "the call to agent-client-relationships-fails" should {
        "return an AgentServicesConnectionFailure" in {
          mockCheckAgentClientRelationship(testAgentReferenceNumber, testVatNumber)(
            Future.successful(Left(CheckAgentClientRelationshipResponseFailure(INTERNAL_SERVER_ERROR, Json.obj())))
          )

          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, agentUser))
          res.left.value shouldBe AgentServicesConnectionFailure
        }
      }
    }

    "the user is a principal user" when {
      "the vat number is stored successfully" should {
        "return DoesNotMatchEnrolment" in {
          mockUpsertVatNumber(testVatNumber)(Future.successful(mock[UpdateWriteResult]))
          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, principalUser))
          res.right.value shouldBe StoreVatNumberSuccess
        }
      }
      "the vat number is not stored successfully" should {
        "return a VatNumberDatabaseFailure" in {
          mockUpsertVatNumber(testVatNumber)(Future.failed(new Exception))

          val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, principalUser))
          res.left.value shouldBe VatNumberDatabaseFailure
        }
      }
      "the vat number does not match enrolment" should {
        "return DoesNotMatchEnrolment" in {
          val res = await(TestStoreVatNumberService.storeVatNumber(UUID.randomUUID().toString, principalUser))
          res.left.value shouldBe DoesNotMatchEnrolment
        }
      }
    }

    "the user does not have either enrolment" should {
      "return InsufficientEnrolments" in {
        val res = await(TestStoreVatNumberService.storeVatNumber(testVatNumber, Enrolments(Set.empty)))
        res.left.value shouldBe InsufficientEnrolments
      }
    }
  }

}
