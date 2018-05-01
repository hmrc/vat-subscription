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

package uk.gov.hmrc.vatsubscription.controllers

import play.api.http.Status._
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants.testVatNumber
import uk.gov.hmrc.vatsubscription.service.mocks.MockVatNumberEligibilityService
import uk.gov.hmrc.vatsubscription.services.VatNumberEligibilityService._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatNumberEligibilityControllerSpec extends UnitSpec with MockAuthConnector with MockVatNumberEligibilityService{

  object TestVatNumberEligibilityController extends VatNumberEligibilityController(mockAuthConnector, mockVatNumberEligibilityService)

  "checkVatNumberEligibility" when {
    "the service returns VAT number eligible" should {
      "return NO_CONTENT" in {
        mockAuthorise()(Future.successful(Unit))
        mockCheckVatNumberEligibility(testVatNumber)(Future.successful(Right(VatNumberEligible)))
        val res = TestVatNumberEligibilityController.checkVatNumberEligibility(testVatNumber)(FakeRequest())
        status(res) shouldBe NO_CONTENT
      }
    }
    "the service returns VAT number ineligible" should {
      "return BAD_REQUEST" in {
        mockAuthorise()(Future.successful(Unit))
        mockCheckVatNumberEligibility(testVatNumber)(Future.successful(Left(VatNumberIneligible)))
        val res = TestVatNumberEligibilityController.checkVatNumberEligibility(testVatNumber)(FakeRequest())
        status(res) shouldBe BAD_REQUEST
      }
    }
    "the service returns VAT Number not found" should {
      "return NOT_FOUND" in {
        mockAuthorise()(Future.successful(Unit))
        mockCheckVatNumberEligibility(testVatNumber)(Future.successful(Left(VatNumberNotFound)))
        val res = TestVatNumberEligibilityController.checkVatNumberEligibility(testVatNumber)(FakeRequest())
        status(res) shouldBe NOT_FOUND
      }
    }
    "the service returns InvalidVatNumber" should {
      "return NOT_FOUND" in {
        mockAuthorise()(Future.successful(Unit))
        mockCheckVatNumberEligibility(testVatNumber)(Future.successful(Left(InvalidVatNumber)))
        val res = TestVatNumberEligibilityController.checkVatNumberEligibility(testVatNumber)(FakeRequest())
        status(res) shouldBe NOT_FOUND
      }
    }

    "the service returns anything else" should {
      "return BAD_GATEWAY" in {
        mockAuthorise()(Future.successful(Unit))
        mockCheckVatNumberEligibility(testVatNumber)(Future.successful(Left(GetVatCustomerInformationFailure)))
        val res = TestVatNumberEligibilityController.checkVatNumberEligibility(testVatNumber)(FakeRequest())
        status(res) shouldBe BAD_GATEWAY
      }
    }
  }

}
