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

package uk.gov.hmrc.vatsubscription.controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.GetVatCustomerInformationStub._
import uk.gov.hmrc.vatsubscription.helpers.{ComponentSpecBase, CustomMatchers}
import uk.gov.hmrc.vatsubscription.models._
import uk.gov.hmrc.vatsubscription.models.get.{PPOBGet, PPOBAddressGet}

class RetrieveVatCustomerDetailsControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  //TODO: Create Integration Tests Constants package so that these can be reused across IT tests.

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

  val rlsIndicator = "0001"
  val website = "www.test.com"

  private val phoneNumber = "01234 567890"
  private val mobileNumber = "07700 123456"
  private val faxNumber = "01234 098765"
  private val email = "test@test.com"
  private val emailVerified = true

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

  //Deregistration
  private val reason = "I dont play by the rules"
  private val cancellationDate = "2018-10-01"
  private val lastReturnDate = "2018-10-01"


  private val testSuccessDesResponse = Json.obj(
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
        "deregistrationReason" -> "I dont play by the rules",
        "effectDateOfCancellation" -> "2018-10-01",
        "lastReturnDueDate" -> "2018-10-01"
      ),
      "returnPeriod" -> Json.obj(
        "stdReturnPeriod" -> returnPeriod
      )
    ),
    "inFlightInformation" -> Json.obj(
      "changeIndicators" -> Json.obj(
        "PPOBDetails" -> true,
        "bankDetails" -> true,
        "returnPeriod" -> true
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
          "stdReturnPeriod" -> returnPeriod
        )
      )
    )
  )

  "/:vatNumber/customer-details" when {
    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        stubAuthFailure()

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to DES is successful" should {

      val expectedCustomerDetailsJson: JsObject = Json.obj(
        "organisationName" -> orgName,
        "firstName" -> firstName,
        "lastName" -> lastName,
        "tradingName" -> tradingName,
        "vatRegistrationDate" -> effectiveDate,
        "hasFlatRateScheme" -> true
      )

      "return OK with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(OK, testSuccessDesResponse)

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(OK),
          jsonBodyAs(expectedCustomerDetailsJson)
        )
      }
    }

    "calls to DES returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }
  }

  "/:vatNumber/full-information" when {

    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        stubAuthFailure()

        val res = await(get(s"/$testVatNumber/full-information"))

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to DES is successful" should {
      "return OK with the status" in {

        val expectedCustomerInformationModel = VatCustomerInformation(
          MTDfBMandated,
          CustomerDetails(
            firstName = Some(firstName),
            lastName = Some(lastName),
            organisationName = Some(orgName),
            tradingName = Some(tradingName),
            vatRegistrationDate = Some(effectiveDate),
            hasFlatRateScheme = true
          ),
          Some(FlatRateScheme(
            Some(frsCategory),
            Some(frsPercent),
            Some(frsLtdCostTrader),
            Some(frsStartDate)
          )),
          Some(PPOBGet(
            PPOBAddressGet(
              addLine1,
              Some(addLine2),
              Some(addLine3),
              Some(addLine4),
              Some(addLine5),
              Some(postcode),
              countryCode
            ),
            Some(ContactDetails(
              Some(phoneNumber),
              Some(mobileNumber),
              Some(faxNumber),
              Some(email),
              Some(emailVerified)
            )),
            Some(website)
          )),
          Some(BankDetails(
            Some(accName),
            Some(accNum),
            Some(accSort)
          )),
          Some(MCReturnPeriod),
          Some(Deregistration(
            Some(reason),
            Some(cancellationDate),
            Some(lastReturnDate)
          )),
          Some(PendingChanges(
            Some(PPOBGet(
              PPOBAddressGet(
                addLine1,
                Some(addLine2),
                Some(addLine3),
                Some(addLine4),
                Some(addLine5),
                Some(postcode),
                countryCode
              ),
              Some(ContactDetails(
                Some(phoneNumber),
                Some(mobileNumber),
                Some(faxNumber),
                Some(email),
                Some(emailVerified)
              )),
              Some(website)
            )),
            Some(BankDetails(
              Some(accName),
              Some(accNum),
              Some(accSort)
            )),
            Some(MCReturnPeriod)
          ))
        )

        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(OK, testSuccessDesResponse)

        val res = await(get(s"/$testVatNumber/full-information"))

        res should have(
          httpStatus(OK),
          jsonBodyAs(Json.toJson(expectedCustomerInformationModel))
        )
      }
    }

    "calls to DES returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = await(get(s"/$testVatNumber/full-information"))

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = await(get(s"/$testVatNumber/full-information"))

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/full-information"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }
  }
}
