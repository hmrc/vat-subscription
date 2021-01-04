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

package models

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.play.test.UnitSpec

class CommsPreferenceModelSpec extends UnitSpec {

  "CommsPreferenceModel" should {

    "correctly parse from json for DigitalPreference" in {
      JsString(DigitalPreference.desValue).as[CommsPreference] shouldBe DigitalPreference
    }

    "correctly parse from json for PaperPreference" in {
      JsString(PaperPreference.desValue).as[CommsPreference] shouldBe PaperPreference
    }

    "correctly parse to json for DigitalPreference to frontends" in {
      Json.toJson(DigitalPreference)(CommsPreference.writes) shouldBe JsString(DigitalPreference.mdtpValue)
    }

    "correctly parse to json for PaperPreference to frontends" in {
      Json.toJson(PaperPreference)(CommsPreference.writes) shouldBe JsString(PaperPreference.mdtpValue)
    }

    "correctly parse to json for DigitalPreference to DES" in {
      Json.toJson(DigitalPreference)(CommsPreference.updatePreferenceWrites) shouldBe JsString(DigitalPreference.desValue)
    }

    "correctly parse to json for PaperPreference to DES" in {
      Json.toJson(PaperPreference)(CommsPreference.updatePreferenceWrites) shouldBe JsString(PaperPreference.desValue)
    }

  }

}
