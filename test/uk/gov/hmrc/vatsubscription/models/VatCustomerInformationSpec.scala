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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.play.test.UnitSpec


class VatCustomerInformationSpec extends UnitSpec {

  private val testJson = Json.parse(
    """
      |{
      |	"approvedInformation": {
      |		"customerDetails": {
      |			"organisationName": "Ancient Antiques",
      |			"individual": {
      |				"title": "0001",
      |				"firstName": "Fred",
      |				"middleName": "M",
      |				"lastName": "Flintstone"
      |			},
      |			"tradingName": "a",
      |			"mandationStatus": "1",
      |			"registrationReason": "0001",
      |			"effectiveRegistrationDate": "1967-08-13",
      |			"businessStartDate": "1967-08-13"
      |		},
      |		"PPOB": {
      |			"address": {
      |				"line1": "VAT ADDR 1",
      |				"line2": "VAT ADDR 2",
      |				"line3": "VAT ADDR 3",
      |				"line4": "VAT ADDR 4",
      |				"postCode": "SW1A 2BQ",
      |				"countryCode": "ES"
      |			}
      |		},
      |		"flatRateScheme": {
      |			"FRSCategory": "001",
      |			"FRSPercentage": 123.12,
      |			"limitedCostTrader": true,
      |			"startDate": "2001-01-01"
      |		}
      |	}
      |}
    """.stripMargin)


  private val testJsonNoFlatRate = Json.parse(
    """
      |{
      |	"approvedInformation": {
      |		"customerDetails": {
      |			"organisationName": "Ancient Antiques",
      |			"individual": {
      |				"title": "0001",
      |				"firstName": "Fred",
      |				"middleName": "M",
      |				"lastName": "Flintstone"
      |			},
      |			"tradingName": "a",
      |			"mandationStatus": "1",
      |			"registrationReason": "0001",
      |			"effectiveRegistrationDate": "1967-08-13",
      |			"businessStartDate": "1967-08-13"
      |		},
      |		"PPOB": {
      |			"address": {
      |				"line1": "VAT ADDR 1",
      |				"line2": "VAT ADDR 2",
      |				"line3": "VAT ADDR 3",
      |				"line4": "VAT ADDR 4",
      |				"postCode": "SW1A 2BQ",
      |				"countryCode": "ES"
      |			}
      |		},
      |   "bankDetails":{
      |     "accountHolderName":"**********************",
      |     "bankAccountNumber":"**7425",
      |     "sortCode":"69***"
      |     }
      |	  }
      |}
    """.stripMargin)

  private val testJsonValidReturnPeriods = Json.parse(
    """
      |{
      |	"approvedInformation": {
      |		"customerDetails": {
      |			"organisationName": "Ancient Antiques",
      |			"individual": {
      |				"title": "0001",
      |				"firstName": "Fred",
      |				"middleName": "M",
      |				"lastName": "Flintstone"
      |			},
      |			"tradingName": "a",
      |			"mandationStatus": "1",
      |			"registrationReason": "0001",
      |			"effectiveRegistrationDate": "1967-08-13",
      |			"businessStartDate": "1967-08-13"
      |		},
      |  "returnPeriod":{"stdReturnPeriod":"MC"}
      |	}
      |}
    """.stripMargin)


  private val testJsonMin = Json.parse(
    """
      |{
      |   "approvedInformation": {
      |      "customerDetails": {
      |         "mandationStatus": "1"
      |      }
      |   }
      |}
    """.stripMargin)

  "desReader" should {
    "parse the json correctly when all optional fields are populated" in {
      val expected = VatCustomerInformation(MTDfBMandated, CustomerDetails(Some("Fred"), Some("Flintstone"), Some("Ancient Antiques"), Some("a")),
        Some(FlatRateScheme(Some("001"), Some(BigDecimal("123.12")), Some(true), Some("2001-01-01"))),
        Some(PPOB(Some(PPOBAddress(Some("VAT ADDR 1"),Some("VAT ADDR 2"),Some("VAT ADDR 3"),Some("VAT ADDR 4"), None, Some("SW1A 2BQ"), Some("ES"))))),
        None,
        None)
      val data = VatCustomerInformation.desReader.reads(testJson).get
      data shouldBe expected
    }

    "parse the json correctly when no optional fields are returned" in {
      val expected = VatCustomerInformation(MTDfBMandated, CustomerDetails(None, None, None, None), None, None, None, None)
      val data = VatCustomerInformation.desReader.reads(testJsonMin).get
      data shouldBe expected
    }

    "parse the json correctly when valid ReturnPeriod returned" in {
      val expected = VatCustomerInformation(MTDfBMandated, CustomerDetails(Some("Fred"), Some("Flintstone"), Some("Ancient Antiques"), Some("a")),
        None, None, None, Some(MCReturnPeriod))
      val data = VatCustomerInformation.desReader.reads(testJsonValidReturnPeriods).get
      data shouldBe expected
    }

    "parse the json correctly when no flat rate scheme is returned" in {
      val expected = VatCustomerInformation(MTDfBMandated, CustomerDetails(Some("Fred"), Some("Flintstone"), Some("Ancient Antiques"), Some("a")),
        None, Some(PPOB(Some(PPOBAddress(Some("VAT ADDR 1"),Some("VAT ADDR 2"),Some("VAT ADDR 3"),Some("VAT ADDR 4"), None, Some("SW1A 2BQ"), Some("ES"))))),
        Some(BankDetails(Some("**********************"),Some("**7425"),Some("69***"))),
        None)
      val data = VatCustomerInformation.desReader.reads(testJsonNoFlatRate).get
      data shouldBe expected
    }
  }

}
