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

package controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import helpers.IntegrationTestConstants._
import helpers.servicemocks.AuthStub._
import helpers.servicemocks.GetVatCustomerInformationStub._
import helpers.{ComponentSpecBase, CustomMatchers}

class RetrieveVatKnownFactsControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val migrationResponse: JsObject = Json.obj(
    "code" -> "MIGRATION"
  )

  val testMinDesResponse: JsObject = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "mandationStatus" -> "1")
    )
  )

  def testSuccessDesResponse(isOverseas: Boolean, isDeregistered: Boolean = false): JsObject = {

    val approvedInformationJson = Json.obj(
      "customerDetails" -> Json.obj(
        "mandationStatus" -> "1",
        "effectiveRegistrationDate" -> effectiveDate,
        "overseasIndicator" -> isOverseas
      ),
      "PPOB" -> (if (isOverseas) {
        Json.obj(
          "address" -> Json.obj(
            "line1" -> addLine1,
            "countryCode" -> countryCode
          )
        )
      } else {
        Json.obj(
          "address" -> Json.obj(
            "line1" -> addLine1,
            "postCode" -> postcode,
            "countryCode" -> countryCode
          )
        )
      }),
      "businessActivities" -> Json.obj(
        "primaryMainCode" -> "00001",
        "mainCode2" -> "00002"
      )
    )

    Json.obj(
      "approvedInformation" -> {
        if (isDeregistered)
          approvedInformationJson ++ Json.obj("deregistration" -> Json.obj())
        else
          approvedInformationJson
      }
    )

  }

  "GET /:vatNumber/known-facts" when {
    "calls to DES is successful" when {
      "the vat number is deregistered" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(OK, testSuccessDesResponse(isOverseas = false, isDeregistered = true))

        val res = await(get(s"/$testVatNumber/known-facts"))

        res should have(
          httpStatus(OK),
          jsonBodyAs(Json.obj("deregistered" -> true))
        )
      }

      "both vat registration date and post code are present" when {
        "the overseas indicator is set to false" should {
          "return OK with the known facts and overseas set to false" in {
            stubAuth(OK, successfulAuthResponse())
            stubGetInformation(testVatNumber)(OK, testSuccessDesResponse(isOverseas = false))

            val res = await(get(s"/$testVatNumber/known-facts"))

            res should have(
              httpStatus(OK),
              jsonBodyAs(Json.obj(
                "vatRegistrationDate" -> effectiveDate,
                "businessPostCode" -> postcode,
                "isOverseas" -> false
              ))
            )
          }
        }
      }

      "vat registration date is present but post code is not" when {
        "the overseas indicator is set to true" should {
          "return OK with the status the known facts and overseas set to true" in {
            stubAuth(OK, successfulAuthResponse())
            stubGetInformation(testVatNumber)(OK, testSuccessDesResponse(isOverseas = true))

            val res = await(get(s"/$testVatNumber/known-facts"))

            res should have(
              httpStatus(OK),
              jsonBodyAs(Json.obj(
                "vatRegistrationDate" -> effectiveDate,
                "isOverseas" -> true
              ))
            )
          }
        }
      }

      "known facts are missing" should {
        "return BAD_GATEWAY with the status" in {
          stubAuth(OK, successfulAuthResponse())
          stubGetInformation(testVatNumber)(BAD_GATEWAY, testMinDesResponse)

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

    "DES returned FORBIDDEN with MIGRATION code" should {
      "return PRECONDITION_FAILED with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(FORBIDDEN, migrationResponse)

        val res = await(get(s"/$testVatNumber/known-facts"))

        res should have(
          httpStatus(PRECONDITION_FAILED)
        )
      }
    }

    "DES returned FORBIDDEN with no body" should {
      "return FORBIDDEN with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(FORBIDDEN, Json.obj())

        val res = await(get(s"/$testVatNumber/known-facts"))

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "DES returned INTERNAL_SERVER_ERROR" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/known-facts"))

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR),
          jsonBodyAs(Json.obj(
            "status" -> INTERNAL_SERVER_ERROR,
            "body" -> "{}"
          ))
        )
      }
    }
  }

}
