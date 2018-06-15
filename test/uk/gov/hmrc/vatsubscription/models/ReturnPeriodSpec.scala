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
import uk.gov.hmrc.vatsubscription.helpers.ReturnPeriodTestConstants._

class ReturnPeriodSpec extends UnitSpec {

  "ReturnPeriod.apply" should {

    "for 'January' should return Jan case object" in {
      ReturnPeriod("MA") shouldBe MAReturnPeriod
    }

    "for 'February' should return Feb case object" in {
      ReturnPeriod("MB") shouldBe MBReturnPeriod
    }

    "for 'March' should return Mar case object" in {
      ReturnPeriod("MC") shouldBe MCReturnPeriod
    }

    "for 'Monthly' should return Monthly case object" in {
      ReturnPeriod("MM") shouldBe MMReturnPeriod
    }

    "for an invalid return period should return 'None'" in {
      ReturnPeriod("AB") shouldBe InvalidReturnPeriod
    }
  }

  "ReturnPeriod.unapply" should {

    "for Jan case object return 'January'" in {
      ReturnPeriod.unapply(MAReturnPeriod) shouldBe "MA"
    }

    "for Feb case object return 'February'" in {
      ReturnPeriod.unapply(MBReturnPeriod) shouldBe "MB"
    }

    "for Mar case object return 'March'" in {
      ReturnPeriod.unapply(MCReturnPeriod) shouldBe "MC"
    }

    "for Monthly case object return 'Monthly'" in {
      ReturnPeriod.unapply(MMReturnPeriod) shouldBe "MM"
    }
  }

  "ReturnPeriod Reads" should {
    "parse the json correctly for MA types" in {
      returnPeriodMAJson.as[ReturnPeriod] shouldBe MAReturnPeriod
    }

    "parse the json correctly for MB types" in {
      returnPeriodMBJson.as[ReturnPeriod] shouldBe MBReturnPeriod
    }

    "parse the json correctly for MC types" in {
      returnPeriodMCJson.as[ReturnPeriod] shouldBe MCReturnPeriod
    }

    "parse the json correctly for MM types" in {
      returnPeriodMMJson.as[ReturnPeriod] shouldBe MMReturnPeriod
    }
  }

  "ReturnPeriod Writes" should {

    "output a fully populated MA ReturnPeriod object with all fields populated" in {
      Json.toJson(MAReturnPeriod) shouldBe returnPeriodMAJson
    }

    "output a fully populated MB ReturnPeriod object with all fields populated" in {
      Json.toJson(MBReturnPeriod) shouldBe returnPeriodMBJson
    }

    "output a fully populated MC ReturnPeriod object with all fields populated" in {
      Json.toJson(MCReturnPeriod) shouldBe returnPeriodMCJson
    }

    "output a fully populated MM ReturnPeriod object with all fields populated" in {
      Json.toJson(MMReturnPeriod) shouldBe returnPeriodMMJson
    }
  }
}