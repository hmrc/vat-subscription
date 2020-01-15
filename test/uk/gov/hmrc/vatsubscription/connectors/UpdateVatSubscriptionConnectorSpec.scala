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

package uk.gov.hmrc.vatsubscription.connectors

import assets.{MockHttpClient, TestUtil}
import uk.gov.hmrc.vatsubscription.config.featureSwitch.{Api1365R7, Api1365R11}
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants.testUser
import uk.gov.hmrc.vatsubscription.helpers.DeclarationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.{ChangeReturnPeriod, ControlInformation, UpdateVatSubscription}
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.{ErrorModel, SuccessModel}

import scala.concurrent.Future

class UpdateVatSubscriptionConnectorSpec extends TestUtil with MockHttpClient {

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
      updatedPPOB = None,
      updatedReturnPeriod = Some(updatedReturnPeriod),
      updateDeregistrationInfo = None,
      declaration = declarationModelAgent
    )

    def setup(response: UpdateVatSubscriptionResponse): UpdateVatSubscriptionConnector = {
      mockHttpPut[UpdateVatSubscription, UpdateVatSubscriptionResponse](response)
      new UpdateVatSubscriptionConnector(mockHttpClient, mockAppConfig)
    }

    "http PUT is successful" should {

      "return successful UpdateVatSubscriptionResponse model when using the R7 API version" in {

        mockAppConfig.features.api1365Version(Api1365R7)

        val connector = setup(Right(SuccessModel("12345")))
        val result: Future[UpdateVatSubscriptionResponse] = connector.updateVatSubscription(testUser, requestModel, hc)

        await(result) shouldBe Right(SuccessModel("12345"))
        connector.writes shouldBe UpdateVatSubscription.DESApi1365WritesR7
      }

      "return successful UpdateVatSubscriptionResponse model when using the R11 API version" in {

        mockAppConfig.features.api1365Version(Api1365R11)

        val connector = setup(Right(SuccessModel("12345")))
        val result: Future[UpdateVatSubscriptionResponse] = connector.updateVatSubscription(testUser, requestModel, hc)

        await(result) shouldBe Right(SuccessModel("12345"))
        connector.writes shouldBe UpdateVatSubscription.DESApi1365WritesR11
      }

    }

    "http PUT is unsuccessful" should {


      "return successful UpdateVatSubscriptionResponse model" in {

        val connector = setup(Left(ErrorModel("BAD_REQUEST", "REASON")))
        val result: Future[UpdateVatSubscriptionResponse] = connector.updateVatSubscription(testUser, requestModel, hc)

        await(result) shouldEqual Left(ErrorModel("BAD_REQUEST", "REASON"))
      }
    }
  }
}
