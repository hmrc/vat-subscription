/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.httpparsers

import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionReads
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.{ErrorModel, SuccessModel}

class UpdateVatSubscriptionHttpParserSpec extends UnitSpec {

  "UpdateVatSubscriptionReads .read" when {

    "an OK response is returned" when {

      "valid json is returned" should {

        val response: JsObject = Json.obj("formBundle" -> "12345")
        val result = UpdateVatSubscriptionReads.read("", "", HttpResponse(Status.OK, Some(response)))

        "return a SuccessModel" in {
          result shouldBe Right(SuccessModel(formBundle = "12345"))
        }
      }

      "invalid json is returned" should {

        val response: JsObject = Json.obj("form" -> "12345")
        val result = UpdateVatSubscriptionReads.read("", "", HttpResponse(Status.OK, Some(response)))

        "return an ErrorModel" in {
          result shouldBe Left(ErrorModel("INTERNAL_SERVER_ERROR", "Invalid Json returned in Success response."))
        }
      }
    }

    "a non-OK response is returned" when {

      "valid json is returned" should {

        val response: JsObject = Json.obj("code" -> "BAD_REQUEST", "reason" -> "REASON")
        val result = UpdateVatSubscriptionReads.read("", "", HttpResponse(Status.BAD_REQUEST, Some(response)))

        "return an ErrorModel" in {
          result shouldBe Left(ErrorModel("BAD_REQUEST", "REASON"))
        }
      }

      "invalid json is returned" should {

        val response: JsObject = Json.obj("" -> "")
        val result = UpdateVatSubscriptionReads.read("", "", HttpResponse(Status.BAD_REQUEST, Some(response)))

        "return an ErrorModel" in {
          result shouldBe Left(ErrorModel("INTERNAL_SERVER_ERROR", "Invalid Json returned in Error response. Status 400 returned"))
        }
      }
    }
  }
}
