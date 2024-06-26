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
import play.api.mvc.{AnyContentAsJson, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import connectors.VatNumberNotFound
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants.testVatNumber
import helpers.TestUtil
import helpers.UpdateVatSubscriptionTestConstants.{updateErrorResponse, updateSuccessResponse}
import models.post.CommsPreferencePost
import models.updateVatSubscription.response.ErrorModel
import models.{DigitalPreference, PaperPreference}
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import services.mocks.{MockUpdateContactPreferenceService, MockVatCustomerDetailsRetrievalService}
import scala.concurrent.Future

class UpdateContactPreferenceControllerSpec extends TestUtil with MockVatAuthorised
  with MockUpdateContactPreferenceService with MockVatCustomerDetailsRetrievalService {

  object TestCommsPreference extends UpdateContactPreferenceController(
    mockVatAuthorised,
    mockUpdateContactPreferenceService,
    mockVatCustomerDetailsRetrievalService,
    controllerComponents)

  val paperPref: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.obj("commsPreference" -> "PAPER"))
  val digitalPref: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.obj("commsPreference" -> "DIGITAL"))
  val invalidPref: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.obj("commsPreference" -> "FAX"))

  "the updateContactPreferences() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {

        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        val res: Future[Result] = TestCommsPreference.updateContactPreference(testVatNumber)(paperPref)
        status(res) shouldBe FORBIDDEN
      }
    }

    "the user is authorised" should {

      "return a successful response" when {

        "Paper Preference is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateContactPreference(CommsPreferencePost(PaperPreference))(Future.successful(Right(updateSuccessResponse)))

          val res: Future[Result] = TestCommsPreference.updateContactPreference(testVatNumber)(paperPref)

          status(res) shouldBe OK
          contentAsJson(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "Digital Preference is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateContactPreference(CommsPreferencePost(DigitalPreference))(Future.successful(Right(updateSuccessResponse)))

          val res: Future[Result] = TestCommsPreference.updateContactPreference(testVatNumber)(digitalPref)

          status(res) shouldBe OK
          contentAsJson(res) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "no JSON body is supplied" should {

          lazy val res: Future[Result] = TestCommsPreference.updateContactPreference(testVatNumber)(fakeRequest)

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe
              Json.toJson(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${fakeRequest.body}"))
          }
        }

        "an invalid JSON body is supplied" should {

          lazy val res: Future[Result] = TestCommsPreference.updateContactPreference(testVatNumber)(invalidPref)

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe
              Json.toJson(ErrorModel("INVALID_JSON", s"Json received, but did not validate"))
          }
        }

        "a valid result is supplied but an error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Future[Result] = TestCommsPreference.updateContactPreference(testVatNumber)(paperPref)

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
            mockUpdateContactPreference(CommsPreferencePost(PaperPreference))(Future.successful(Left(updateErrorResponse)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(res) shouldBe Json.toJson(updateErrorResponse)
          }
        }

        "a valid result is supplied but an error is returned instead of a welshIndicator" should {

          lazy val res: Future[Result] = TestCommsPreference.updateContactPreference(testVatNumber)(paperPref)

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Left(VatNumberNotFound)))
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
