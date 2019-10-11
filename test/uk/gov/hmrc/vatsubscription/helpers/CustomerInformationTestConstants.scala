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

package uk.gov.hmrc.vatsubscription.helpers

import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.vatsubscription.helpers.PPOBTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.BankDetailsTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.FlatRateSchemeTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.ReturnPeriodTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.CustomerDetailsTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.DeregistrationTestConstants._
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
        "businessStartDate" -> startDate,
        "welshIndicator" -> true,
        "isPartialMigration" -> false,
        "partyType" -> partyType,
        "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
        "overseasIndicator" -> false
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
        )
      )
    )
  )

  val customerInformationDESJsonMaxR8: JsValue = Json.obj(
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
        "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
        "partyType" -> "Z1",
        "overseasIndicator" -> false
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
      "returnPeriod" -> returnPeriodMCJson,
      "businessActivities" -> Json.obj(
        "primaryMainCode" -> "00001",
        "mainCode2" -> "00002",
        "mainCode2" -> "00003"
      )
    ),
    "inFlightInformation" -> Json.obj(
      "changeIndicators" -> Json.obj(
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
        "returnPeriod" -> inflightReturnPeriodMMJson
      )
    )
  )

  val customerInformationDESJsonMinR8: JsValue = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj(
        "mandationStatus" -> mandationStatusCode,
        "isPartialMigration" -> false,
        "overseasIndicator" -> false
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
        )
      ),
      "businessActivities" -> Json.obj(
        "primaryMainCode" -> "00010"
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
        "overseasIndicator" -> false
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
      "tradingName" -> tradingName,
      "vatRegistrationDate" -> effectiveDate,
      "welshIndicator" -> true,
      "isPartialMigration" -> false,
      "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
      "overseasIndicator" -> false
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
    "mandationStatus" -> mandationStatus,
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
      "PPOBDetails" -> true,
      "bankDetails" -> true,
      "returnPeriod" -> true,
      "deregister" -> true,
      "annualAccounting" -> true
    ),
    "pendingChanges" -> Json.obj(
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
      "mandationStatus" -> mandationStatus
    ),
    "partyType" -> partyType,
    "primaryMainCode" -> "00004"
  )

  val customerInformationOutputJsonMax: JsValue = Json.obj(
    "mandationStatus" -> mandationStatus,
    "customerDetails" -> Json.obj(
      "firstName" -> firstName,
      "hasFlatRateScheme" -> false,
      "lastName" -> lastName,
      "organisationName" -> orgName,
      "tradingName" -> tradingName,
      "vatRegistrationDate" -> effectiveDate,
      "welshIndicator" -> false,
      "isPartialMigration" -> false,
      "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
      "overseasIndicator" -> false
    ),
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
      "PPOBDetails" -> true,
      "bankDetails" -> true,
      "returnPeriod" -> true,
      "deregister" -> true,
      "annualAccounting" -> true
    ),
    "pendingChanges" -> Json.obj(
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
      "mandationStatus" -> mandationStatus
    ),
    "primaryMainCode" -> "00005"
  )

  val customerInformationOutputJsonMaxWithTrueOverseas: JsValue = Json.obj(
    "mandationStatus" -> mandationStatus,
    "customerDetails" -> Json.obj(
      "firstName" -> firstName,
      "hasFlatRateScheme" -> false,
      "lastName" -> lastName,
      "organisationName" -> orgName,
      "tradingName" -> tradingName,
      "vatRegistrationDate" -> effectiveDate,
      "welshIndicator" -> false,
      "isPartialMigration" -> false,
      "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
      "overseasIndicator" -> true
    ),
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
      "PPOBDetails" -> true,
      "bankDetails" -> true,
      "returnPeriod" -> true,
      "deregister" -> true,
      "annualAccounting" -> true
    ),
    "pendingChanges" -> Json.obj(
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
      "mandationStatus" -> mandationStatus
    ),
    "primaryMainCode" -> "00006"
  )

  val customerInformationOutputJsonMin: JsValue = Json.obj(
    "customerDetails" -> customerDetailsJsonMin,
    "mandationStatus" -> "MTDfB Mandated",
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "line2" -> addLine2,
        "line3" -> addLine3,
        "line4" -> addLine4,
        "line5" -> addLine5,
        "postCode" -> postcode,
        "countryCode" -> countryCode
      )
    ),
    "primaryMainCode" -> "00010"
  )

  val manageAccountSummaryOutputJsonMax: JsValue = Json.obj(
    "mandationStatus" -> mandationStatus,
    "ppobAddress" -> Json.obj(
      "line1" -> addLine1,
      "line2" -> addLine2,
      "line3" -> addLine3,
      "line4" -> addLine4,
      "line5" -> addLine5,
      "postCode" -> postcode,
      "countryCode" -> countryCode
    ),
    "contactEmail" -> email,
    "landline" -> phoneNumber,
    "mobile" -> mobileNumber,
    "businessName" -> orgName,
    "repaymentBankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "partyType" -> "50",
    "overseasIndicator" -> false
  )

  val manageAccountSummaryOutputJsonMin: JsValue = Json.obj(
    "mandationStatus" -> mandationStatus,
    "ppobAddress" -> Json.obj(
      "line1" -> addLine1,
      "line2" -> addLine2,
      "line3" -> addLine3,
      "line4" -> addLine4,
      "line5" -> addLine5,
      "postCode" -> postcode,
      "countryCode" -> countryCode
    ),
    "overseasIndicator" -> false
  )

  val migrationDESJson: JsValue = Json.obj(
    "code" -> "MIGRATION"
  )

  val customerInformationModelMaxWithFRS: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated,
    customerDetailsModelMaxWithFRS,
    Some(frsModelMax),
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None)),
    Some(deregModel),
    Some(ChangeIndicators(
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MCReturnPeriod(None)),
      Some(MTDfBMandated)
    )),
    Some(UKCompanyType),
    "00004"
  )

  val customerInformationModelMax: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated,
    customerDetailsModelMax,
    None,
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None)),
    Some(deregModel),
    Some(ChangeIndicators(
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MCReturnPeriod(None)),
      Some(MTDfBMandated)
    )),
    None,
    "00005"
  )

  val customerInformationModelMaxWithTrueOverseas: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated,
    customerDetailsModelMaxWithTrueOverseas,
    None,
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None)),
    Some(deregModel),
    Some(ChangeIndicators(
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MCReturnPeriod(None)),
      Some(MTDfBMandated)
    )),
    None,
    "00006"
  )

  val manageAccountModelMax: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated,
    customerDetailsModelMax,
    None,
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None)),
    Some(deregModel),
    Some(ChangeIndicators(
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MCReturnPeriod(None)),
      Some(MTDfBMandated)
    )),
    Some(UKCompanyType),
    "00007"
  )

  val customerInformationModelMaxR8: VatCustomerInformation = VatCustomerInformation(
    MTDfBMandated,
    customerDetailsModelMaxR8,
    None,
    ppobModelMax,
    Some(bankDetailsModelMax),
    Some(MCReturnPeriod(None)),
    Some(deregModel),
    Some(ChangeIndicators(
      ppob = true,
      bankDetails = true,
      returnPeriod = true,
      deregister = true,
      annualAccounting = true
    )),
    Some(PendingChanges(
      Some(ppobModelMax),
      Some(bankDetailsModelMax),
      Some(MMReturnPeriod(None)),
      None
    )),
    Some(IndividualZ1Type),
    "00001"
  )

  val customerInformationModelNoWelshIndicator: VatCustomerInformation = VatCustomerInformation(
    mandationStatus = MTDfBMandated,
    customerDetails = customerDetailsModelNoWelshIndicator,
    flatRateScheme = None,
    ppob = ppobModelMax,
    bankDetails = None,
    returnPeriod = None,
    deregistration = None,
    changeIndicators = None,
    pendingChanges = None,
    primaryMainCode = "00009"
  )

  val customerInformationModelMin: VatCustomerInformation = VatCustomerInformation(
    mandationStatus = MTDfBMandated,
    customerDetails = CustomerDetails(
      firstName = None,
      lastName = None,
      organisationName = None,
      tradingName = None,
      vatRegistrationDate = None,
      welshIndicator = None,
      isPartialMigration = false,
      customerMigratedToETMPDate = None,
      overseasIndicator = false),
    flatRateScheme = None,
    ppob = ppobModelMin,
    bankDetails = None,
    returnPeriod = None,
    deregistration = None,
    changeIndicators = None,
    pendingChanges = None,
    primaryMainCode = "00010"
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
    "returnPeriod" -> inflightReturnPeriodMAJson,
    "mandationStatus" -> Json.obj(
      "mandationStatus" -> mandationStatusCode
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

  //Release 8 data -- Separated for ease of deletion

  val customerInformationOutputJsonMaxWithFRSR8: JsValue = Json.obj(
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
      "tradingName" -> tradingName,
      "vatRegistrationDate" -> effectiveDate,
      "welshIndicator" -> true,
      "isPartialMigration" -> false,
      "customerMigratedToETMPDate" -> customerMigratedToETMPDate
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
    "mandationStatus" -> mandationStatus,
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
      "PPOBDetails" -> true,
      "bankDetails" -> true,
      "returnPeriod" -> true,
      "deregister" -> true,
      "annualAccounting" -> true
    ),
    "pendingChanges" -> Json.obj(
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
      "mandationStatus" -> mandationStatus
    ),
    "partyType" -> partyType,
    "primaryMainCode" -> "00004"
  )

  val customerInformationOutputJsonMinR8: JsValue = Json.obj(
    "customerDetails" -> customerDetailsJsonMinR8,
    "mandationStatus" -> "MTDfB Mandated",
    "ppob" -> Json.obj(
      "address" -> Json.obj(
        "line1" -> addLine1,
        "line2" -> addLine2,
        "line3" -> addLine3,
        "line4" -> addLine4,
        "line5" -> addLine5,
        "postCode" -> postcode,
        "countryCode" -> countryCode
      )
    ),
    "primaryMainCode" -> "00010"
  )

  val manageAccountSummaryOutputJsonMaxR8: JsValue = Json.obj(
    "mandationStatus" -> mandationStatus,
    "ppobAddress" -> Json.obj(
      "line1" -> addLine1,
      "line2" -> addLine2,
      "line3" -> addLine3,
      "line4" -> addLine4,
      "line5" -> addLine5,
      "postCode" -> postcode,
      "countryCode" -> countryCode
    ),
    "contactEmail" -> email,
    "landline" -> phoneNumber,
    "mobile" -> mobileNumber,
    "businessName" -> orgName,
    "repaymentBankDetails" -> Json.obj(
      "accountHolderName" -> accName,
      "bankAccountNumber" -> accNum,
      "sortCode" -> accSort
    ),
    "partyType" -> "50"
  )

  val manageAccountSummaryOutputJsonMinR8: JsValue = Json.obj(
    "mandationStatus" -> mandationStatus,
    "ppobAddress" -> Json.obj(
      "line1" -> addLine1,
      "line2" -> addLine2,
      "line3" -> addLine3,
      "line4" -> addLine4,
      "line5" -> addLine5,
      "postCode" -> postcode,
      "countryCode" -> countryCode
    )
  )
}
