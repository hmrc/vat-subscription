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

class ReturnPeriodSpec extends UnitSpec {

  //TODO: Create Test Constants package so these can be reused in other test specs.

  private val ma = Json.obj("stdReturnPeriod" -> "MA")
  private val mb = Json.obj("stdReturnPeriod" -> "MB")
  private val mc = Json.obj("stdReturnPeriod" -> "MC")
  private val mm = Json.obj("stdReturnPeriod" -> "MM")

  "ReturnPeriod Reads" should {
    "parse the json correctly for MA types" in {
      ReturnPeriod.returnPeriodReader.reads(ma).get shouldBe MAReturnPeriod
    }

    "parse the json correctly for MB types" in {
      ReturnPeriod.returnPeriodReader.reads(mb).get shouldBe MBReturnPeriod
    }

    "parse the json correctly for MC types" in {
      ReturnPeriod.returnPeriodReader.reads(mc).get shouldBe MCReturnPeriod
    }

    "parse the json correctly for MM types" in {
      ReturnPeriod.returnPeriodReader.reads(mm).get shouldBe MMReturnPeriod
    }
  }

  "ReturnPeriod Writes" should {

    "output a fully populated MA ReturnPeriod object with all fields populated" in {
      ReturnPeriod.returnPeriodWriter.writes(MAReturnPeriod) shouldBe ma
    }

    "output a fully populated MB ReturnPeriod object with all fields populated" in {
      ReturnPeriod.returnPeriodWriter.writes(MBReturnPeriod) shouldBe mb
    }

    "output a fully populated MC ReturnPeriod object with all fields populated" in {
      ReturnPeriod.returnPeriodWriter.writes(MCReturnPeriod) shouldBe mc
    }

    "output a fully populated MM ReturnPeriod object with all fields populated" in {
      ReturnPeriod.returnPeriodWriter.writes(MMReturnPeriod) shouldBe mm
    }
  }
}