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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants.agentOrCapacitor

class DeclarationSpec extends UnitSpec {

  "Declaration Writes" when {

    "supplied with an agentOrCapacitor model" should {

      val model: Declaration = Declaration(Some(agentOrCapacitor), Signing())

      "output a correctly formatted Declaration json with agentOrCapacitor" in {
        val result = Json.obj(
          "agentOrCapacitor" -> agentOrCapacitor,
          "signing" -> Signing()
        )

        Declaration.writes.writes(model) shouldBe result
      }
    }

    "not supplied with an agentOrCapacitor model" should {

      val model: Declaration = Declaration(None, Signing())

      "output a correctly formatted Declaration json" in {
        val result = Json.obj(
          "signing" -> Signing()
        )

        Declaration.writes.writes(model) shouldBe result
      }
    }
  }
}
