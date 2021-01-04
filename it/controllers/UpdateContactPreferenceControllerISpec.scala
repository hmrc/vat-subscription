/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json.{JsValue, Json}
import helpers.{ComponentSpecBase, CustomMatchers}
import helpers.servicemocks.AuthStub.{mtdVatEnrolment, stubAuth, stubAuthFailure, successfulAuthResponse}
import helpers.IntegrationTestConstants.{testSuccessCustomerDetailsDesResponse, testVatNumber}
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, OK}
import helpers.servicemocks.GetVatCustomerInformationStub.stubGetInformation
import helpers.servicemocks.UpdateVatCustomerSubscriptionStub.stubUpdateSubscription

class UpdateContactPreferenceControllerISpec extends ComponentSpecBase with CustomMatchers {

  val validContactPreferenceJson: JsValue = Json.obj("commsPreference" -> "DIGITAL")

  val testSuccessDesResponse: JsValue = Json.obj("formBundle" -> "XAIT000000123456")
  val testErrorDesResponse: JsValue = Json.obj("code" -> "TEST", "reason" -> "ERROR")
  val testErrorResponse: JsValue = Json.obj("status" -> "TEST", "message" -> "ERROR")

  "PUT /vat-subsription/:vatNumber/contact-preference" when {

    "the user is unauthorised" should {
      "return FORBIDDEN" in {

        stubAuthFailure()

        val res = await(put(s"/$testVatNumber/contact-preference")(validContactPreferenceJson))

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "the user is authorised" when {

      "the call to DES is successful" should {

        "return OK with the status" in {

          stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
          stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)
          stubUpdateSubscription(testVatNumber)(OK, testSuccessDesResponse)

          val res = await(put(s"/$testVatNumber/contact-preference")(validContactPreferenceJson))

          res should have(
            httpStatus(OK),
            jsonBodyAs(testSuccessDesResponse)
          )
        }
      }

      "the call to DES returns an error" should {

        "return ISE with the error response" in {

          stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
          stubGetInformation(testVatNumber)(OK,testSuccessCustomerDetailsDesResponse)
          stubUpdateSubscription(testVatNumber)(BAD_REQUEST,  testErrorDesResponse)

          val res = await(put(s"/$testVatNumber/contact-preference")(validContactPreferenceJson))

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs(testErrorResponse)
          )
        }
      }
    }
  }
}
