/*
 * Copyright 2020 HM Revenue & Customs
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
import helpers.BaseTestConstants.{testAgentUser, testArn, testUser}
import helpers.DeclarationTestConstants.agentContactDetails
import helpers.DeregistrationInfoTestConstants
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.updateVatSubscription.request._
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}
import services.RequestDeregistrationService

class RequestDeregistrationServiceSpec extends TestUtil with MockUpdateVatSubscriptionConnector {

  def setup(response: UpdateVatSubscriptionResponse): RequestDeregistrationService = {
    mockUpdateVatSubscriptionResponse(response)
    new RequestDeregistrationService(mockUpdateVatSubscriptionConnector)
  }

  "Calling .deregister()" when {

    "connector call is successful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Right(SuccessModel("12345")))
        val result = service.deregister(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel, welshIndicator = false)(testUser, hc, ec)
        await(result) shouldEqual Right(SuccessModel("12345"))
      }
    }

    "connector call is unsuccessful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Left(ErrorModel("ERROR", "Error")))
        val result = service.deregister(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel, welshIndicator = false)(testUser, hc, ec)
        await(result) shouldEqual Left(ErrorModel("ERROR", "Error"))
      }
    }
  }

  "Calling .constructDeregistrationModel()" when {

    val service = new RequestDeregistrationService(mockUpdateVatSubscriptionConnector)

    "user is not an Agent" should {

      val result = service.constructDeregistrationModel(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel, welshIndicator = false)(testUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = DeregistrationRequest,
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = Some(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel),
        declaration = Declaration(None, Signing()),
        commsPreference = None
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }

    "user is an Agent" should {

      val result = service.constructDeregistrationModel(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel, welshIndicator = false)(testAgentUser)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = DeregistrationRequest,
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = Some(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel),
        declaration = Declaration(Some(AgentOrCapacitor(testArn, Some(agentContactDetails))), Signing()),
        commsPreference = None
      )

      "return an UpdateVatSubscription model containing agentOrCapacitor" in {
        result shouldEqual expectedResult
      }
    }
  }
}
