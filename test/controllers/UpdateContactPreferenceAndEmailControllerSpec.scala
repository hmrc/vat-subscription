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

package controllers

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import assets.TestUtil
import connectors.UnexpectedGetVatCustomerInformationFailure
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants.testVatNumber
import helpers.CustomerInformationTestConstants.customerInformationModelMax
import helpers.UpdateVatSubscriptionTestConstants.{updateErrorResponse, updateSuccessResponse}
import service.mocks.{MockUpdateContactPreferenceService, MockVatCustomerDetailsRetrievalService}

import scala.concurrent.Future

class UpdateContactPreferenceAndEmailControllerSpec extends TestUtil
                                                    with MockVatAuthorised
                                                    with MockUpdateContactPreferenceService
                                                    with MockVatCustomerDetailsRetrievalService {

  object TestController extends UpdateContactPreferenceAndEmailController(
    mockVatAuthorised,
    mockUpdateContactPreferenceService,
    mockVatCustomerDetailsRetrievalService,
    controllerComponents
  )

  val request: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.obj("emailAddress" -> "email@email.com"))

  "the updateContactPreferences() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        lazy val result = await(TestController.update(testVatNumber)(FakeRequest()))

        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is authorised" when {

      "JSON body of request is valid" when {

        "call to retrieve current details is successful" when {

          "updating preference and email is successful" should {

            "return OK" in {
              mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
              mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMax)))
              mockUpdatePreferenceAndEmail()(Future.successful(Right(updateSuccessResponse)))

              lazy val result = await(TestController.update(testVatNumber)(request))

              status(result) shouldBe OK
              jsonBodyOf(result) shouldBe Json.toJson(updateSuccessResponse)
            }
          }

          "updating preference and email is unsuccessful" should {

            "return INTERNAL_SERVER_ERROR" in {

              mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
              mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMax)))
              mockUpdatePreferenceAndEmail()(Future.successful(Left(updateErrorResponse)))

              lazy val result = await(TestController.update(testVatNumber)(request))

              status(result) shouldBe INTERNAL_SERVER_ERROR
              jsonBodyOf(result) shouldBe Json.toJson(updateErrorResponse)
            }
          }
        }

        "call to retrieve current details is unsuccessful" should {

          "return INTERNAL_SERVER_ERROR" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, ""))))

            lazy val result = await(TestController.update(testVatNumber)(request))

            status(result) shouldBe INTERNAL_SERVER_ERROR
            jsonBodyOf(result) shouldBe Json.obj("status" -> "500", "body" -> "")
          }
        }
      }

      "JSON body of request is invalid" should {

        "return BAD_REQUEST" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

          lazy val result = await(TestController.update(testVatNumber)(
            FakeRequest().withJsonBody(Json.obj("blah" -> "blah")))
          )

          status(result) shouldBe BAD_REQUEST
          jsonBodyOf(result) shouldBe Json.obj("status" -> "INVALID_JSON", "message" -> "Json received, but did not validate")
        }
      }
    }
  }
}
