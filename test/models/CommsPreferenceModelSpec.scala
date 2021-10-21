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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.JsString

class CommsPreferenceModelSpec extends AnyWordSpecLike with Matchers {

  "CommsPreference" should {

    "correctly parse from json for DigitalPreference" in {
      JsString(DigitalPreference.desValue).as[CommsPreference] shouldBe DigitalPreference
    }

    "correctly parse from json for PaperPreference" in {
      JsString(PaperPreference.desValue).as[CommsPreference] shouldBe PaperPreference
    }
  }

}
