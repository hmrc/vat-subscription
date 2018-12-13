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

import java.util.UUID

import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.models.ContactDetails
import uk.gov.hmrc.vatsubscription.models.get.{PPOBAddressGet, PPOBGet}
import uk.gov.hmrc.vatsubscription.models.post.{EmailPost, PPOBAddressPost, PPOBPost}

object IntegrationTestConstants {

  //Customer Details
  val title = "0001"
  val orgName = "Ancient Antiques Ltd"
  val tradingName = "Dusty Relics"
  val firstName = "Fred"
  val lastName = "Flintstone"
  val middleName = "M"
  val mandationStatus = "1"
  val regReason = "0001"
  val effectiveDate = "1967-08-13"
  val startDate = "1967-08-13"

  //PPOB
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

  //FRS
  val frsCategory = "001"
  val frsPercent = 22.9
  val frsLtdCostTrader = true
  val frsStartDate = "2001-01-01"

  //Bank Details
  val accName = "**********************"
  val accNum = "**7425"
  val accSort = "69***"

  //Return Period
  val returnPeriod = "MC"

  //Deregistration
  val reason = "I dont play by the rules"
  val cancellationDate = "2018-10-01"
  val lastReturnDate = "2018-10-01"

  //Agent Details
  val agentEmail = "agent@email.com"


  val testSuccessCustomerDetailsDesResponse = Json.obj(
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
        "isPartialMigration" -> true
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
        "returnPeriod" -> Json.obj(
          "returnPeriod" -> returnPeriod
        )
      )
    )
  )

  val testVatNumber: String = UUID.randomUUID().toString
  val testArn: String = UUID.randomUUID().toString

  val ppobAddressModelMax = PPOBAddressGet(
    addLine1,
    Some(addLine2),
    Some(addLine3),
    Some(addLine4),
    Some(addLine5),
    Some(postcode),
    countryCode
  )

  val ppobAddressModelMaxPost = PPOBAddressPost(
    addLine1,
    Some(addLine2),
    Some(addLine3),
    Some(addLine4),
    Some(addLine5),
    Some(postcode),
    countryCode
  )

  val contactDetailsModelMax = ContactDetails(
    Some(phoneNumber),
    Some(mobileNumber),
    Some(faxNumber),
    Some(email),
    Some(emailVerified)
  )

  val ppobModelEmailMaxPost = EmailPost(ppobAddressModelMaxPost, contactDetailsModelMax, Some(website))
  val ppobModelMax = PPOBGet(ppobAddressModelMax, Some(contactDetailsModelMax), Some(website))
  val ppobModelMaxPost = PPOBPost(ppobAddressModelMaxPost, Some(contactDetailsModelMax), Some(website), Some(agentEmail))
}
