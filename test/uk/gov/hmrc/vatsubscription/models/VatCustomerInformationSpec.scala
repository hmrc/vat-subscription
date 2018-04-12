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


class VatCustomerInformationSpec extends UnitSpec {

  private val testJson = Json.parse(
    """
      |{
      |   "approvedInformation": {
      |      "customerDetails": {
      |         "organisationName": "Ancient Antiques",
      |         "individual": {
      |            "title": "0001",
      |            "firstName": "Fred",
      |            "middleName": "M",
      |            "lastName": "Flintstone"
      |         },
      |         "tradingName": "a",
      |         "mandationStatus": "1",
      |         "registrationReason": "0001",
      |         "effectiveRegistrationDate": "1967-08-13",
      |         "businessStartDate": "1967-08-13"
      |      }
      |   }
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
      val expected = VatCustomerInformation(MTDfBMandated, CustomerDetails(Some("Fred"), Some("Flintstone"), Some("Ancient Antiques"), Some("a")))
      val data = VatCustomerInformation.desReader.reads(testJson).get
      data shouldBe expected
    }

    "parse the json correctly when no optional fields are returned" in {
      val expected = VatCustomerInformation(MTDfBMandated, CustomerDetails(None, None, None, None))
      val data = VatCustomerInformation.desReader.reads(testJsonMin).get
      data shouldBe expected
    }
  }

}
