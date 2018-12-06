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

  val testNotMasteredResponse = Json.obj(
    "code" -> "NOT_MASTERED"
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
        "hasFlatRateScheme" -> true,
        "welshIndicator" -> true,
        "isPartialMigration" -> true
      )

      "return OK with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)

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

    "calls to DES returned FORBIDDEN with NOT_MASTERED code" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, testNotMasteredResponse)

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }

    "calls to DES returned FORBIDDEN with no body" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, Json.obj())

        val res = await(get(s"/$testVatNumber/customer-details"))

        res should have(
          httpStatus(BAD_GATEWAY)
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
            hasFlatRateScheme = true,
            welshIndicator = Some(true),
            isPartialMigration = true
          ),
          Some(FlatRateScheme(
            Some(frsCategory),
            Some(frsPercent),
            Some(frsLtdCostTrader),
            Some(frsStartDate)
          )),
          PPOBGet(
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
          ),
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
          Some(ChangeIndicators(
            ppob = true,
            bankDetails = true,
            returnPeriod = true,
            deregister = true
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
        stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)

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

    "calls to DES returned FORBIDDEN with NOT_MASTERED code" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, testNotMasteredResponse)

        val res = await(get(s"/$testVatNumber/full-information"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }

    "calls to DES returned FORBIDDEN with no body" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, Json.obj())

        val res = await(get(s"/$testVatNumber/full-information"))

        res should have(
          httpStatus(BAD_GATEWAY)
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

  "/:vatNumber/manage-account-summary" when {

    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        stubAuthFailure()

        val res = await(get(s"/$testVatNumber/manage-account-summary"))

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
            hasFlatRateScheme = true,
            welshIndicator = Some(true),
            isPartialMigration = true
          ),
          Some(FlatRateScheme(
            Some(frsCategory),
            Some(frsPercent),
            Some(frsLtdCostTrader),
            Some(frsStartDate)
          )),
          PPOBGet(
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
          ),
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
          Some(ChangeIndicators(
            ppob = true,
            bankDetails = true,
            returnPeriod = true,
            deregister = true
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
        stubGetInformation(testVatNumber)(OK, testSuccessCustomerDetailsDesResponse)

        val res = await(get(s"/$testVatNumber/manage-account-summary"))

        res should have(
          httpStatus(OK),
          jsonBodyAs(Json.toJson(expectedCustomerInformationModel)(VatCustomerInformation.manageAccountWrites))
        )
      }
    }

    "calls to DES returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = await(get(s"/$testVatNumber/manage-account-summary"))

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = await(get(s"/$testVatNumber/manage-account-summary"))

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returned FORBIDDEN with NOT_MASTERED code" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, testNotMasteredResponse)

        val res = await(get(s"/$testVatNumber/manage-account-summary"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }

    "calls to DES returned FORBIDDEN with no body" should {
      "return BAD_GATEWAY with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(FORBIDDEN, Json.obj())

        val res = await(get(s"/$testVatNumber/manage-account-summary"))

        res should have(
          httpStatus(BAD_GATEWAY)
        )
      }
    }

    "calls to DES returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/manage-account-summary"))

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }

}
