/*
 * Copyright 2021 HM Revenue & Customs
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

package service

import assets.TestUtil
import connectors.mocks.MockUpdateVatSubscriptionConnector
import helpers.BaseTestConstants.{concatNames, firstName, lastName, middleName, orgName, testAgentUser, testArn, testUser, title}
import helpers.CustomerDetailsTestConstants._
import helpers.OrganisationDetailsTestConstants._
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.ContactDetails
import models.updateVatSubscription.request._
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}

class UpdateOrganisationDetailsServiceSpec extends TestUtil with MockUpdateVatSubscriptionConnector {

  def setup(response: UpdateVatSubscriptionResponse): UpdateOrganisationDetailsService = {
    mockUpdateVatSubscriptionResponse(response)
    new UpdateOrganisationDetailsService(mockUpdateVatSubscriptionConnector)
  }

  "Calling .updateTradingName" when {

    "connector call is successful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Right(SuccessModel("12345")))
        val result = service.updateTradingName(tradingNameModel, customerDetailsModelMax.copy(welshIndicator = Some(true)))(testUser, hc, ec)
        await(result) shouldEqual Right(SuccessModel("12345"))
      }
    }

    "connector call is unsuccessful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Left(ErrorModel("ERROR", "Error")))
        val result = service.updateTradingName(tradingNameModel, customerDetailsModelMax)(testUser, hc, ec)
        await(result) shouldEqual Left(ErrorModel("ERROR", "Error"))
      }
    }
  }

  "Calling .constructTradingNameUpdateModel" when {

    val service = new UpdateOrganisationDetailsService(mockUpdateVatSubscriptionConnector)

    "user is not an Agent" should {

      val result = service.constructTradingNameUpdateModel(
        tradingNameModel,
        customerDetailsModelMax)(testUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangeOrganisationDetailsRequest,
        organisationDetails = Some(UpdatedOrganisationDetails(None, None, Some(tradingName))),
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = None
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }

    "user has removed their trading name" should {

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangeOrganisationDetailsRequest,
        organisationDetails = Some(UpdatedOrganisationDetails(None, None, Some(orgName))),
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = None
      )

      val result = service.constructTradingNameUpdateModel(
        removedTradingNameModel,
        customerDetailsModelMax)(testUser)

      "return a correct UpdateVatSubscription model with the business name as the trading name" in {
        result shouldEqual expectedResult
      }

    }

    "user is an Agent" should {

      val result = service.constructTradingNameUpdateModel(
        tradingNameModelAgent,
        customerDetailsModelMax)(testAgentUser)
      val agentContactDetails = Some(ContactDetails(None, None, None, Some("agent@emailaddress.com"), None))

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangeOrganisationDetailsRequest,
        organisationDetails = Some(UpdatedOrganisationDetails(None, None, Some(tradingName))),
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(Some(AgentOrCapacitor(testArn, agentContactDetails)), Signing()),
        commsPreference = None
      )

      "return an UpdateVatSubscription model containing agentOrCapacitor" in {
        result shouldEqual expectedResult
      }
    }

    "user has a welshIndicator" should {

      val result = service.constructTradingNameUpdateModel(
        tradingNameModel,
        customerDetailsModelMaxWithTrueWelsh)(testUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = true),
        requestedChanges = ChangeOrganisationDetailsRequest,
        organisationDetails = Some(UpdatedOrganisationDetails(None, None, Some(tradingName))),
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = None
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }
  }

  "Calling .tradingName" when {

    val service = setup(Right(SuccessModel("12345")))

    "the customerDetails has a trading name" should {

      "return the trading name" in {

        val expectedResult = tradingName

        val result = service.tradingName(tradingNameModel, customerDetailsModelMax)
        result shouldBe expectedResult

      }
    }

    "the customerDetails has no trading name" when {

      "the customerDetails has a business name" should {

        "return the business name" in {

          val result = service.tradingName(removedTradingNameModel, customerDetailsOrgNameOnly)
          result shouldBe orgName

        }
      }

      "the customerDetails has no business name" should {

        "return the correctly formatted user's name(s) for a user with 3 names" in {
          val result = service.tradingName(removedTradingNameModel, customerDetailsModelNoOrgName)
          result shouldBe concatNames
        }

        "return the correctly formatted user's name for a user with just a first name" in {
          val result = service.tradingName(removedTradingNameModel, customerDetailsModel1Name)
          result shouldBe firstName
        }

        "return the correctly formatted user's name for a user with just a first and middle name" in {
          val result = service.tradingName(removedTradingNameModel, customerDetailsModel2Names)
          result shouldBe firstName + " " + middleName
        }
      }

      "there are no individual or business names in the customerDetails" should {

          "return an empty string" in {

            val result = service.tradingName(removedTradingNameModel, customerDetailsModelMin)
            result shouldBe ""

          }
      }
    }
  }

  "Calling .updateBusinessName" when {

    "connector call is successful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Right(SuccessModel("12345")))
        val result = service.updateBusinessName(businessNameModel, customerDetailsModelMax)(testUser, hc, ec)
        await(result) shouldEqual Right(SuccessModel("12345"))
      }
    }

    "connector call is unsuccessful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Left(ErrorModel("ERROR", "Error")))
        val result = service.updateBusinessName(businessNameModel, customerDetailsModelMax)(testUser, hc, ec)
        await(result) shouldEqual Left(ErrorModel("ERROR", "Error"))
      }
    }
  }

  "Calling .constructBusinessNameUpdateModel" when {

    val service = new UpdateOrganisationDetailsService(mockUpdateVatSubscriptionConnector)
    val updateOrgDetailsModel: UpdatedOrganisationDetails = UpdatedOrganisationDetails(
      Some(businessName),
      Some(IndividualName(Some(title), Some(firstName), Some(middleName), Some(lastName))),
      customerDetailsModelMax.tradingName
    )

    "user is not an Agent" should {

      val result = service.constructBusinessNameUpdateModel(businessNameModel, customerDetailsModelMax)(testUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangeOrganisationDetailsRequest,
        organisationDetails = Some(updateOrgDetailsModel),
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = None
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }

    "user is an Agent" should {

      val result = service.constructBusinessNameUpdateModel(businessNameModelAgent, customerDetailsModelMax)(testAgentUser)
      val agentContactDetails = Some(ContactDetails(None, None, None, Some("agent@emailaddress.com"), None))

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangeOrganisationDetailsRequest,
        organisationDetails = Some(updateOrgDetailsModel),
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(Some(AgentOrCapacitor(testArn, agentContactDetails)), Signing()),
        commsPreference = None
      )

      "return an UpdateVatSubscription model containing agentOrCapacitor" in {
        result shouldEqual expectedResult
      }
    }

    "user has a welshIndicator" should {

      val result = service.constructBusinessNameUpdateModel(businessNameModel, customerDetailsModelMaxWithFRS)(testUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = true),
        requestedChanges = ChangeOrganisationDetailsRequest,
        organisationDetails = Some(updateOrgDetailsModel),
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = None
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }
  }
}
