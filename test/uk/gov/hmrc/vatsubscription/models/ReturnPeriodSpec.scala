/*
 * Copyright 2019 HM Revenue & Customs
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

import assets.TestUtil
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.ReturnPeriodTestConstants._

class ReturnPeriodSpec extends TestUtil {

  "ReturnPeriod .currentReads" when {
    "called with a transactorOrCapacitorEmail" should {
      val json = Json.obj(
        "stdReturnPeriod" -> "MA",
        "transactorOrCapacitorEmail" -> "agent@email.com"
      )

      val model: ReturnPeriod = MAReturnPeriod(Some("agent@email.com"))

      "output a correctly formatted ReturnPeriod json with an email address" in {
        ReturnPeriod.currentDesReads.reads(json).get shouldEqual model
      }
    }

    "called without a transactorOrCapacitorEmail" should {
      val json = Json.obj(
        "stdReturnPeriod" -> "MA"
      )

      val model: ReturnPeriod = MAReturnPeriod(None)

      "output a correctly formatted ReturnPeriod json with no email address" in {
        ReturnPeriod.currentDesReads.reads(json).get shouldEqual model
      }
    }
  }

  "ReturnPeriod .newReads" when {
    "called with a transactorOrCapacitorEmail" should {
      val json = Json.obj(
        "returnPeriod" -> "MA",
        "transactorOrCapacitorEmail" -> "agent@email.com"
      )

      val model: ReturnPeriod = MAReturnPeriod(Some("agent@email.com"))

      "output a correctly formatted ReturnPeriod json" in {
        ReturnPeriod.newDesReads.reads(json).get shouldEqual model
      }
    }

    "called without a transactorOrCapacitorEmail" should {
      val json = Json.obj(
        "returnPeriod" -> "MA"
      )

      val model: ReturnPeriod = MAReturnPeriod(None)

      "output a correctly formatted ReturnPeriod json" in {
        ReturnPeriod.newDesReads.reads(json).get shouldEqual model
      }
    }
  }

  "ReturnPeriod Writes" should {

    "output a fully populated MA ReturnPeriod object with all fields populated" in {
      Json.toJson(MAReturnPeriod(None)) shouldBe returnPeriodMAJson
    }

    "output a fully populated MB ReturnPeriod object with all fields populated" in {
      Json.toJson(MBReturnPeriod(None)) shouldBe returnPeriodMBJson
    }

    "output a fully populated MC ReturnPeriod object with all fields populated" in {
      Json.toJson(MCReturnPeriod(None)) shouldBe returnPeriodMCJson
    }

    "output a fully populated MM ReturnPeriod object with all fields populated" in {
      Json.toJson(MMReturnPeriod(None)) shouldBe returnPeriodMMJson
    }

    "output a fully populated YA ReturnPeriod object with all fields populated" in {
      Json.toJson(YAReturnPeriod(None)) shouldBe returnPeriodYAJson
    }

    "output a fully populated YB ReturnPeriod object with all fields populated" in {
      Json.toJson(YBReturnPeriod(None)) shouldBe returnPeriodYBJson
    }

    "output a fully populated YC ReturnPeriod object with all fields populated" in {
      Json.toJson(YCReturnPeriod(None)) shouldBe returnPeriodYCJson
    }

    "output a fully populated YD ReturnPeriod object with all fields populated" in {
      Json.toJson(YDReturnPeriod(None)) shouldBe returnPeriodYDJson
    }

    "output a fully populated YE ReturnPeriod object with all fields populated" in {
      Json.toJson(YEReturnPeriod(None)) shouldBe returnPeriodYEJson
    }

    "output a fully populated YF ReturnPeriod object with all fields populated" in {
      Json.toJson(YFReturnPeriod(None)) shouldBe returnPeriodYFJson
    }

    "output a fully populated YG ReturnPeriod object with all fields populated" in {
      Json.toJson(YGReturnPeriod(None)) shouldBe returnPeriodYGJson
    }

    "output a fully populated YH ReturnPeriod object with all fields populated" in {
      Json.toJson(YHReturnPeriod(None)) shouldBe returnPeriodYHJson
    }

    "output a fully populated YI ReturnPeriod object with all fields populated" in {
      Json.toJson(YIReturnPeriod(None)) shouldBe returnPeriodYIJson
    }

    "output a fully populated YJ ReturnPeriod object with all fields populated" in {
      Json.toJson(YJReturnPeriod(None)) shouldBe returnPeriodYJJson
    }

    "output a fully populated YK ReturnPeriod object with all fields populated" in {
      Json.toJson(YKReturnPeriod(None)) shouldBe returnPeriodYKJson
    }

    "output a fully populated YL ReturnPeriod object with all fields populated" in {
      Json.toJson(YLReturnPeriod(None)) shouldBe returnPeriodYLJson
    }
  }

  "ReturnPeriod .filterReturnPeriod" when {

    "the enableAnnualAccounting feature switch is enabled" should {

      val returnPeriodMA: Option[ReturnPeriod] = Some(MAReturnPeriod(None))
      val returnPeriodMB: Option[ReturnPeriod] = Some(MBReturnPeriod(None))
      val returnPeriodMC: Option[ReturnPeriod] = Some(MCReturnPeriod(None))
      val returnPeriodMM: Option[ReturnPeriod] = Some(MMReturnPeriod(None))
      val returnPeriodYA: Option[ReturnPeriod] = Some(YAReturnPeriod(None))
      val returnPeriodYB: Option[ReturnPeriod] = Some(YBReturnPeriod(None))
      val returnPeriodYC: Option[ReturnPeriod] = Some(YCReturnPeriod(None))
      val returnPeriodYD: Option[ReturnPeriod] = Some(YDReturnPeriod(None))
      val returnPeriodYE: Option[ReturnPeriod] = Some(YEReturnPeriod(None))
      val returnPeriodYF: Option[ReturnPeriod] = Some(YFReturnPeriod(None))
      val returnPeriodYG: Option[ReturnPeriod] = Some(YGReturnPeriod(None))
      val returnPeriodYH: Option[ReturnPeriod] = Some(YHReturnPeriod(None))
      val returnPeriodYI: Option[ReturnPeriod] = Some(YIReturnPeriod(None))
      val returnPeriodYJ: Option[ReturnPeriod] = Some(YJReturnPeriod(None))
      val returnPeriodYK: Option[ReturnPeriod] = Some(YKReturnPeriod(None))
      val returnPeriodYL: Option[ReturnPeriod] = Some(YLReturnPeriod(None))

      "return the same return periods back for all returnPeriods" in {
        ReturnPeriod.filterReturnPeriod(returnPeriodMA, mockAppConfig) shouldBe returnPeriodMA
        ReturnPeriod.filterReturnPeriod(returnPeriodMB, mockAppConfig) shouldBe returnPeriodMB
        ReturnPeriod.filterReturnPeriod(returnPeriodMC, mockAppConfig) shouldBe returnPeriodMC
        ReturnPeriod.filterReturnPeriod(returnPeriodMM, mockAppConfig) shouldBe returnPeriodMM
        ReturnPeriod.filterReturnPeriod(returnPeriodYA, mockAppConfig) shouldBe returnPeriodYA
        ReturnPeriod.filterReturnPeriod(returnPeriodYB, mockAppConfig) shouldBe returnPeriodYB
        ReturnPeriod.filterReturnPeriod(returnPeriodYC, mockAppConfig) shouldBe returnPeriodYC
        ReturnPeriod.filterReturnPeriod(returnPeriodYD, mockAppConfig) shouldBe returnPeriodYD
        ReturnPeriod.filterReturnPeriod(returnPeriodYE, mockAppConfig) shouldBe returnPeriodYE
        ReturnPeriod.filterReturnPeriod(returnPeriodYF, mockAppConfig) shouldBe returnPeriodYF
        ReturnPeriod.filterReturnPeriod(returnPeriodYG, mockAppConfig) shouldBe returnPeriodYG
        ReturnPeriod.filterReturnPeriod(returnPeriodYH, mockAppConfig) shouldBe returnPeriodYH
        ReturnPeriod.filterReturnPeriod(returnPeriodYI, mockAppConfig) shouldBe returnPeriodYI
        ReturnPeriod.filterReturnPeriod(returnPeriodYJ, mockAppConfig) shouldBe returnPeriodYJ
        ReturnPeriod.filterReturnPeriod(returnPeriodYK, mockAppConfig) shouldBe returnPeriodYK
        ReturnPeriod.filterReturnPeriod(returnPeriodYL, mockAppConfig) shouldBe returnPeriodYL
      }
    }

    "the enableAnnualAccounting feature switch is disabled" when {

      "return period is in valid values" should {

        val returnPeriodMA: Option[ReturnPeriod] = Some(MAReturnPeriod(None))
        val returnPeriodMB: Option[ReturnPeriod] = Some(MBReturnPeriod(None))
        val returnPeriodMC: Option[ReturnPeriod] = Some(MCReturnPeriod(None))
        val returnPeriodMM: Option[ReturnPeriod] = Some(MMReturnPeriod(None))

        "return the same return period back" in {
          mockAppConfig.features.enableAnnualAccounting(false)

          ReturnPeriod.filterReturnPeriod(returnPeriodMA, mockAppConfig) shouldBe returnPeriodMA
          ReturnPeriod.filterReturnPeriod(returnPeriodMB, mockAppConfig) shouldBe returnPeriodMB
          ReturnPeriod.filterReturnPeriod(returnPeriodMC, mockAppConfig) shouldBe returnPeriodMC
          ReturnPeriod.filterReturnPeriod(returnPeriodMM, mockAppConfig) shouldBe returnPeriodMM
        }
      }

      "return period is not in valid values" should {

        val returnPeriodYA: Option[ReturnPeriod] = Some(YAReturnPeriod(None))

        "return None" in {
          mockAppConfig.features.enableAnnualAccounting(false)
          ReturnPeriod.filterReturnPeriod(returnPeriodYA, mockAppConfig) shouldBe None
        }
      }
    }

    "not supplied with a return period" should {

      "return None" in {
        ReturnPeriod.filterReturnPeriod(None, mockAppConfig) shouldBe None
      }
    }
  }
}