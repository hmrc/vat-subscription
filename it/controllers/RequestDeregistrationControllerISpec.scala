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

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsValue, Json}
import helpers.IntegrationTestConstants.{testSuccessCustomerDetailsDesResponse, testVatNumber}
import helpers.servicemocks.AuthStub.{mtdVatEnrolment, stubAuth, stubAuthFailure, successfulAuthResponse}
import helpers.servicemocks.GetVatCustomerInformationStub.stubGetInformation
import helpers.servicemocks.UpdateVatCustomerSubscriptionStub.stubUpdateSubscription
import helpers.{ComponentSpecBase, CustomMatchers}
import models.updateVatSubscription.request.deregistration.CeasedTrading

class RequestDeregistrationControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val validDeregistrationRequestJson: JsValue = Json.obj(
    "deregReason" -> CeasedTrading.value,
    "deregDate" -> "2018-02-02",
    "deregLaterDate" -> "2018-02-04",
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> 400,
    "stocksValue" -> 600,
    "capitalAssetsValue" -> 10.99
  )

  val testSuccessDesResponse: JsValue = Json.obj("formBundle" -> "XAIT000000123456")
  val testErrorDesResponse: JsValue = Json.obj("code" -> "TEST", "reason" -> "ERROR")
  val testErrorResponse: JsValue = Json.obj("status" -> "TEST", "message" -> "ERROR")

  "PUT /vat-subscription/:vatNumber/deregister" when {

    "the user is unauthorised" should {
      "return FORBIDDEN" in {

        stubAuthFailure()

        val res = await(put(s"/$testVatNumber/deregister")(validDeregistrationRequestJson))

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "the user is authorised" should {

      "calls to DES is successful" should {

        "return OK with the status" in {

          stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
          stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)
          stubUpdateSubscription(testVatNumber)(OK, testSuccessDesResponse)

          val res = await(put(s"/$testVatNumber/deregister")(validDeregistrationRequestJson))

          res should have(
            httpStatus(OK),
            jsonBodyAs(testSuccessDesResponse)
          )
        }
      }

      "calls to DES return an error" should {

        "return ISE with the error response" in {

          stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
          stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)
          stubUpdateSubscription(testVatNumber)(BAD_REQUEST, testErrorDesResponse)

          val res = await(put(s"/$testVatNumber/deregister")(validDeregistrationRequestJson))

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs(testErrorResponse)
          )
        }
      }
    }
  }

}
