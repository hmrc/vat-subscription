/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsJson, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import uk.gov.hmrc.vatsubscription.assets.TestUtil
import uk.gov.hmrc.vatsubscription.connectors.VatNumberNotFound
import uk.gov.hmrc.vatsubscription.controllers.actions.mocks.MockVatAuthorised
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants.testVatNumber
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants.{updateErrorResponse, updateSuccessResponse}
import uk.gov.hmrc.vatsubscription.models.post.CommsPreferencePost
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.ErrorModel
import uk.gov.hmrc.vatsubscription.models.{DigitalPreference, PaperPreference}
import uk.gov.hmrc.vatsubscription.service.mocks.{MockUpdateContactPreferenceService, MockVatCustomerDetailsRetrievalService}

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
        val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(paperPref))
        status(res) shouldBe FORBIDDEN
      }
    }

    "the user is authorised" should {

      "return a successful response" when {

        "Paper Preference is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateContactPreference(CommsPreferencePost(PaperPreference))(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(paperPref))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "Digital Preference is supplied ant the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateContactPreference(CommsPreferencePost(DigitalPreference))(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(digitalPref))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "no JSON body is supplied" should {

          lazy val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(fakeRequest))

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe
              Json.toJson(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${fakeRequest.body}"))
          }
        }

        "an invalid JSON body is supplied" should {

          lazy val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(invalidPref))

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe
              Json.toJson(ErrorModel("INVALID_JSON", s"Json received, but did not validate"))
          }
        }

        "a valid result is supplied but an error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(paperPref))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
            mockUpdateContactPreference(CommsPreferencePost(PaperPreference))(Future.successful(Left(updateErrorResponse)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(updateErrorResponse)
          }
        }

        "a valid result is supplied but an error is returned instead of a welshIndicator" should {

          lazy val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(paperPref))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Left(VatNumberNotFound)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(VatNumberNotFound)
          }
        }
      }
    }
  }
}
