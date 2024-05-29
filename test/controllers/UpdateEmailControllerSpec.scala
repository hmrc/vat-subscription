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
import helpers.PPOBTestConstants.{ppobModelEmailMaxPost, ppobModelMax}
import helpers.TestUtil
import helpers.UpdateVatSubscriptionTestConstants.{updateConflictResponse, updateErrorResponse, updateSuccessResponse}
import models.updateVatSubscription.response.ErrorModel
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import services.mocks.{MockUpdateEmailService, MockVatCustomerDetailsRetrievalService}
import scala.concurrent.Future

class UpdateEmailControllerSpec extends TestUtil with MockVatAuthorised with MockUpdateEmailService
  with MockVatCustomerDetailsRetrievalService {

  object TestUpdateEmailController
    extends UpdateEmailAddressController(mockVatAuthorised,
                                        mockUpdateEmailService,
                                        mockVatCustomerDetailsRetrievalService,
                                        controllerComponents)

  val ppobPostRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(ppobModelMax))

  "the.updateEmail() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        val res: Future[Result] = TestUpdateEmailController.updateEmail(testVatNumber)(ppobPostRequest)
        status(res) shouldBe FORBIDDEN
      }
    }

    "The user is authorised" should {

      "return a successful response" when {

        "a valid EmailPost is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateEmail(ppobModelEmailMaxPost)(Future.successful(Right(updateSuccessResponse)))

          val res: Future[Result] = TestUpdateEmailController.updateEmail(testVatNumber)(ppobPostRequest)

          status(res) shouldBe OK
          contentAsJson(res) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "no json body is supplied for the PUT" should {

          val emptyUpdateEmailRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
          lazy val res: Future[Result] = TestUpdateEmailController.updateEmail(testVatNumber)(emptyUpdateEmailRequest)

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe
              Json.toJson(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${emptyUpdateEmailRequest.body}"))
          }
        }

        "a valid EmailPost is supplied but a Conflict error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Future[Result] = TestUpdateEmailController.updateEmail(testVatNumber)(ppobPostRequest)

          "return status CONFLICT (409)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
            mockUpdateEmail(ppobModelEmailMaxPost)(Future.successful(Left(updateConflictResponse)))
            status(res) shouldBe CONFLICT
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe Json.toJson(updateConflictResponse)
          }
        }

        "a valid EmailPost is supplied but an unexpected error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Future[Result] = TestUpdateEmailController.updateEmail(testVatNumber)(ppobPostRequest)

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
            mockUpdateEmail(ppobModelEmailMaxPost)(Future.successful(Left(updateErrorResponse)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe Json.toJson(updateErrorResponse)
          }
        }


        "a valid EmailPost is supplied but an error is returned instead of a welsh indicator" should {

          lazy val res: Future[Result] = TestUpdateEmailController.updateEmail(testVatNumber)(ppobPostRequest)

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Left(VatNumberNotFound)))
            mockUpdateEmail(ppobModelEmailMaxPost)(Future.successful(Left(updateErrorResponse)))
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
