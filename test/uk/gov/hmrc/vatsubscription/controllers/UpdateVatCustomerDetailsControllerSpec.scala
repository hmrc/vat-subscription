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

import assets.TestUtil
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, AnyContentAsEmpty, AnyContentAsJson, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import uk.gov.hmrc.vatsubscription.controllers.actions.mocks.MockVatAuthorised
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.ReturnPeriodTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants.{updateErrorResponse, updateSuccessResponse}
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.ErrorModel
import uk.gov.hmrc.vatsubscription.models._
import uk.gov.hmrc.vatsubscription.service.mocks.MockUpdateVatsubscriptionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateVatCustomerDetailsControllerSpec extends TestUtil with MockVatAuthorised with MockUpdateVatsubscriptionService {

  object TestUpdateVatCustomerDetailsController
    extends UpdateVatCustomerDetailsController(mockVatAuthorised, mockUpdateVatSubscriptionService)

  val maRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MAReturnPeriod))
  val mbRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MBReturnPeriod))
  val mcRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MCReturnPeriod))
  val mmRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MMReturnPeriod))

  "the.updateVatReturnPeriod() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, allEnrolments)(Future.failed(InsufficientEnrolments()))
        val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(maRequest))
        status(res) shouldBe FORBIDDEN
      }
    }

    "The user is authorised" should {

      "return a successful response" when {

        "the 'MA' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockUpdateReturnPeriod(MAReturnPeriod)(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(maRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "the 'MB' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockUpdateReturnPeriod(MBReturnPeriod)(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(mbRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "the 'MC' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockUpdateReturnPeriod(MCReturnPeriod)(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(mcRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "the 'MM' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockUpdateReturnPeriod(MMReturnPeriod)(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(mmRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "an unknown return period is supplied" should {

          val unknownReturnPeriodRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(InvalidReturnPeriod))
          lazy val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(unknownReturnPeriodRequest))

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(ErrorModel("RETURN_PERIOD_ERROR", "Invalid Json or Invalid Return Period supplied"))
          }
        }

        "no json body is supplied for the PUT" should {

          val unknownReturnPeriodRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
          lazy val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(unknownReturnPeriodRequest))

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(ErrorModel("RETURN_PERIOD_ERROR", "Invalid Json or Invalid Return Period supplied"))
          }
        }

        "a valid return period is supplied but an error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Result = await(TestUpdateVatCustomerDetailsController.updateVatReturnPeriod(testVatNumber)(maRequest))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockUpdateReturnPeriod(MAReturnPeriod)(Future.successful(Left(updateErrorResponse)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(updateErrorResponse)
          }
        }
      }
    }
  }
}