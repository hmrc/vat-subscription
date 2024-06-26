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

package models

import helpers.FlatRateSchemeTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class FlatRateSchemeSpec extends AnyWordSpecLike with Matchers {

  "FlatRateScheme Reads" should {
    "parse the json correctly when all optional fields are populated" in {
      FlatRateScheme.frsReader.reads(frsJsonMax).get shouldBe frsModelMax
    }

    "parse the json correctly when all fields are null" in {
      FlatRateScheme.frsReader.reads(frsJsonMin).get shouldBe frsModelMin
    }
  }

  "FlatRateScheme Writes" should {

    "output a fully populated FlatRateScheme object with all fields populated" in {
      FlatRateScheme.frsWriter.writes(frsModelMax) shouldBe frsJsonMax
    }

    "an empty json object when an empty FlatRateScheme object is marshalled" in {
      FlatRateScheme.frsWriter.writes(frsModelMin) shouldBe frsJsonMin
    }
  }
}