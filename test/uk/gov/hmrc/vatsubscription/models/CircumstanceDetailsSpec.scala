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


  private val expectedString =
    """{"organisationName":"Ancient Antiques","flatRateScheme":{"FRSCategory":"001","FRSPercentage":123.12,"limitedCostTrader":true,"startDate":"2001-01-01"},"PPOB":{"address":{"line1":"VAT ADDR 1","line2":"VAT ADDR 2","line3":"VAT ADDR 3","line4":"VAT ADDR 4","postCode":"SW1A 2BQ","countryCode":"ES"}},"bankDetails":{"accountHolderName":"***********************","bankAccountNumber":"****5274","sortCode":"77****"},"returnPeriod":{"stdReturnPeriod":"MM"}}"""

  "desWriter" should {
    "output the json correctly when all fields are populated" in {
      val expected = CircumstanceDetails(Some("Ancient Antiques"),
        Some(FlatRateScheme(Some("001"), Some(BigDecimal("123.12")), Some(true), Some("2001-01-01"))),
        Some(PPOB(Some(PPOBAddress(Some("VAT ADDR 1"),Some("VAT ADDR 2"),Some("VAT ADDR 3"),Some("VAT ADDR 4"), None, Some("SW1A 2BQ"), Some("ES"))))),
        Some(BankDetails(Some("***********************"), Some("****5274"), Some("77****"))),
        Some(MMReturnPeriod))
      val data = CircumstanceDetails.circsWriter.writes(expected)
      data.toString() shouldBe expectedString
    }

    "parse the json correctly when no optional fields are returned" in {
      val expected = CircumstanceDetails(None, None, None, None, None)
      val data = CircumstanceDetails.circsWriter.writes(expected)
      data.toString() shouldBe "{}"
    }
  }

}
