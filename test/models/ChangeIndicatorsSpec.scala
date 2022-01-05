/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json.Json

class ChangeIndicatorsSpec extends AnyWordSpecLike with Matchers {

  "ChangeIndicators .reads" when {

    "annualAccounting is returned" should {

      val json = Json.obj(
        "organisationDetails" -> true,
        "PPOBDetails" -> true,
        "bankDetails" -> false,
        "returnPeriod" -> true,
        "deregister" -> false,
        "annualAccounting" -> true
      )

      val model = ChangeIndicators(
        organisationDetails = true,
        ppob = true,
        bankDetails = false,
        returnPeriod = true,
        deregister = false,
        annualAccounting = true
      )

      "parse JSON correctly into model" in {
        ChangeIndicators.changeIndicatorsReader.reads(json).get shouldBe model
      }
    }

    "annualAccounting is not returned" should {

      val json = Json.obj(
        "organisationDetails" -> true,
        "PPOBDetails" -> true,
        "bankDetails" -> false,
        "returnPeriod" -> true,
        "deregister" -> false
      )

      val model = ChangeIndicators(
        organisationDetails = true,
        ppob = true,
        bankDetails = false,
        returnPeriod = true,
        deregister = false,
        annualAccounting = false
      )

      "parse JSON correctly into model and set annual accounting to false" in {
        ChangeIndicators.changeIndicatorsReader.reads(json).get shouldBe model
      }
    }
  }

  "ChangeIndicators .writes" should {

    val model = ChangeIndicators(
      organisationDetails = true,
      ppob = true,
      bankDetails = false,
      returnPeriod = true,
      deregister = false,
      annualAccounting = true
    )

    val json = Json.obj(
      "organisationDetails" -> true,
      "PPOBDetails" -> true,
      "bankDetails" -> false,
      "returnPeriod" -> true,
      "deregister" -> false,
      "annualAccounting" -> true
    )

    "parse correctly into JSON" in {
      Json.toJson(model) shouldBe json
    }
  }
}
