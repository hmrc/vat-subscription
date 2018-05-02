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

import play.api.http.Status._
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.vatsubscription.service.mocks.MockStoreTemporarySubscriptionDataControllerSpec
import uk.gov.hmrc.vatsubscription.services.StoreTemporarySubscriptionDataService.
{StoreTemporarySubscriptionDataDatabaseFailure, StoreTemporarySubscriptionDataSuccess}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreTemporarySubscriptionDataControllerSpec extends UnitSpec with MockAuthConnector
  with MockStoreTemporarySubscriptionDataControllerSpec {

  object TestStoreTemporarySubscriptionDataController
    extends StoreTemporarySubscriptionDataController(mockAuthConnector, mockStoreTemporarySubscriptionDataService)

  "storeTemporarySubscriptionData" should {
    "return NoContent" when {
      "the temporary subscription data is successfully stored" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreTemporarySubscriptionData(testVatNumber, testEmail)(Future.successful(Right(StoreTemporarySubscriptionDataSuccess)))

        val request = FakeRequest() withBody testEmail

        val res: Result = await(TestStoreTemporarySubscriptionDataController.storeTemporarySubscriptionData(testVatNumber)(request))

        status(res) shouldBe NO_CONTENT
      }
    }

    "return Internal Server Error" when {
      "the temporary subscription data is unsuccessfully stored" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreTemporarySubscriptionData(testVatNumber, testEmail)(Future.successful(Left(StoreTemporarySubscriptionDataDatabaseFailure)))

        val request = FakeRequest() withBody testEmail

        val res: Result = await(TestStoreTemporarySubscriptionDataController.storeTemporarySubscriptionData(testVatNumber)(request))

        status(res) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
