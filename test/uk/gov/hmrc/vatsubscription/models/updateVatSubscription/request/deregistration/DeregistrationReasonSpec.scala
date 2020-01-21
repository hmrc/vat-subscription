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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.play.test.UnitSpec

class DeregistrationReasonSpec extends UnitSpec {

  "DeregistrationReason" when {

    "deserializing JSON" should {

      "return CeasedTrading case object for 'ceased'" in {
        JsString("ceased").as[DeregistrationReason] shouldBe CeasedTrading
      }

      "return ReducedTurnover case object for 'belowThreshold'" in {
        JsString("belowThreshold").as[DeregistrationReason] shouldBe ReducedTurnover
      }

      "return ZeroRated case object for 'zeroRated'" in {
        JsString("zeroRated").as[DeregistrationReason] shouldBe ZeroRated
      }

      "return ExemptOnly case object for 'exemptOnly'" in {
        JsString("exemptOnly").as[DeregistrationReason] shouldBe ExemptOnly
      }

      "return JsError invalid" in {
        JsString("banana").validate[DeregistrationReason].isError shouldBe true
      }
    }

    "serializing to JSON" should {

      "for CeasedTrading output '0003'" in {
        Json.toJson(CeasedTrading) shouldBe JsString("0003")
      }

      "for ReducedTurnover output '0010'" in {
        Json.toJson(ReducedTurnover) shouldBe JsString("0010")
      }

      "for ZeroRated output '0006'" in {
        Json.toJson(ZeroRated) shouldBe JsString("0006")
      }

      "for ExemptOnly output '0014'" in {
        Json.toJson(ExemptOnly) shouldBe JsString("0014")
      }
    }
  }
}
