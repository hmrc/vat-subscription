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

package uk.gov.hmrc.vatsubscription.service

import assets.TestUtil
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockUpdateVatSubscriptionConnector
import uk.gov.hmrc.vatsubscription.httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request._
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.{ErrorModel, SuccessModel}
import uk.gov.hmrc.vatsubscription.services.UpdateVatSubscriptionService
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants._
import uk.gov.hmrc.vatsubscription.models.{MAReturnPeriod, User}

class UpdateVatSubscriptionServiceSpec extends TestUtil with MockUpdateVatSubscriptionConnector {

  def setup(response: UpdateVatSubscriptionResponse): UpdateVatSubscriptionService = {
    mockUpdateVatSubscriptionResponse(response)
    new UpdateVatSubscriptionService(mockUpdateVatSubscriptionConnector)
  }

  "Calling .updateVatSubscription" when {

    val requestModel: UpdateVatSubscription = UpdateVatSubscription(
      requestedChanges = changeReturnPeriod,
      updatedReturnPeriod = Some(updatedReturnPeriod),
      declaration = nonAgentDeclaration
    )

    "connector call is successful" should {
      lazy val service = setup(Right(SuccessModel("12345")))
      lazy val result = service.updateVatSubscription("123456789", requestModel)

      "return successful UpdateVatSubscriptionResponse model" in {
        await(result) shouldEqual Right(SuccessModel("12345"))
      }
    }

    "connector call is unsuccessful" should {
      lazy val service = setup(Left(ErrorModel("ERROR", "Error")))
      lazy val result = service.updateVatSubscription("123456789", requestModel)

      "return successful UpdateVatSubscriptionResponse model" in {
        await(result) shouldEqual Left(ErrorModel("ERROR", "Error"))
      }
    }
  }

  "Calling .constructReturnPeriodUpdateModel" when {

    val service = new UpdateVatSubscriptionService(mockUpdateVatSubscriptionConnector)

    "user is not an Agent" should {

      implicit val user: User = User("123456789", arn = None)
      val result = service.constructReturnPeriodUpdateModel(MAReturnPeriod)

      val expectedResult = UpdateVatSubscription(
        requestedChanges = RequestedChanges(addressDetails = false, returnPeriod = true),
        updatedReturnPeriod = Some(UpdatedReturnPeriod(MAReturnPeriod)),
        declaration = Declaration(None, Signing())
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }

    "user is an Agent" should {

      implicit val user: User = User("123456789", arn = Some("XAIT000000000"))
      val result = service.constructReturnPeriodUpdateModel(MAReturnPeriod)

      val expectedResult = UpdateVatSubscription(
        requestedChanges = RequestedChanges(addressDetails = false, returnPeriod = true),
        updatedReturnPeriod = Some(UpdatedReturnPeriod(MAReturnPeriod)),
        declaration = Declaration(Some(AgentOrCapacitor("XAIT000000000")), Signing())
      )

      "return an UpdateVatSubscription model containing agentOrCapacitor" in {
        result shouldEqual expectedResult
      }
    }
  }

  "Calling .updateReturnPeriod" should {

    implicit val user: User = User("123456789", arn = None)
    lazy val service = setup(Right(SuccessModel("12345")))
    lazy val result = service.updateReturnPeriod(MAReturnPeriod)

    "return a success model" in {
      await(result) shouldEqual Right(SuccessModel("12345"))
    }
  }
}
