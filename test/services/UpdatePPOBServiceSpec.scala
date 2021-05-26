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

package services

import assets.TestUtil
import connectors.mocks.MockUpdateVatSubscriptionConnector
import helpers.BaseTestConstants.{testAgentUser, testArn, testUser}
import helpers.PPOBTestConstants.{ppobModelMaxPost, ppobModelMaxPostAgent}
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.ContactDetails
import models.updateVatSubscription.request._
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}

class UpdatePPOBServiceSpec extends TestUtil with MockUpdateVatSubscriptionConnector {

  def setup(response: UpdateVatSubscriptionResponse): UpdatePPOBService = {
    mockUpdateVatSubscriptionResponse(response)
    new UpdatePPOBService(mockUpdateVatSubscriptionConnector)
  }

  "Calling .updatePPOB" when {

    "connector call is successful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Right(SuccessModel("12345")))
        val result = service.updatePPOB(ppobModelMaxPost, welshIndicator = false)(testUser, hc, ec)
        await(result) shouldEqual Right(SuccessModel("12345"))
      }
    }

    "connector call is unsuccessful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Left(ErrorModel("ERROR", "Error")))
        val result = service.updatePPOB(ppobModelMaxPost, welshIndicator = false)(testUser, hc, ec)
        await(result) shouldEqual Left(ErrorModel("ERROR", "Error"))
      }
    }
  }

  "Calling .constructPPOBUpdateModel" when {

    val service = new UpdatePPOBService(mockUpdateVatSubscriptionConnector)

    "user is not an Agent" should {

      val result = service.constructPPOBUpdateModel(ppobModelMaxPost, welshIndicator = false)(testUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangePPOB,
        organisationDetails = None,
        updatedPPOB = Some(UpdatedPPOB(ppobModelMaxPost)),
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

      val result = service.constructPPOBUpdateModel(ppobModelMaxPostAgent, welshIndicator = false)(testAgentUser)
      val agentContactDetails = Some(ContactDetails(None, None, None, Some("agent@emailaddress.com"), None))

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangePPOB,
        organisationDetails = None,
        updatedPPOB = Some(UpdatedPPOB(ppobModelMaxPostAgent)),
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

      val result = service.constructPPOBUpdateModel(ppobModelMaxPost, welshIndicator = true)(testUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = true),
        requestedChanges = ChangePPOB,
        organisationDetails = None,
        updatedPPOB = Some(UpdatedPPOB(ppobModelMaxPost)),
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
