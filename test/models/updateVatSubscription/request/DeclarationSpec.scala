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

package models.updateVatSubscription.request

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec
import helpers.DeclarationTestConstants._

class DeclarationSpec extends UnitSpec {

  "Declaration" when {

    "serializing to JSON" should {

      "Output the correct JSON for an Agent" in {
        Json.toJson(declarationModelAgent) shouldBe declarationDESJsonAgent
      }

      "Output the correct JSON for an Non-Agent" in {
        Json.toJson(declarationModelNonAgent) shouldBe declarationDESJsonlNonAgent
      }
    }
  }
}
