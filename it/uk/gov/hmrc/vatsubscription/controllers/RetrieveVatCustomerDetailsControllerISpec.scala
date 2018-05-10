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

class RetrieveVatCustomerDetailsControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val testSuccessDesResponse = {
    val testIndividualJson = Json.obj("title" -> "00001",
      "firstName" -> "testFirstName",
      "middleName" -> "testMiddleName",
      "lastName" -> "testLastName")

    Json.obj(
      "approvedInformation" -> Json.obj(
        "customerDetails" -> Json.obj("individual" -> testIndividualJson,
          "organisationName" -> "testOrganisationName",
          "tradingName" -> "testTradingName",
          "mandationStatus" -> "1"
        )
      )
    )
  }

  val expectedCustomerDetailsJson = Json.obj("organisationName" -> "testOrganisationName",
                                             "firstName" -> "testFirstName",
                                             "lastName" -> "testLastName",
                                             "tradingName" -> "testTradingName")


  "/:vatNumber/customer-details" when {
    "calls to DES is successful" should {
      "return OK with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(OK, testSuccessDesResponse)

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(OK),
          jsonBodyAs(expectedCustomerDetailsJson)
        )
      }
    }

    "calls to DES returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }

  }

}
