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


class CircumstanceDetailsSpec extends UnitSpec {

  "circumstanceWriter" should {

    "output the json correctly when all fields are populated" in {


      //TODO: Create Test Constants package so these can be reused in other test specs.

      val expected = Json.obj(
        "organisationName" -> "Ancient Antiques",
        "flatRateScheme" -> Json.obj(
          "FRSCategory" -> "001",
          "FRSPercentage" ->123.12,
          "limitedCostTrader" -> true,
          "startDate" -> "2001-01-01"
        ),
        "PPOB" -> Json.obj(
          "address" -> Json.obj(
            "line1" -> "VAT ADDR 1",
            "line2" -> "VAT ADDR 2",
            "line3" -> "VAT ADDR 3",
            "line4" -> "VAT ADDR 4",
            "postCode" -> "SW1A 2BQ",
            "countryCode" -> "ES"
          )
        ),
        "bankDetails" ->
          Json.obj(
            "accountHolderName" -> "***********************",
            "bankAccountNumber" -> "****5274",
            "sortCode" -> "77****"
          ),
        "returnPeriod" -> Json.obj(
          "stdReturnPeriod" -> "MM"
        )
      )

      val model = CircumstanceDetails(
        Some("Ancient Antiques"),
        Some(FlatRateScheme(
          Some("001"),
          Some(BigDecimal("123.12")),
          Some(true),
          Some("2001-01-01")
        )),
        Some(PPOB(
          Some(PPOBAddress(
            Some("VAT ADDR 1"),
            Some("VAT ADDR 2"),
            Some("VAT ADDR 3"),
            Some("VAT ADDR 4"),
            None,
            Some("SW1A 2BQ"),
            Some("ES")
          ))
        )),
        Some(BankDetails(
          Some("***********************"),
          Some("****5274"),
          Some("77****")
        )),
        Some(MMReturnPeriod)
      )

      CircumstanceDetails.circsWriter.writes(model) shouldBe expected
    }

    "parse the json correctly when no optional fields are returned" in {
      val model = CircumstanceDetails(None, None, None, None, None)
      CircumstanceDetails.circsWriter.writes(model) shouldBe Json.obj()
    }
  }
}
