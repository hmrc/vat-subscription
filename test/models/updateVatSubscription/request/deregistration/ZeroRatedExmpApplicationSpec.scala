/*
 * Copyright 2024 HM Revenue & Customs
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

package models.updateVatSubscription.request.deregistration

import play.api.libs.json.{JsString, Json}
import helpers.ZeroRatedExmpApplicationConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ZeroRatedExmpApplicationSpec extends AnyWordSpecLike with Matchers {

  "ZeroRatedExmpApplication" when {

    "deserializing JSON" should {

      "return correct ZeroRatedExmpApplication when valid JSON max is recieved" in {
        zeroRatedExmpApplicationFrontendJsonMax.as[ZeroRatedExmpApplication] shouldBe zeroRatedExmpApplicationFrontendModelMax
      }

      "return correct ZeroRatedExmpApplication when valid JSON min is recieved" in {
        zeroRatedExmpApplicationFrontendJsonMin.as[ZeroRatedExmpApplication] shouldBe zeroRatedExmpApplicationFrontendModelMin
      }

      "return JsError when invalid JSON is received" in {
        JsString("banana").validate[ZeroRatedExmpApplication].isError shouldBe true
      }

    }

    "serializing to JSON" should {

      "for turnoverBelowThresholdModelMax output correct JSON" in {
        Json.toJson(zeroRatedExmpApplicationFrontendModelMax) shouldBe zeroRatedExmpApplicationFrontendJsonMax
      }

      "for turnoverBelowThresholdModelMin output correct JSON" in {
        Json.toJson(zeroRatedExmpApplicationFrontendModelMin) shouldBe zeroRatedExmpApplicationFrontendJsonMin
      }
    }

  }

}
