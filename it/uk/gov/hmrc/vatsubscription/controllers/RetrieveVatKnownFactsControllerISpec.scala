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

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.GetVatCustomerInformationStub._
import uk.gov.hmrc.vatsubscription.helpers.{ComponentSpecBase, CustomMatchers}

class RetrieveVatKnownFactsControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val testMinDesResponse = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "mandationStatus" -> "1")
    )
  )

  val testSuccessDesResponse = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "mandationStatus" -> "1",
        "effectiveRegistrationDate" -> effectiveDate
      ),
      "PPOB" -> Json.obj(
        "address" -> Json.obj(
          "line1" -> addLine1,
          "postCode" -> postcode,
          "countryCode" -> countryCode
        )
      )
    )
  )

  "GET /:vatNumber/known-facts" when {
    "calls to DES is successful" when {
      "both vat registration date and post code are present" should {
        "return OK with the status" in {
          stubAuth(OK, successfulAuthResponse())
          stubGetInformation(testVatNumber)(OK, testSuccessDesResponse)

          val res = await(get(s"/$testVatNumber/known-facts"))

          res should have(
            httpStatus(OK),
            jsonBodyAs(Json.obj(
              "vatRegistrationDate" -> effectiveDate,
              "businessPostCode" -> postcode
            ))
          )
        }
      }
      "known facts are missing" should {
        "return BAD_GATEWAY with the status" in {
          stubAuth(OK, successfulAuthResponse())
          stubGetInformation(testVatNumber)(OK, testMinDesResponse)

          val res = await(get(s"/$testVatNumber/known-facts"))

          res should have(
            httpStatus(BAD_GATEWAY)
          )
        }
      }
    }
    "DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = await(get(s"/$testVatNumber/known-facts"))

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }
    "DES returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = await(get(s"/$testVatNumber/known-facts"))

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }
    "DES returned INTERNAL_SERVER_ERROR" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/known-facts"))

        res should have(
          httpStatus(BAD_GATEWAY),
          jsonBodyAs(Json.obj(
            "status" -> INTERNAL_SERVER_ERROR,
            "body" -> "{}"
          ))
        )
      }
    }
  }

}
