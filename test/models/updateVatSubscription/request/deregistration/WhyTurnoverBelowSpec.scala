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

package models.updateVatSubscription.request.deregistration

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.play.test.UnitSpec
import helpers.WhyTurnoverBelowTestConstants._

class WhyTurnoverBelowSpec extends UnitSpec {

  "WhyTurnoverBelow" when {

    "deserializing JSON" should {

      "return correct WhyTurnoverBelowModel when valid JSON is received" in {
        whyTurnoverBelowJsonMax.as[WhyTurnoverBelow] shouldBe whyTurnoverBelowModelMax
      }

      "return JsError when invalid JSON is received" in {
        JsString("banana").validate[WhyTurnoverBelow].isError shouldBe true
      }
    }

    "serializing to JSON" should {

      "for whyTurnoverBelowModel output correct String" in {
        Json.toJson(whyTurnoverBelowModelMax) shouldBe JsString(whyTurnoverBelowModelMax.toString)
      }
    }

    "calling the .whyTurnoverBelowString" should {

      "return all reasons as a correctly formatted string" in {

        whyTurnoverBelowModelMax.toString shouldBe whyTurnoverBelowStringMax
        TurnoverAlreadyBelow.toString shouldBe whyTurnoverBelowStringAlreadyBelow
      }
    }
  }
}
