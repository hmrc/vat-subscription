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
import uk.gov.hmrc.vatsubscription.models.MTDfBMandated

class GetMandationStatusControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val testSuccessResponse = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj("mandationStatus" -> "1"),
      "PPOB" -> Json.obj(
        "address" -> Json.obj(
          "line1" -> addLine1,
          "line2" -> addLine2,
          "line3" -> addLine3,
          "line4" -> addLine4,
          "line5" -> addLine5,
          "postCode" -> postcode,
          "countryCode" -> countryCode
        )
      )
    )
  )

  val testMigrationResponse = Json.obj(
    "code" -> "MIGRATION"
  )

  "/:vatNumber/mandation-status " when {
    "calls to DES is successful" should {
      "return OK with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(OK, testSuccessResponse)

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(OK),
          jsonBodyAs(Json.obj("mandationStatus" -> MTDfBMandated))
        )
      }
    }

    "calls to DES returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returned FORBIDDEN with MIGRATION code" should {
      "return PRECONDITION_FAILED with the status 412" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(FORBIDDEN, testMigrationResponse)

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(PRECONDITION_FAILED)
        )
      }
    }

    "calls to DES returned FORBIDDEN with no body" should {
      "return FORBIDDEN with the status 403" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(FORBIDDEN, Json.obj())

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to DES returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }

  }
}
