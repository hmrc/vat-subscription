/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsJson, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import connectors.VatNumberNotFound
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants.testVatNumber
import helpers.{DeregistrationInfoTestConstants, TestUtil}
import helpers.UpdateVatSubscriptionTestConstants.{updateErrorResponse, updateSuccessResponse}
import models.updateVatSubscription.response.ErrorModel
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import services.mocks.{MockDeregistrationRequestService, MockVatCustomerDetailsRetrievalService}
import scala.concurrent.Future

class RequestDeregistrationControllerSpec extends TestUtil with MockVatAuthorised with MockDeregistrationRequestService
  with MockVatCustomerDetailsRetrievalService {

  object TestRequestDeregistrationController
    extends RequestDeregistrationController(mockVatAuthorised,
                                            mockDeregistrationRequestService,
                                            mockVatCustomerDetailsRetrievalService,
                                            controllerComponents)

  val deregRequest: FakeRequest[AnyContentAsJson] =
    FakeRequest().withJsonBody(DeregistrationInfoTestConstants.deregInfoCeasedTradingFrontendJson)

  "the .deregister() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        val res: Future[Result] = TestRequestDeregistrationController.deregister(testVatNumber)(deregRequest)
        status(res) shouldBe FORBIDDEN
      }
    }

    "The user is authorised" should {

      "return a successful response" when {

        "a valid Deregistration Model is received" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future.successful(Right(false)))
          mockDeregister(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel)(Future.successful(Right(updateSuccessResponse)))

          val res: Future[Result] = TestRequestDeregistrationController.deregister(testVatNumber)(deregRequest)

          status(res) shouldBe OK
          contentAsJson(res) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "no json body is supplied for the PUT" should {

          val unknownReturnPeriodRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
          lazy val res: Future[Result] = TestRequestDeregistrationController.deregister(testVatNumber)(unknownReturnPeriodRequest)

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe
              Json.toJson(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${unknownReturnPeriodRequest.body}"))
          }
        }

        "a valid return period is supplied but an error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Future[Result] = TestRequestDeregistrationController.deregister(testVatNumber)(deregRequest)

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future.successful(Right(false)))
            mockDeregister(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel)(Future.successful(Left(updateErrorResponse)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe Json.toJson(updateErrorResponse)
          }
        }

        "a valid return period is supplied but an error is returned instead of a welshIndicator" should {

          lazy val res: Future[Result] = TestRequestDeregistrationController.deregister(testVatNumber)(deregRequest)

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future.successful(Left(VatNumberNotFound)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe Json.toJson(VatNumberNotFound)
          }
        }
      }
    }
  }
}
