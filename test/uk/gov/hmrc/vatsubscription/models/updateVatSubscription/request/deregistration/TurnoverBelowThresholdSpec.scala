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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.TurnoverBelowThresholdTestConstants._

class TurnoverBelowThresholdSpec extends UnitSpec {

  "TurnoverBelowThreshold" when {

    "deserializing JSON" should {

      "return correct TurnoverBelowThresholdModel when valid JSON max is received" in {
        turnoverBelowThresholdFrontendJsonMax.as[TurnoverBelowThreshold] shouldBe turnoverBelowThresholdModelMax
      }

      "return correct TurnoverBelowThresholdModel when valid JSON min is received" in {
        turnoverBelowThresholdFrontendJsonMin.as[TurnoverBelowThreshold] shouldBe turnoverBelowThresholdModelMin
      }

      "return JsError when invalid JSON is received" in {
        JsString("banana").validate[TurnoverBelowThreshold].isError shouldBe true
      }
    }

    "serializing to JSON" should {

      "for turnoverBelowThresholdModelMax output correct JSON" in {
        Json.toJson(turnoverBelowThresholdModelMax) shouldBe turnoverBelowThresholdDESJsonMax
      }

      "for turnoverBelowThresholdModelMin output correct JSON" in {
        Json.toJson(turnoverBelowThresholdModelMin) shouldBe turnoverBelowThresholdDESJsonMin
      }
    }
  }
}
