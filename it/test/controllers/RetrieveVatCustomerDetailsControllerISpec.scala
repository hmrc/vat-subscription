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

package controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import helpers.IntegrationTestConstants._
import helpers.servicemocks.AuthStub._
import helpers.servicemocks.GetVatCustomerInformationStub._
import helpers.{ComponentSpecBase, CustomMatchers}
import models._
import models.get.{PPOBGet, PPOBAddressGet}

class RetrieveVatCustomerDetailsControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val migrationResponse: JsObject = Json.obj(
    "code" -> "MIGRATION"
  )

  "/:vatNumber/customer-details" when {
    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        stubAuthFailure()

        val res = get(s"/$testVatNumber/customer-details")

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to DES is successful" should {

      val expectedCustomerDetailsJson: JsObject = Json.obj(
        "organisationName" -> orgName,
        "title" -> title,
        "firstName" -> firstName,
        "middleName" -> middleName,
        "lastName" -> lastName,
        "tradingName" -> tradingName,
        "effectiveRegistrationDate" -> effectiveDate,
        "hasFlatRateScheme" -> true,
        "welshIndicator" -> true,
        "isPartialMigration" -> true,
        "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
        "isInsolvent" -> false,
        "insolvencyType" -> "03",
        "insolvencyDate" -> "2019-01-02",
        "continueToTrade" -> true,
        "hybridToFullMigrationDate" -> hybridToFullMigrationDate,
        "overseasIndicator" -> false,
        "nameIsReadOnly" -> true
      )

      "return OK with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)

        val res = get(s"/$testVatNumber/customer-details")

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

        val res = get(s"/$testVatNumber/customer-details")

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = get(s"/$testVatNumber/customer-details")

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returned FORBIDDEN with MIGRATION code" should {
      "return PRECONDITION_FAILED with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, migrationResponse)

        val res = get(s"/$testVatNumber/customer-details")

        res should have(
          httpStatus(PRECONDITION_FAILED)
        )
      }
    }

    "calls to DES returned FORBIDDEN with no body" should {
      "return FORBIDDEN with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, Json.obj())

        val res = get(s"/$testVatNumber/customer-details")

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to DES returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = get(s"/$testVatNumber/customer-details")

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }

  "/:vatNumber/full-information" when {

    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        stubAuthFailure()

        val res = get(s"/$testVatNumber/full-information")

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to DES is successful" should {
      "return OK with the status" in {

        val expectedCustomerInformationModel = VatCustomerInformation(
          MTDfB,
          CustomerDetails(
            title = Some(title),
            firstName = Some(firstName),
            middleName = Some(middleName),
            lastName = Some(lastName),
            organisationName = Some(orgName),
            tradingName = Some(tradingName),
            effectiveRegistrationDate = Some(effectiveDate),
            hasFlatRateScheme = true,
            welshIndicator = Some(true),
            isPartialMigration = true,
            customerMigratedToETMPDate = Some(customerMigratedToETMPDate),
            isInsolvent = Some(false),
            insolvencyType = Some("03"),
            insolvencyDate = Some("2019-01-02"),
            continueToTrade = Some(true),
            hybridToFullMigrationDate = Some(hybridToFullMigrationDate),
            overseasIndicator = false,
            nameIsReadOnly = Some(true)
          ),
          Some(FlatRateScheme(
            Some(frsCategory),
            Some(frsPercent),
            Some(frsLtdCostTrader),
            Some(frsStartDate)
          )),
          PPOBGet(
            PPOBAddressGet(
              Some(addLine1),
              Some(addLine2),
              Some(addLine3),
              Some(addLine4),
              Some(addLine5),
              Some(postcode),
              Some(countryCode)
            ),
            Some(ContactDetails(
              Some(phoneNumber),
              Some(mobileNumber),
              Some(faxNumber),
              Some(email),
              Some(emailVerified)
            )),
            Some(website)
          ),
          Some(BankDetails(
            Some(accName),
            Some(accNum),
            Some(accSort)
          )),
          Some(MCReturnPeriod(None, None, None)),
          Some(Deregistration(
            Some(reason),
            Some(cancellationDate),
            Some(lastReturnDate)
          )),
          Some(ChangeIndicators(
            organisationDetails = true,
            ppob = true,
            bankDetails = true,
            returnPeriod = true,
            deregister = true,
            annualAccounting = false
          )),
          Some(PendingChanges(
            Some(PPOBGet(
              PPOBAddressGet(
                Some(addLine1),
                Some(addLine2),
                Some(addLine3),
                Some(addLine4),
                Some(addLine5),
                Some(postcode),
                Some(countryCode)
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
            Some(MCReturnPeriod(None, None, None)),
            Some(MTDfB),
            Some(DigitalPreference),
            Some(tradingName),
            Some(orgName)
          )),
          Some(UKCompanyType),
          primaryMainCode = Some("00001"),
          missingTrader = true,
          commsPreference = Some(DigitalPreference)
        )

        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)

        val res = get(s"/$testVatNumber/full-information")

        res should have(
          httpStatus(OK),
          jsonBodyAs(Json.toJson(expectedCustomerInformationModel)(VatCustomerInformation.writes))
        )
      }
    }

    "calls to DES returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = get(s"/$testVatNumber/full-information")

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = get(s"/$testVatNumber/full-information")

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returned FORBIDDEN with MIGRATION code" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, migrationResponse)

        val res = get(s"/$testVatNumber/full-information")

        res should have(
          httpStatus(PRECONDITION_FAILED)
        )
      }
    }

    "calls to DES returned FORBIDDEN with no body" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, Json.obj())

        val res = get(s"/$testVatNumber/full-information")

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to DES returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = get(s"/$testVatNumber/full-information")

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }
}
