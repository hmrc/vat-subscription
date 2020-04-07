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

package uk.gov.hmrc.vatsubscription.models

import java.time.LocalDate

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.vatsubscription.assets.TestUtil
import uk.gov.hmrc.vatsubscription.helpers.ReturnPeriodTestConstants._

class ReturnPeriodSpec extends TestUtil {

  val fullModel: ReturnPeriod = MAReturnPeriod(
    Some("agent@email.com"),
    Some(Seq(
      TaxPeriod(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-02-02")),
      TaxPeriod(LocalDate.parse("2018-03-03"), LocalDate.parse("2018-04-04"))
    )),
    Some(TaxPeriod(LocalDate.parse("2018-05-05"), LocalDate.parse("2018-06-06")))
  )

  val fullJson: JsObject = Json.obj(
    "stdReturnPeriod" -> "MA",
    "transactorOrCapacitorEmail" -> "agent@email.com",
    "nonStdTaxPeriods" -> Json.arr(
      Json.obj("periodStartDate" -> "2018-01-01", "periodEndDate" -> "2018-02-02"),
      Json.obj("periodStartDate" -> "2018-03-03", "periodEndDate" -> "2018-04-04")
    ),
    "firstNonNSTPPeriod" -> Json.obj(
      "periodStartDateOfFirstStandardPeriod" -> "2018-05-05",
      "periodEndDateOfFirstStandardPeriod" -> "2018-06-06"
    )
  )

  "ReturnPeriod .currentReads" when {

    "all fields are present" should {

      "output an expected ReturnPeriod" in {
        ReturnPeriod.currentDesReads.reads(fullJson).get shouldEqual fullModel
      }
    }

    "the minimum number of fields are present" should {
      val json = Json.obj("stdReturnPeriod" -> "MA")

      val model: ReturnPeriod = MAReturnPeriod(None, None, None)

      "output a correctly formatted ReturnPeriod json with no email address" in {
        ReturnPeriod.currentDesReads.reads(json).get shouldEqual model
      }
    }
  }

  "ReturnPeriod .inFlightReads" should {

    val json = Json.obj("returnPeriod" -> "MA")
    val model: ReturnPeriod = MAReturnPeriod(None, None, None)

    "output a correctly formatted ReturnPeriod json" in {
      ReturnPeriod.inFlightReads.reads(json).get shouldEqual model
    }
  }

  "ReturnPeriod Writes" when {

    "all fields are present" should {

      val json = Json.obj(
        "stdReturnPeriod" -> "MA",
        "nonStdTaxPeriods" -> Json.arr(
          Json.obj("periodStart" -> "2018-01-01", "periodEnd" -> "2018-02-02"),
          Json.obj("periodStart" -> "2018-03-03", "periodEnd" -> "2018-04-04")
        ),
        "firstNonNSTPPeriod" -> Json.obj("periodStart" -> "2018-05-05", "periodEnd" -> "2018-06-06")
      )

      "output correctly formatted JSON" in {
        Json.toJson(fullModel) shouldBe json
      }
    }

    "the minimum number of fields are present" should {

      "output correctly formatted JSON for a MAReturnPeriod" in {
        Json.toJson(MAReturnPeriod(None, None, None)) shouldBe returnPeriodMAJson
      }

      "output correctly formatted JSON for a MBReturnPeriod" in {
        Json.toJson(MBReturnPeriod(None, None, None)) shouldBe returnPeriodMBJson
      }

      "output correctly formatted JSON for a MCReturnPeriod" in {
        Json.toJson(MCReturnPeriod(None, None, None)) shouldBe returnPeriodMCJson
      }

      "output correctly formatted JSON for a MMReturnPeriod" in {
        Json.toJson(MMReturnPeriod(None, None, None)) shouldBe returnPeriodMMJson
      }

      "output correctly formatted JSON for a YAReturnPeriod" in {
        Json.toJson(YAReturnPeriod(None, None, None)) shouldBe returnPeriodYAJson
      }

      "output correctly formatted JSON for a YBReturnPeriod" in {
        Json.toJson(YBReturnPeriod(None, None, None)) shouldBe returnPeriodYBJson
      }

      "output correctly formatted JSON for a YCReturnPeriod" in {
        Json.toJson(YCReturnPeriod(None, None, None)) shouldBe returnPeriodYCJson
      }

      "output correctly formatted JSON for a YDReturnPeriod" in {
        Json.toJson(YDReturnPeriod(None, None, None)) shouldBe returnPeriodYDJson
      }

      "output correctly formatted JSON for a YEReturnPeriod" in {
        Json.toJson(YEReturnPeriod(None, None, None)) shouldBe returnPeriodYEJson
      }

      "output correctly formatted JSON for a YFReturnPeriod" in {
        Json.toJson(YFReturnPeriod(None, None, None)) shouldBe returnPeriodYFJson
      }

      "output correctly formatted JSON for a YGReturnPeriod" in {
        Json.toJson(YGReturnPeriod(None, None, None)) shouldBe returnPeriodYGJson
      }

      "output correctly formatted JSON for a YHReturnPeriod" in {
        Json.toJson(YHReturnPeriod(None, None, None)) shouldBe returnPeriodYHJson
      }

      "output correctly formatted JSON for a YIReturnPeriod" in {
        Json.toJson(YIReturnPeriod(None, None, None)) shouldBe returnPeriodYIJson
      }

      "output correctly formatted JSON for a YJReturnPeriod" in {
        Json.toJson(YJReturnPeriod(None, None, None)) shouldBe returnPeriodYJJson
      }

      "output correctly formatted JSON for a YKReturnPeriod" in {
        Json.toJson(YKReturnPeriod(None, None, None)) shouldBe returnPeriodYKJson
      }

      "output correctly formatted JSON for a YLReturnPeriod" in {
        Json.toJson(YLReturnPeriod(None, None, None)) shouldBe returnPeriodYLJson
      }
    }
  }

  "ReturnPeriod .filterReturnPeriod" when {

    "the enableAnnualAccounting feature switch is enabled" should {

      val returnPeriods: List[ReturnPeriod] = List(
        MAReturnPeriod(None, None, None),
        MBReturnPeriod(None, None, None),
        MCReturnPeriod(None, None, None),
        MMReturnPeriod(None, None, None),
        YAReturnPeriod(None, None, None),
        YBReturnPeriod(None, None, None),
        YCReturnPeriod(None, None, None),
        YDReturnPeriod(None, None, None),
        YEReturnPeriod(None, None, None),
        YFReturnPeriod(None, None, None),
        YGReturnPeriod(None, None, None),
        YHReturnPeriod(None, None, None),
        YIReturnPeriod(None, None, None),
        YJReturnPeriod(None, None, None),
        YKReturnPeriod(None, None, None),
        YLReturnPeriod(None, None, None)
      )

      "return the same return periods back for all returnPeriods" in {
        for(rp <- returnPeriods){ReturnPeriod.filterReturnPeriod(Some(rp), mockAppConfig) shouldBe Some(rp)}
      }
    }

    "the enableAnnualAccounting feature switch is disabled" when {

      "return period is in valid values" should {

        val returnPeriodMC: Option[ReturnPeriod] = Some(MCReturnPeriod(None, None, None))
        val returnPeriodMM: Option[ReturnPeriod] = Some(MMReturnPeriod(None, None, None))
        val returnPeriodMA: Option[ReturnPeriod] = Some(MAReturnPeriod(None, None, None))
        val returnPeriodMB: Option[ReturnPeriod] = Some(MBReturnPeriod(None, None, None))

        "return the same return period back" in {
          mockAppConfig.features.enableAnnualAccounting(false)

          ReturnPeriod.filterReturnPeriod(returnPeriodMA, mockAppConfig) shouldBe returnPeriodMA
          ReturnPeriod.filterReturnPeriod(returnPeriodMB, mockAppConfig) shouldBe returnPeriodMB
          ReturnPeriod.filterReturnPeriod(returnPeriodMC, mockAppConfig) shouldBe returnPeriodMC
          ReturnPeriod.filterReturnPeriod(returnPeriodMM, mockAppConfig) shouldBe returnPeriodMM
        }
      }

      "return period is not in valid values" should {

        val returnPeriodYA: Option[ReturnPeriod] = Some(YAReturnPeriod(None, None, None))

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