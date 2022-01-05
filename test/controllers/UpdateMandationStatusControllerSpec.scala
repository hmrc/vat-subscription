/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.VatNumberNotFound
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants.testVatNumber
import helpers.TestUtil
import helpers.MandationStatusTestConstants._
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.api.http.Status._
import services.mocks.{MockUpdateMandationStatusService, MockVatCustomerDetailsRetrievalService}

import scala.concurrent.Future

class UpdateMandationStatusControllerSpec extends TestUtil
                                          with MockVatAuthorised
                                          with MockUpdateMandationStatusService
                                          with MockVatCustomerDetailsRetrievalService {

  object TestController
    extends UpdateMandationStatusController(
      mockVatAuthorised,
      mockUpdateMandationStatusService,
      mockVatCustomerDetailsRetrievalService,
      controllerComponents
    )(ec)

  "UpdateMandationStatusController .updateMandationStatus" when {

    "the mandation status POST is correctly parsed" when {

      lazy val requestWithValidJson: FakeRequest[AnyContentAsJson] =
        FakeRequest().withJsonBody(mandationStatusPostJson)

      "the customer details service call is successful" when {

        "the update mandation status service is successful" should {

          lazy val result = {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future.successful(Right(true)))
            mockUpdateMandationStatus(mandationStatusPost)(Future.successful(Right(SuccessModel(""))))
            TestController.updateMandationStatus(testVatNumber)(requestWithValidJson)
          }
          "return OK" in {
            status(result) shouldBe OK
          }
          "return a JSON body" in {
            contentAsJson(result) shouldBe Json.toJson(SuccessModel(""))
          }
        }

        "the update mandation status service returns a CONFLICT response" should {
          lazy val result = {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future.successful(Right(true)))
            mockUpdateMandationStatus(mandationStatusPost)(Future.successful(Left(ErrorModel("CONFLICT", "conflict!"))))
            TestController.updateMandationStatus(testVatNumber)(requestWithValidJson)
          }
          "return a CONFLICT (409)" in {
            status(result) shouldBe CONFLICT
          }
          "return a JSON error model" in {
            contentAsJson(result) shouldBe Json.toJson(ErrorModel("CONFLICT", "conflict!"))
          }
        }

        "the update mandation status service returns another error" should {
          lazy val result = {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future.successful(Right(true)))
            mockUpdateMandationStatus(mandationStatusPost)(Future.successful(Left(ErrorModel("", ""))))
            TestController.updateMandationStatus(testVatNumber)(requestWithValidJson)
          }
          "return ISE (500)" in {
            status(result) shouldBe INTERNAL_SERVER_ERROR
          }
          "return a JSON error model" in {
            contentAsJson(result) shouldBe Json.toJson(ErrorModel("", ""))
          }
        }
      }

      "the customer details service call fails" should {
        lazy val result = {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future.successful(Left(VatNumberNotFound)))
          TestController.updateMandationStatus(testVatNumber)(requestWithValidJson)
        }
        "return ISE (500)" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }

        "return error JSON" in {
          contentAsJson(result) shouldBe Json.toJson(VatNumberNotFound)
        }
      }
    }

    "the mandation status POST cannot be parsed" should {
      lazy val result = {
        mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
        TestController.updateMandationStatus(testVatNumber)(FakeRequest().withJsonBody(Json.toJson("{}")))
      }
      "return Bad Request (400)" in {
        status(result) shouldBe BAD_REQUEST
      }

      "return error JSON" in {
        contentAsJson(result) shouldBe Json.toJson(ErrorModel("INVALID_JSON", s"Json received, but did not validate"))
      }
    }
  }
}
