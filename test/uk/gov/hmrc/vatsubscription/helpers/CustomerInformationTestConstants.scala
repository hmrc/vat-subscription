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

package uk.gov.hmrc.vatsubscription.helpers

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.vatsubscription.helpers.AddressTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.BankDetailsTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.FlatRateSchemeTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.ReturnPeriodTestConstants._
import uk.gov.hmrc.vatsubscription.models._


object CustomerInformationTestConstants {

  val mandationStatusCode = "1"
  val mandationStatus = "MTDfB Mandated"
  val regReason = "0001"
  val effectiveDate = "1967-08-13"
  val startDate = "1967-08-13"

  val customerInformationDESJsonMaxWithFRS: JsValue = Json.obj(
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
        "mandationStatus" -> mandationStatusCode,
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
        "FRSPercentage" -> frsPercentage,
        "limitedCostTrader" -> frsLimitedCostTrader,
        "startDate" -> frsStartDate
      ),
      "bankDetails" -> Json.obj(
        "accountHolderName" -> accName,
        "bankAccountNumber" -> accNum,
        "sortCode" -> accSort
      ),
      "returnPeriod" -> returnPeriodMC
    )
  )

  val customerInformationDESJsonMax: JsValue = Json.obj(
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
        "mandationStatus" -> mandationStatusCode,
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
      "bankDetails" -> Json.obj(
        "accountHolderName" -> accName,
        "bankAccountNumber" -> accNum,
        "sortCode" -> accSort
      ),
      "returnPeriod" -> returnPeriodMC
    )
  )

  val customerInformationDESJsonMin: JsValue = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "mandationStatus" -> mandationStatusCode
      )
    )
  )

  val customerInformationOutputJsonMaxWithFRS: JsValue = Json.obj(
    "bankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "customerDetails" -> Json.obj(
      "firstName" -> firstName,
      "hasFlatRateScheme" -> true,
      "lastName" -> lastName,
      "organisationName" -> orgName,
      "tradingName" -> tradingName
    ),
    "flatRateScheme" -> Json.obj(
      "FRSCategory" -> frsCategory,
      "FRSPercentage" -> frsPercentage,
      "limitedCostTrader" -> frsLimitedCostTrader,
      "startDate" -> frsStartDate
    ),
    "mandationStatus" -> mandationStatus,
    "ppob" -> Json.obj(
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
    "returnPeriod" -> returnPeriodMC
  )

  val customerInformationOutputJsonMax: JsValue = Json.obj(
    "bankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "customerDetails" -> Json.obj(
      "firstName" -> firstName,
      "hasFlatRateScheme" -> false,
      "lastName" -> lastName,
      "organisationName" -> orgName,
      "tradingName" -> tradingName
    ),
    "mandationStatus" -> mandationStatus,
    "ppob" -> Json.obj(
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
    "returnPeriod" -> returnPeriodMC
  )

  val customerInformationOutputJsonMin: JsValue = Json.obj(
    "customerDetails" -> Json.obj(
      "hasFlatRateScheme" -> false
    ),
    "mandationStatus" -> "MTDfB Mandated"
  )

  val customerInformationModelMaxWithFRS: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated,
    CustomerDetails(
      firstName = Some(firstName),
      lastName = Some(lastName),
      organisationName = Some(orgName),
      tradingName = Some(tradingName),
      hasFlatRateScheme = true
    ),
    Some(FlatRateScheme(
      Some(frsCategory),
      Some(frsPercentage),
      Some(frsLimitedCostTrader),
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

  val customerInformationModelMax: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated,
    CustomerDetails(
      firstName = Some(firstName),
      lastName = Some(lastName),
      organisationName = Some(orgName),
      tradingName = Some(tradingName)
    ),
    None,
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

  val customerInformationModelMin: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated, CustomerDetails(None, None, None, None), None, None, None, None
  )
}
