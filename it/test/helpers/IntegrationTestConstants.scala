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

package helpers

import java.util.UUID
import play.api.libs.json.{JsObject, Json, JsArray}
import models.{BusinessName, ContactDetails, DigitalPreference, TradingName}
import models.get.{PPOBAddressGet, PPOBGet}
import models.post.{EmailPost, PPOBAddressPost, PPOBPost}

import java.time.LocalDate

object IntegrationTestConstants {

  // Customer Details
  val title = "0001"
  val orgName = "Ancient Antiques Ltd"
  val tradingName = "Dusty Relics"
  val firstName = "Fred"
  val lastName = "Flintstone"
  val middleName = "M"
  val mandationStatus = "2"
  val regReason = "0001"
  val effectiveDate = "1967-08-13"
  val poaActiveUntil: LocalDate = LocalDate.parse("2045-01-01")
  val startDate = "1967-08-13"
  val customerMigratedToETMPDate = "2019-01-01"
  val hybridToFullMigrationDate = "2019-01-01"

  // PPOB
  val addLine1 = "Add Line 1"
  val addLine2 = "Add Line 2"
  val addLine3 = "Add Line 3"
  val addLine4 = "Add Line 4"
  val addLine5 = "Add Line 5"
  val postcode = "TE37 7AD"
  val countryCode = "ES"

  val rlsIndicator = "0001"
  val website = "www.test.com"

  val phoneNumber = "01234 567890"
  val mobileNumber = "07700 123456"
  val faxNumber = "01234 098765"
  val email = "test@test.com"
  val emailVerified = true

  // FRS
  val frsCategory = "001"
  val frsPercent = 22.9
  val frsLtdCostTrader = true
  val frsStartDate = "2001-01-01"

  // Bank Details
  val accName = "**********************"
  val accNum = "**7425"
  val accSort = "69***"

  // Return Period
  val returnPeriod = "MC"

  // Deregistration
  val reason = "I dont play by the rules"
  val cancellationDate = "2018-10-01"
  val lastReturnDate = "2018-10-01"

  // Agent Details
  val agentEmail = "agent@email.com"

