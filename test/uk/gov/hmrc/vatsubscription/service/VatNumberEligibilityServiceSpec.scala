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

import cats.data.NonEmptyList
import org.scalatest.EitherValues
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.config.featureswitch.{AlreadySubscribedCheck, MTDEligibilityCheck}
import uk.gov.hmrc.vatsubscription.config.mocks.MockConfig
import uk.gov.hmrc.vatsubscription.connectors.mocks.{MockKnownFactsAndControlListInformationConnector, MockMandationStatusConnector}
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.GetMandationStatusHttpParser.GetMandationStatusHttpFailure
import uk.gov.hmrc.vatsubscription.httpparsers.{InvalidVatNumber => _, VatNumberNotFound => _, _}
import uk.gov.hmrc.vatsubscription.models.{MTDfBMandated, MTDfBVoluntary, NonDigital, NonMTDfB}
import uk.gov.hmrc.vatsubscription.services.VatNumberEligibilityService
import uk.gov.hmrc.vatsubscription.services.VatNumberEligibilityService._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatNumberEligibilityServiceSpec extends UnitSpec with EitherValues
  with MockMandationStatusConnector with MockKnownFactsAndControlListInformationConnector with MockConfig {

  object TestVatNumberEligibilityService extends VatNumberEligibilityService(
    mockMandationStatusConnector,
    mockKnownFactsAndControlListInformationConnector,
    mockConfig
  )

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "checkVatNumberEligibility" when {
    "the AlreadySubscribedCheck feature switch is enabled" when {
      "the mandation status service returns NonMTDfB" when {
        "the MTDEligibilityCheck feature switch is enabled" when {
          "the known facts and control list service returns MtdEligible" should {
            "return VatNumberEligible" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
              mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))

              await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).right.value shouldBe VatNumberEligible
            }
          }
          "the known facts and control list service returns MtdIneligible" should {
            "return VatNumberIneligible" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
              mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(MtdIneligible(NonEmptyList.one("")))))

              await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).left.value shouldBe VatNumberIneligible
            }
          }
          "the known facts and control list service returns KnownFactsInvalidVatNumber" should {
            "return InvalidVatNumber" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
              mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(KnownFactsInvalidVatNumber)))

              await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).left.value shouldBe InvalidVatNumber
            }
          }
          "the known facts and control list service returns ControlListInformationVatNumberNotFound" should {
            "return VatNumberNotFound" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
              mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(ControlListInformationVatNumberNotFound)))

              await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).left.value shouldBe VatNumberNotFound
            }
          }
          "the known facts and control list service returns any other error" should {
            "return KnownFactsAndControlListFailure" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))
              mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Left(UnexpectedKnownFactsAndControlListInformationFailure(Status.INTERNAL_SERVER_ERROR, ""))))

              await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).left.value shouldBe KnownFactsAndControlListFailure
            }
          }
        }
        "the MTDEligibilityCheck feature switch is disabled" should {
          "return VatNumberEligible" in {
            enable(AlreadySubscribedCheck)

            mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonMTDfB)))

            await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).right.value shouldBe VatNumberEligible
          }
        }
      }
      "the mandation status service returns NonDigital" when {
        "the MTDEligibilityCheck feature switch is enabled" when {
          "the known facts and control list service returns MtdEligible" should {
            "return VatNumberEligible" in {
              enable(AlreadySubscribedCheck)
              enable(MTDEligibilityCheck)

              mockGetMandationStatus(testVatNumber)(Future.successful(Right(NonDigital)))
              mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))

              await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).right.value shouldBe VatNumberEligible
            }
          }
        }
      }
      "the mandation status service returns VatNumberNotFound" should {
        "return AlreadySubscribed" in {
          enable(AlreadySubscribedCheck)
          enable(MTDEligibilityCheck)

          mockGetMandationStatus(testVatNumber)(Future.successful(Left(GetMandationStatusHttpParser.VatNumberNotFound)))
          mockGetKnownFactsAndControlListInformation(testVatNumber)(Future.successful(Right(MtdEligible(testPostCode, testDateOfRegistration))))

          await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).right.value shouldBe VatNumberEligible
        }
      }
      "the mandation status service returns MTDfBMandated" should {
        "return AlreadySubscribed" in {
          enable(AlreadySubscribedCheck)

          mockGetMandationStatus(testVatNumber)(Future.successful(Right(MTDfBMandated)))
          await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).left.value shouldBe AlreadySubscribed
        }
      }
      "the mandation status service returns MTDfBVoluntary" should {
        "return AlreadySubscribed" in {
          enable(AlreadySubscribedCheck)

          mockGetMandationStatus(testVatNumber)(Future.successful(Right(MTDfBVoluntary)))
          await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).left.value shouldBe AlreadySubscribed
        }
      }
      "the mandation status service returns any other error" should {
        "return GetVatCustomerInformationFailure" in {
          enable(AlreadySubscribedCheck)

          mockGetMandationStatus(testVatNumber)(Future.successful(Left(GetMandationStatusHttpFailure(Status.INTERNAL_SERVER_ERROR, ""))))
          await(TestVatNumberEligibilityService.checkVatNumberEligibility(testVatNumber)).left.value shouldBe GetVatCustomerInformationFailure
        }
      }
    }
  }
}
