/*
 * Copyright 2024 HM Revenue & Customs
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

package helpers

import play.api.libs.json.{JsObject, JsValue, Json}
import helpers.BankDetailsTestConstants._
import helpers.BaseTestConstants._
import helpers.CustomerDetailsTestConstants._
import helpers.DeregistrationTestConstants._
import helpers.FlatRateSchemeTestConstants._
import helpers.PPOBTestConstants._
import helpers.ReturnPeriodTestConstants._
import models._

object CustomerInformationTestConstants {

  val mandationStatusCode = "2"
  val regReason = "0001"
  val effectiveDate = "1967-08-13"
  val startDate = "1967-08-13"

  val pendingChangesOutputMax: JsObject = Json.obj(
    "PPOBDetails" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "line2" -> addLine2,
        "line3" -> addLine3,
        "line4" -> addLine4,
        "line5" -> addLine5,
        "postCode" -> postcode,
        "countryCode" -> countryCode
      ),
      "contactDetails" -> Json.obj(
        "primaryPhoneNumber" -> phoneNumber,
        "mobileNumber" -> mobileNumber,
        "faxNumber" -> faxNumber,
        "emailAddress" -> email,
        "emailVerified" -> emailVerified
      ),
      "websiteAddress" -> website
    ),
    "bankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "returnPeriod" -> returnPeriodMCJson,
    "mandationStatus" -> "MTDfB",
    "commsPreference" -> "DIGITAL",
    "tradingName" -> tradingName,
    "organisationName" -> orgName
  )

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
        "businessStartDate" -> startDate,
        "welshIndicator" -> true,
        "isPartialMigration" -> false,
        "partyType" -> partyType,
        "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
        "isInsolvent" -> false,
        "insolvencyType" -> "03",
        "insolvencyDate" -> "2019-01-02",
        "continueToTrade" -> true,
        "hybridToFullMigrationDate" -> hybridToFullMigrationDate,
        "overseasIndicator" -> false,
        "nameIsReadOnly" -> false
      ),
      "PPOB" -> Json.obj(
        "address" -> Json.obj(
          "line1" -> addLine1,
          "line2" -> addLine2,
          "line3" -> addLine3,
          "line4" -> addLine4,
          "line5" -> addLine5,
          "postCode" -> postcode,
          "countryCode" -> countryCode
        ),
        "RLS" -> rlsIndicator,
        "contactDetails" -> Json.obj(
          "primaryPhoneNumber" -> phoneNumber,
          "mobileNumber" -> mobileNumber,
          "faxNumber" -> faxNumber,
          "emailAddress" -> email,
          "emailVerified" -> emailVerified
        ),
        "websiteAddress" -> website,
        "commsPreference" -> "ZEL"
      ),
      "flatRateScheme" -> Json.obj(
        "FRSCategory" -> frsCategory,
        "FRSPercentage" -> frsPercentage,
        "limitedCostTrader" -> frsLimitedCostTrader,
        "startDate" -> frsStartDate
      ),
      "deregistration" -> Json.obj(
        "deregistrationReason" -> reason,
        "effectDateOfCancellation" -> cancellationDate,
        "lastReturnDueDate" -> lastReturnDate
      ),
      "bankDetails" -> Json.obj(
        "accountHolderName" -> accName,
        "bankAccountNumber" -> accNum,
        "sortCode" -> accSort
      ),
      "returnPeriod" -> returnPeriodMCJson,
      "businessActivities" -> Json.obj(
        "primaryMainCode" -> "00004",
        "mainCode2" -> "00005"
      )
    ),
    "inFlightInformation" -> Json.obj(
      "changeIndicators" -> Json.obj(
        "organisationDetails" -> true,
        "PPOBDetails" -> true,
        "bankDetails" -> true,
        "returnPeriod" -> true,
        "deregister" -> true,
        "annualAccounting" -> true
      ),
      "inFlightChanges" -> Json.obj(
        "PPOBDetails" -> Json.obj(
          "address" -> Json.obj(
            "line1" -> addLine1,
            "line2" -> addLine2,
            "line3" -> addLine3,
            "line4" -> addLine4,
            "line5" -> addLine5,
            "postCode" -> postcode,
            "countryCode" -> countryCode
          ),
          "contactDetails" -> Json.obj(
            "primaryPhoneNumber" -> phoneNumber,
            "mobileNumber" -> mobileNumber,
            "faxNumber" -> faxNumber,
            "emailAddress" -> email,
            "emailVerified" -> emailVerified
          ),
          "websiteAddress" -> website
        ),
        "bankDetails" -> Json.obj(
          "accountHolderName" -> accName,
          "bankAccountNumber" -> accNum,
          "sortCode" -> accSort
        ),
        "returnPeriod" -> inflightReturnPeriodMCJson,
        "mandationStatus" -> Json.obj(
          "mandationStatus" -> mandationStatusCode
        ),
        "commsPreference" -> Json.obj(
          "commsPreference" -> "ZEL"
        ),
        "organisationDetails" -> Json.obj(
          "tradingName" -> tradingName,
          "organisationName" -> orgName
        )
      )
    )
  )

  val customerInformationDESJsonInvalidReturnPeriod: JsValue = Json.obj(
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
        "businessStartDate" -> startDate,
        "welshIndicator" -> false,
        "isPartialMigration" -> false,
        "partyType" -> "Z1",
        "overseasIndicator" -> false,
        "nameIsReadOnly" -> false
      ),
      "PPOB" -> Json.obj(
        "address" -> Json.obj(
          "line1" -> addLine1,
          "line2" -> addLine2,
          "line3" -> addLine3,
          "line4" -> addLine4,
          "line5" -> addLine5,
          "postCode" -> postcode,
          "countryCode" -> countryCode
        ),
        "RLS" -> rlsIndicator,
        "contactDetails" -> Json.obj(
          "primaryPhoneNumber" -> phoneNumber,
          "mobileNumber" -> mobileNumber,
          "faxNumber" -> faxNumber,
          "emailAddress" -> email,
          "emailVerified" -> emailVerified
        ),
        "websiteAddress" -> website
      ),
      "deregistration" -> Json.obj(
        "deregistrationReason" -> reason,
        "effectDateOfCancellation" -> cancellationDate,
        "lastReturnDueDate" -> lastReturnDate
      ),
      "bankDetails" -> Json.obj(
        "accountHolderName" -> accName,
        "bankAccountNumber" -> accNum,
        "sortCode" -> accSort
      ),
      "returnPeriod" -> invalidReturnPeriod,
      "businessActivities" -> Json.obj(
        "primaryMainCode" -> "00002"
      )
    ),
    "inFlightInformation" -> Json.obj(
      "changeIndicators" -> Json.obj(
        "organisationDetails" -> true,
        "PPOBDetails" -> true,
        "bankDetails" -> true,
        "returnPeriod" -> true,
        "deregister" -> true
      ),
      "inFlightChanges" -> Json.obj(
        "PPOBDetails" -> Json.obj(
          "address" -> Json.obj(
            "line1" -> addLine1,
            "line2" -> addLine2,
            "line3" -> addLine3,
            "line4" -> addLine4,
            "line5" -> addLine5,
            "postCode" -> postcode,
            "countryCode" -> countryCode
          ),
          "contactDetails" -> Json.obj(
            "primaryPhoneNumber" -> phoneNumber,
            "mobileNumber" -> mobileNumber,
            "faxNumber" -> faxNumber,
            "emailAddress" -> email,
            "emailVerified" -> emailVerified
          ),
          "websiteAddress" -> website
        ),
        "bankDetails" -> Json.obj(
          "accountHolderName" -> accName,
          "bankAccountNumber" -> accNum,
          "sortCode" -> accSort
        ),
        "returnPeriod" -> invalidInflightReturnPeriod,
        "mandationStatus" -> Json.obj(
          "mandationStatus" -> mandationStatusCode
        )
      )
    )
  )

  val customerInformationDESJsonMin: JsValue = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "dataOrigin" -> "0001",
        "mandationStatus" -> mandationStatusCode,
        "isPartialMigration" -> false,
        "overseasIndicator" -> false,
        "isInsolvent" -> false
      ),
      "PPOB" -> Json.obj(
        "address" -> Json.obj(
          "line1" -> addLine1,
          "countryCode" -> countryCode
        )
      )
    )
  )

  val customerInformationOutputJsonMaxWithFRS: JsValue = Json.obj(
    "bankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "customerDetails" -> customerDetailsJsonMaxWithFRS,
    "flatRateScheme" -> Json.obj(
      "FRSCategory" -> frsCategory,
      "FRSPercentage" -> frsPercentage,
      "limitedCostTrader" -> frsLimitedCostTrader,
      "startDate" -> frsStartDate
    ),
    "deregistration" -> Json.obj(
      "deregistrationReason" -> reason,
      "effectDateOfCancellation" -> cancellationDate,
      "lastReturnDueDate" -> lastReturnDate
    ),
    "mandationStatus" -> MTDfB.value,
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "line2" -> addLine2,
        "line3" -> addLine3,
        "line4" -> addLine4,
        "line5" -> addLine5,
        "postCode" -> postcode,
        "countryCode" -> countryCode
      ),
      "contactDetails" -> Json.obj(
        "primaryPhoneNumber" -> phoneNumber,
        "mobileNumber" -> mobileNumber,
        "faxNumber" -> faxNumber,
        "emailAddress" -> email,
        "emailVerified" -> emailVerified
      ),
      "websiteAddress" -> website
    ),
    "returnPeriod" -> returnPeriodMCJson,
    "changeIndicators" -> Json.obj(
      "organisationDetails" -> true,
      "PPOBDetails" -> true,
      "bankDetails" -> true,
      "returnPeriod" -> true,
      "deregister" -> true,
      "annualAccounting" -> true
    ),
    "pendingChanges" -> pendingChangesOutputMax,
    "partyType" -> partyType,
    "primaryMainCode" -> "00004",
    "missingTrader" -> true,
    "commsPreference" -> DigitalPreference.mdtpValue
  )

  val customerInformationOutputJsonMax: JsValue = Json.obj(
    "mandationStatus" -> MTDfB.value,
    "customerDetails" -> customerDetailsJsonMax,
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "line2" -> addLine2,
        "line3" -> addLine3,
        "line4" -> addLine4,
        "line5" -> addLine5,
        "postCode" -> postcode,
        "countryCode" -> countryCode
      ),
      "contactDetails" -> Json.obj(
        "primaryPhoneNumber" -> phoneNumber,
        "mobileNumber" -> mobileNumber,
        "faxNumber" -> faxNumber,
        "emailAddress" -> email,
        "emailVerified" -> emailVerified
      ),
      "websiteAddress" -> website
    ),
    "bankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "returnPeriod" -> returnPeriodMCJson,
    "deregistration" -> Json.obj(
      "deregistrationReason" -> reason,
      "effectDateOfCancellation" -> cancellationDate,
      "lastReturnDueDate" -> lastReturnDate
    ),
    "changeIndicators" -> Json.obj(
      "organisationDetails" -> true,
      "PPOBDetails" -> true,
      "bankDetails" -> true,
      "returnPeriod" -> true,
      "deregister" -> true,
      "annualAccounting" -> true
    ),
    "pendingChanges" -> pendingChangesOutputMax,
    "primaryMainCode" -> "00005",
    "missingTrader" -> true,
    "commsPreference" -> DigitalPreference.mdtpValue
  )

  val customerInformationOutputJsonMaxWithTrueOverseas: JsValue = Json.obj(
    "mandationStatus" -> MTDfB.value,
    "customerDetails" -> customerDetailsJsonMaxWithTrueOverseas,
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "line2" -> addLine2,
        "line3" -> addLine3,
        "line4" -> addLine4,
        "line5" -> addLine5,
        "postCode" -> postcode,
        "countryCode" -> countryCode
      ),
      "contactDetails" -> Json.obj(
        "primaryPhoneNumber" -> phoneNumber,
        "mobileNumber" -> mobileNumber,
        "faxNumber" -> faxNumber,
        "emailAddress" -> email,
        "emailVerified" -> emailVerified
      ),
      "websiteAddress" -> website
    ),
    "bankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "returnPeriod" -> returnPeriodMCJson,
    "deregistration" -> Json.obj(
      "deregistrationReason" -> reason,
      "effectDateOfCancellation" -> cancellationDate,
      "lastReturnDueDate" -> lastReturnDate
    ),
    "changeIndicators" -> Json.obj(
      "organisationDetails" -> true,
      "PPOBDetails" -> true,
      "bankDetails" -> true,
      "returnPeriod" -> true,
      "deregister" -> true,
      "annualAccounting" -> true
    ),
    "pendingChanges" -> pendingChangesOutputMax,
    "primaryMainCode" -> "00006",
    "missingTrader" -> true,
    "commsPreference" -> DigitalPreference.mdtpValue
  )

  val customerInformationOutputJsonMin: JsValue = Json.obj(
    "customerDetails" -> customerDetailsJsonMin,
    "mandationStatus" -> MTDfB.value,
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "countryCode" -> countryCode
      )
    ),
    "missingTrader" -> false
  )

  val migrationDESJson: JsValue = Json.obj(
    "code" -> "MIGRATION"
  )

  val customerInformationModelMaxWithFRS: VatCustomerInformation = VatCustomerInformation(
    MTDfB,
    customerDetailsModelMaxWithFRS,
    Some(frsModelMax),
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None, None, None)),
    None,
    Some(deregModel),
    Some(ChangeIndicators(
      organisationDetails = true,
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MCReturnPeriod(None, None, None)),
      Some(MTDfB),
      Some(DigitalPreference),
      Some(tradingName),
      Some(orgName)
    )),
    Some(UKCompanyType),
    Some("00004"),
    missingTrader = true,
    Some(DigitalPreference)
  )

  val customerInformationModelMinWithFRS: VatCustomerInformation = VatCustomerInformation(
    mandationStatus = MTDfB,
    customerDetails = customerDetailsModelMaxWithFRS,
    flatRateScheme = None,
    ppob = ppobModelMin,
    bankDetails = None,
    returnPeriod = None,
    poaActiveUntil = None,
    deregistration = None,
    changeIndicators = None,
    pendingChanges = None,
    primaryMainCode = None,
    commsPreference = None
  )

  val customerInformationModelMinWithOverseas: VatCustomerInformation = VatCustomerInformation(
    mandationStatus = MTDfB,
    customerDetails = customerDetailsModelMaxWithTrueOverseas,
    flatRateScheme = None,
    ppob = ppobModelOverseas,
    bankDetails = None,
    returnPeriod = None,
    poaActiveUntil = None,
    deregistration = None,
    changeIndicators = None,
    pendingChanges = None,
    primaryMainCode = None,
    commsPreference = None
  )

  val customerInformationModelMax: VatCustomerInformation = VatCustomerInformation(
    MTDfB,
    customerDetailsModelMax,
    None,
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None, None, None)),
    poaActiveUntil = None,
    Some(deregModel),
    Some(ChangeIndicators(
      organisationDetails = true,
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MCReturnPeriod(None, None, None)),
      Some(MTDfB),
      Some(DigitalPreference),
      Some(tradingName),
      Some(orgName)
    )),
    None,
    Some("00005"),
    missingTrader = true,
    commsPreference = Some(DigitalPreference)
  )

  val customerInformationModelMaxWithTrueOverseas: VatCustomerInformation = VatCustomerInformation(
    MTDfB,
    customerDetailsModelMaxWithTrueOverseas,
    None,
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None, None, None)),
    poaActiveUntil = None,
    Some(deregModel),
    Some(ChangeIndicators(
      organisationDetails = true,
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MCReturnPeriod(None, None, None)),
      Some(MTDfB),
      Some(DigitalPreference),
      Some(tradingName),
      Some(orgName)
    )),
    None,
    Some("00006"),
    missingTrader = true,
    commsPreference = Some(DigitalPreference)
  )

  val customerInformationModelNoWelshIndicator: VatCustomerInformation = VatCustomerInformation(
    mandationStatus = MTDfB,
    customerDetails = customerDetailsModelNoWelshIndicator,
    flatRateScheme = None,
    ppob = ppobModelMax,
    bankDetails = None,
    returnPeriod = None,
    poaActiveUntil = None,
    deregistration = None,
    changeIndicators = None,
    pendingChanges = None,
    primaryMainCode = None,
    missingTrader = true,
    commsPreference = None
  )

  val customerInformationModelMin: VatCustomerInformation = VatCustomerInformation(
    mandationStatus = MTDfB,
    customerDetails = customerDetailsModelMin,
    flatRateScheme = None,
    ppob = ppobModelMin,
    bankDetails = None,
    returnPeriod = None,
    poaActiveUntil = None,
    deregistration = None,
    changeIndicators = None,
    pendingChanges = None,
    primaryMainCode = None,
    commsPreference = None
  )

  val inFlightChanges: JsObject = Json.obj(
    "PPOBDetails" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "line2" -> addLine2,
        "line3" -> addLine3,
        "line4" -> addLine4,
        "line5" -> addLine5,
        "postCode" -> postcode,
        "countryCode" -> countryCode
      ),
      "contactDetails" -> Json.obj(
        "primaryPhoneNumber" -> phoneNumber,
        "mobileNumber" -> mobileNumber,
        "faxNumber" -> faxNumber,
        "emailAddress" -> email,
        "emailVerified" -> emailVerified
      ),
      "websiteAddress" -> website
    ),
    "bankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "returnPeriod" -> inflightReturnPeriodMCJson,
    "mandationStatus" -> Json.obj(
      "mandationStatus" -> mandationStatusCode
    ),
    "commsPreference" -> Json.obj(
      "commsPreference" -> "ZEL"
    ),
    "organisationDetails" -> Json.obj(
      "tradingName" -> tradingName,
      "organisationName" -> orgName
    )
  )

  val inFlightChangesInvalidReturnPeriod: JsObject = Json.obj(
    "PPOBDetails" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "countryCode" -> countryCode
      )
    ),
    "returnPeriod" -> invalidInflightReturnPeriod
  )
}
