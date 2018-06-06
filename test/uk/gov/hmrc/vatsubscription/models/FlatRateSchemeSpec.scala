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

package uk.gov.hmrc.vatsubscription.models

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class FlatRateSchemeSpec extends UnitSpec {

  //TODO: Create Test Constants package so these can be reused in other test specs.

  private val category = "003"
  private val percentage = 59.99
  private val startDate = "2001-01-01"
  private val limitedCostTrader = true

  private val allFieldsInput = Json.obj(
    "FRSCategory" -> category,
    "FRSPercentage" -> percentage,
    "startDate" -> startDate,
    "limitedCostTrader" -> limitedCostTrader
  )

  private val noFields = Json.obj()

  "FlatRateScheme Reads" should {
    "parse the json correctly when all optional fields are populated" in {
      val expected = FlatRateScheme(Some(category), Some(percentage), Some(limitedCostTrader), Some(startDate))
      FlatRateScheme.frsReader.reads(allFieldsInput).get shouldBe expected
    }

    "parse the json correctly when all fields are null" in {
      val expected = FlatRateScheme(None, None, None, None)
      FlatRateScheme.frsReader.reads(noFields).get shouldBe expected
    }
  }

  "FlatRateScheme Writes" should {

    "output a fully populated FlatRateScheme object with all fields populated" in {
      val model = FlatRateScheme(Some(category), Some(percentage), Some(limitedCostTrader), Some(startDate))
      FlatRateScheme.frsWriter.writes(model) shouldBe allFieldsInput
    }

    "an empty json object when an empty FlatRateScheme object is marshalled" in {
      val model = FlatRateScheme(None,None,None,None)
      FlatRateScheme.frsWriter.writes(model) shouldBe noFields
    }
  }
}