  val standingRequestModelEmptyJson = Json.obj(
    "processingDate" -> "2024-07-15T09:30:47Z",
    "standingRequests" -> JsArray.empty
  )
  val standingRequestScheduleJson: JsObject = Json.obj(
    "processingDate" -> "2024-07-15T09:30:47Z",
    "standingRequests" -> Json.arr(
      Json.obj(
        "requestNumber" -> "20000037272",
        "requestCategory" -> "3",
        "createdOn" -> "2023-11-30",
        "changedOn" -> "2024-12-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "24A1",
            "startDate" -> "2024-02-01",
            "endDate" -> "2024-04-30",
            "dueDate" -> "2024-03-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2024-03-31"
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "24A1",
            "startDate" -> "2024-02-01",
            "endDate" -> "2024-04-30",
            "dueDate" -> "2024-04-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191345",
            "postingDueDate" -> "2024-04-30"
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "24A2",
            "startDate" -> "2024-05-01",
            "endDate" -> "2024-07-31",
            "dueDate" -> "2024-06-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191346",
            "postingDueDate" -> "2024-06-30"
          ),
          Json.obj(
            "period" -> "4",
            "periodKey" -> "24A2",
            "startDate" -> "2024-05-01",
            "endDate" -> "2024-07-31",
            "dueDate" -> "2024-07-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191347",
            "postingDueDate" -> "2024-07-31"
          ),
          Json.obj(
            "period" -> "5",
            "periodKey" -> "24A3",
            "startDate" -> "2024-08-01",
            "endDate" -> "2024-10-31",
            "dueDate" -> "2024-09-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191348",
            "postingDueDate" -> "2024-09-30"
          ),
          Json.obj(
            "period" -> "6",
            "periodKey" -> "24A3",
            "startDate" -> "2024-08-01",
            "endDate" -> "2024-10-31",
            "dueDate" -> "2024-10-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191349",
            "postingDueDate" -> "2024-10-31"
          ),
          Json.obj(
            "period" -> "7",
            "periodKey" -> "24A4",
            "startDate" -> "2024-11-01",
            "endDate" -> "2025-01-31",
            "dueDate" -> "2024-12-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191350",
            "postingDueDate" -> "2024-12-31"
          ),
          Json.obj(
            "period" -> "8",
            "periodKey" -> "24A4",
            "startDate" -> "2024-11-01",
            "endDate" -> "2025-01-31",
            "dueDate" -> "2025-01-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191351",
            "postingDueDate" -> "2025-01-31"
          )
        )
      ),
      Json.obj(
        "requestNumber" -> "20000037277",
        "requestCategory" -> "3",
        "createdOn" -> "2024-11-30",
        "changedOn" -> "2025-01-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "25A1",
            "startDate" -> "2025-02-01",
            "endDate" -> "2025-04-30",
            "dueDate" -> "2025-03-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "25A1",
            "startDate" -> "2025-02-01",
            "endDate" -> "2025-04-30",
            "dueDate" -> "2025-04-30",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "25A2",
            "startDate" -> "2025-05-01",
            "endDate" -> "2025-07-31",
            "dueDate" -> "2025-06-30",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "4",
            "periodKey" -> "25A2",
            "startDate" -> "2025-05-01",
            "endDate" -> "2025-07-31",
            "dueDate" -> "2025-07-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "5",
            "periodKey" -> "25A3",
            "startDate" -> "2025-08-01",
            "endDate" -> "2025-10-31",
            "dueDate" -> "2025-09-30",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "6",
            "periodKey" -> "25A3",
            "startDate" -> "2025-08-01",
            "endDate" -> "2025-10-31",
            "dueDate" -> "2025-10-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "7",
            "periodKey" -> "25A4",
            "startDate" -> "2025-11-01",
            "endDate" -> "2026-01-31",
            "dueDate" -> "2025-12-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "8",
            "periodKey" -> "25A4",
            "startDate" -> "2025-11-01",
            "endDate" -> "2026-01-31",
            "dueDate" -> "2026-01-31",
            "amount" -> 122945.23
          )
        )
      )
    )
  )

  val standingRequestNonPOACategoryJson: JsObject = Json.obj(
    "processingDate" -> "2025-03-17T09:30:47Z",
    "standingRequests" -> Json.arr(
      Json.obj(
        "requestNumber" -> "20000037277",
        "requestCategory" -> "4",
        "createdOn" -> "2025-12-31",
        "changedOn" -> "2026-01-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "26A1",
            "startDate" -> "2026-01-01",
            "endDate" -> "2026-03-31",
            "dueDate" -> "2026-02-28",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2026-02-28"
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "26A1",
            "startDate" -> "2026-01-01",
            "endDate" -> "2026-03-31",
            "dueDate" -> "2026-03-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2026-03-31"
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "25A2",
            "startDate" -> "2026-04-01",
            "endDate" -> "2026-06-30",
            "dueDate" -> "2026-05-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2026-05-31"
          )
        )
      )
    )
  )

  val standingRequestMultipleRequestCategoriesJson: JsObject = Json.obj(
    "processingDate" -> "2025-03-17T09:30:47Z",
    "standingRequests" -> Json.arr(
      Json.obj(
        "requestNumber" -> "20000037272",
        "requestCategory" -> "3",
        "createdOn" -> "2025-01-05",
        "changedOn" -> "2025-02-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "25A1",
            "startDate" -> "2025-01-01",
            "endDate" -> "2025-03-31",
            "dueDate" -> "2025-03-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2025-03-31"
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "25A2",
            "startDate" -> "2025-04-01",
            "endDate" -> "2025-06-30",
            "dueDate" -> "2025-05-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2025-05-31"
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "25A2",
            "startDate" -> "2025-04-01",
            "endDate" -> "2025-06-30",
            "dueDate" -> "2025-06-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2025-06-30"
          )
        )
      ),
      Json.obj(
        "requestNumber" -> "20000037277",
        "requestCategory" -> "4",
        "createdOn" -> "2025-12-31",
        "changedOn" -> "2026-01-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "26A1",
            "startDate" -> "2026-01-01",
            "endDate" -> "2026-03-31",
            "dueDate" -> "2026-02-28",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2026-02-28"
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "26A1",
            "startDate" -> "2026-01-01",
            "endDate" -> "2026-03-31",
            "dueDate" -> "2026-03-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2026-03-31"
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "25A2",
            "startDate" -> "2026-04-01",
            "endDate" -> "2026-06-30",
            "dueDate" -> "2026-05-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2026-05-31"
          )
        )
      )
    )
  )

  val testSuccessCustomerDetailsDesResponse: JsObject = Json.obj(
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
        "businessStartDate" -> startDate,
        "welshIndicator" -> true,
        "isPartialMigration" -> true,
        "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
        "hybridToFullMigrationDate" -> hybridToFullMigrationDate,
        "partyType" -> "50",
        "isInsolvent" -> false,
        "insolvencyType" -> "03",
        "insolvencyDate" -> "2019-01-02",
        "continueToTrade" -> true,
        "overseasIndicator" -> false,
        "nameIsReadOnly" -> true
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
        "commsPreference" -> DigitalPreference.desValue
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
      "deregistration" -> Json.obj(
        "deregistrationReason" -> reason,
        "effectDateOfCancellation" -> cancellationDate,
        "lastReturnDueDate" -> lastReturnDate
      ),
      "returnPeriod" -> Json.obj(
        "stdReturnPeriod" -> returnPeriod
      ),
      "businessActivities" -> Json.obj(
        "primaryMainCode" -> "00001",
        "mainCode2" -> "00002"
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
        "returnPeriod" -> Json.obj(
          "returnPeriod" -> returnPeriod
        ),
        "mandationStatus" -> Json.obj(
          "mandationStatus" -> mandationStatus
        ),
        "commsPreference" -> Json.obj(
          "commsPreference" -> DigitalPreference.desValue
        ),
        "organisationDetails" -> Json.obj(
          "tradingName" -> tradingName,
          "organisationName" -> orgName
        )
      )
    )
  )


  val testSuccessCustomerDetailDesResponseWithPOAActiveUntil : JsObject =
    Json.obj(
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
          "businessStartDate" -> startDate,
          "welshIndicator" -> true,
          "isPartialMigration" -> true,
          "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
          "hybridToFullMigrationDate" -> hybridToFullMigrationDate,
          "partyType" -> "50",
          "isInsolvent" -> false,
          "insolvencyType" -> "03",
          "insolvencyDate" -> "2019-01-02",
          "continueToTrade" -> true,
          "overseasIndicator" -> false,
          "nameIsReadOnly" -> true
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
          "commsPreference" -> DigitalPreference.desValue
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
        "POAActiveUntil" -> poaActiveUntil,
        "deregistration" -> Json.obj(
          "deregistrationReason" -> reason,
          "effectDateOfCancellation" -> cancellationDate,
          "lastReturnDueDate" -> lastReturnDate
        ),
        "returnPeriod" -> Json.obj(
          "stdReturnPeriod" -> returnPeriod
        ),
        "businessActivities" -> Json.obj(
          "primaryMainCode" -> "00001",
          "mainCode2" -> "00002"
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
          "returnPeriod" -> Json.obj(
            "returnPeriod" -> returnPeriod
          ),
          "mandationStatus" -> Json.obj(
            "mandationStatus" -> mandationStatus
          ),
          "commsPreference" -> Json.obj(
            "commsPreference" -> DigitalPreference.desValue
          ),
          "organisationDetails" -> Json.obj(
            "tradingName" -> tradingName,
            "organisationName" -> orgName
          )
        )
      )
    )

  val testVatNumber: String = UUID.randomUUID().toString
  val testArn: String = UUID.randomUUID().toString

  val ppobAddressModelMax: PPOBAddressGet = PPOBAddressGet(
    Some(addLine1),
    Some(addLine2),
    Some(addLine3),
    Some(addLine4),
    Some(addLine5),
    Some(postcode),
    Some(countryCode)
  )

  val ppobAddressModelMaxPost: PPOBAddressPost = PPOBAddressPost(
    Some(addLine1),
    Some(addLine2),
    Some(addLine3),
    Some(addLine4),
    Some(addLine5),
    Some(postcode),
    Some(countryCode)
  )

  val contactDetailsModelMax: ContactDetails = ContactDetails(
    Some(phoneNumber),
    Some(mobileNumber),
    Some(faxNumber),
    Some(email),
    Some(emailVerified)
  )

  val ppobModelEmailMaxPost: EmailPost =
    EmailPost(ppobAddressModelMaxPost, contactDetailsModelMax, Some(website))

  val ppobModelMax: PPOBGet =
    PPOBGet(ppobAddressModelMax, Some(contactDetailsModelMax), Some(website))

  val ppobModelMaxPost: PPOBPost =
    PPOBPost(
      ppobAddressModelMaxPost,
      Some(contactDetailsModelMax),
      Some(website),
      Some(agentEmail)
    )

  val tradingNameModel: TradingName =
    TradingName(None, Some("some Random name"))
  val businessNameModel: BusinessName = BusinessName("MyBiz", None)
}
