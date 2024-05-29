/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import helpers.BaseTestConstants.{fakeRequest, testCredentials, testUser, testVatNumber}
import helpers.DeclarationTestConstants._
import helpers.UpdateVatSubscriptionTestConstants._
import helpers.{MockHttpClient, TestUtil}
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.User
import models.updateVatSubscription.request.{ChangeReturnPeriod, ControlInformation, UpdateVatSubscription}
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException

import scala.concurrent.Future

class UpdateVatSubscriptionConnectorSpec extends TestUtil with MockHttpClient {

  implicit val testUser: User[_] = User(testVatNumber, None, testCredentials.providerId)(fakeRequest)

  "UpdateVatSubscriptionConnector url()" should {

    object TestConnector extends UpdateVatSubscriptionConnector(mockHttp, mockAppConfig)

    "correctly format the url" in {
      TestConnector.url("123456789") shouldEqual "http://localhost:9156/vat/subscription/vrn/123456789"
    }
  }

  "UpdateVatSubscriptionConnector .updateVatSubscription()" when {

    val requestModel: UpdateVatSubscription = UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator = false),
      requestedChanges = ChangeReturnPeriod,
      organisationDetails = None,
      updatedPPOB = None,
      updatedReturnPeriod = Some(updatedReturnPeriod),
      updateDeregistrationInfo = None,
      declaration = declarationModelAgent,
      commsPreference = None
    )

    def setup(response: Future[UpdateVatSubscriptionResponse]): UpdateVatSubscriptionConnector = {
      mockHttpPut[UpdateVatSubscription, UpdateVatSubscriptionResponse](response)
      new UpdateVatSubscriptionConnector(mockHttpClient, mockAppConfig)
    }

    "http PUT is successful" should {

      "return successful UpdateVatSubscriptionResponse model" in {

        val connector = setup(Future.successful(Right(SuccessModel("12345"))))
        val result: Future[UpdateVatSubscriptionResponse] = connector.updateVatSubscription(requestModel, hc)

        await(result) shouldBe Right(SuccessModel("12345"))
      }

    }

    "http PUT is unsuccessful" should {


      "return successful UpdateVatSubscriptionResponse model" in {

        val connector = setup(Future.successful(Left(ErrorModel("BAD_REQUEST", "REASON"))))
        val result: Future[UpdateVatSubscriptionResponse] = connector.updateVatSubscription(requestModel, hc)

        await(result) shouldBe Left(ErrorModel("BAD_REQUEST", "REASON"))
      }
    }

    "there is a HTTP exception" should {

      "return an ErrorModel" in {
        val exception = new RequestTimeoutException("Request timed out!!!")
        val connector = setup(Future.failed(exception))
        val result: Future[UpdateVatSubscriptionResponse] = connector.updateVatSubscription(requestModel, hc)

        await(result) shouldBe Left(ErrorModel("BAD_GATEWAY", exception.message))
      }
    }
  }
}
