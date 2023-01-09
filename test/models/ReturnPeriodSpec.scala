/*
 * Copyright 2023 HM Revenue & Customs
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

import java.time.LocalDate
import play.api.libs.json.{JsObject, Json}
import helpers.ReturnPeriodTestConstants._
import helpers.TestUtil

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

      periods.foreach { period =>
        val std = period.period.stdReturnPeriod
        s"output a correctly formatted ReturnPeriod json with no email address for $std period" in {
          val json = Json.obj("stdReturnPeriod" -> period.period.stdReturnPeriod)
          val model: ReturnPeriod = period.period
          ReturnPeriod.currentDesReads.reads(json).get shouldEqual model
        }
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

      periods.foreach { period =>
        s"output correctly formatted JSON for ${period.period.stdReturnPeriod} period" in {
          Json.toJson(period.period) shouldBe period.json
        }
      }
    }
  }
}
