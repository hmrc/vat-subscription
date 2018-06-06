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

  //TODO: Create Test Constants package so these can be reused in other test specs.

  //Customer Details
  private val title = "0001"
  private val orgName = "Ancient Antiques Ltd"
  private val tradingName = "Dusty Relics"
  private val firstName = "Fred"
  private val lastName = "Flintstone"
  private val middleName = "M"
  private val mandationStatus = "1"
  private val regReason = "0001"
  private val effectiveDate = "1967-08-13"
  private val startDate = "1967-08-13"

  //PPOB
  private val addLine1 = "Add Line 1"
  private val addLine2 = "Add Line 2"
  private val addLine3 = "Add Line 3"
  private val addLine4 = "Add Line 4"
  private val addLine5 = "Add Line 5"
  private val postcode = "TE37 7AD"
  private val countryCode = "ES"

  //FRS
  private val frsCategory = "001"
  private val frsPercent = 22.9
  private val frsLtdCostTrader = true
  private val frsStartDate = "2001-01-01"

  //Bank Details
  private val accName = "**********************"
  private val accNum = "**7425"
  private val accSort = "69***"

  //Return Period
  private val returnPeriod = "MC"


  private val vatInformationMax = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "organisationName" -> orgName,
        "individual" -> Json.obj(
          "title" -> title,
          "firstName" -> firstName,
          "middleName" -> middleName,
          "lastName" -> lastName
        ),
        "tradingName" -> tradingName,
        "mandationStatus" -> mandationStatus,
        "registrationReason" -> regReason,
        "effectiveRegistrationDate" -> effectiveDate,
        "businessStartDate" -> startDate
      ),
      "PPOB" -> Json.obj(
        "address" -> Json.obj(
          "line1" -> addLine1,
          "line2" -> addLine2,
          "line3" -> addLine3,
          "line4" -> addLine4,
          "line4" -> addLine4,
          "line5" -> addLine5,
          "postCode" -> postcode,
          "countryCode" -> countryCode
        )
      ),
      "flatRateScheme" -> Json.obj(
        "FRSCategory" -> frsCategory,
        "FRSPercentage" -> frsPercent,
        "limitedCostTrader" -> frsLtdCostTrader,
        "startDate" -> frsStartDate
      ),
      "bankDetails" -> Json.obj(
        "accountHolderName" -> accName,
        "bankAccountNumber" -> accNum,
        "sortCode" -> accSort
      ),
      "returnPeriod" -> Json.obj(
        "stdReturnPeriod" -> returnPeriod
      )
    )
  )


  private val vatInformationMin = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "mandationStatus" -> "1"
      )
    )
  )

  "desReader" should {
    "parse the json correctly when all optional fields are populated" in {
      val expected = VatCustomerInformation(
        MTDfBMandated,
        CustomerDetails(
          firstName = Some(firstName),
          lastName = Some(lastName),
          organisationName = Some(orgName),
          tradingName = Some(tradingName)
        ),
        Some(FlatRateScheme(
          Some(frsCategory),
          Some(frsPercent),
          Some(frsLtdCostTrader),
          Some(frsStartDate)
        )),
        Some(PPOB(
          Some(PPOBAddress(
            Some(addLine1),
            Some(addLine2),
            Some(addLine3),
            Some(addLine4),
            Some(addLine5),
            Some(postcode),
            Some(countryCode)
          ))
        )),
        Some(BankDetails(
          Some(accName),
          Some(accNum),
          Some(accSort)
        )),
        Some(MCReturnPeriod)
      )

      VatCustomerInformation.desReader.reads(vatInformationMax).get shouldBe expected
    }

    "parse the json correctly when no optional fields are returned" in {
      val expected = VatCustomerInformation(MTDfBMandated, CustomerDetails(None, None, None, None), None, None, None, None)
      VatCustomerInformation.desReader.reads(vatInformationMin).get shouldBe expected
    }
  }
}
