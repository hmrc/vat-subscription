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


import reactivemongo.api.commands.UpdateWriteResult
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthenticatorConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.repositories.mocks.MockEmailRequestRepository
import uk.gov.hmrc.vatsubscription.services.StoreTemporarySubscriptionDataService.{StoreTemporarySubscriptionDataDatabaseFailure,
StoreTemporarySubscriptionDataSuccess}
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class StoreTemporarySubscriptionDataServiceSpec
  extends UnitSpec with MockAuthenticatorConnector with MockEmailRequestRepository {

  object TestStoreTemporarySubscriptionDataService extends StoreTemporarySubscriptionDataService(
    mockEmailRequestRepository
  )

  "storeTemporarySubscriptionData" should {
    "store the temporary subscription data and return StoreTemporarySubscriptionDataSuccess" in {

      mockUpsertEmail(testVatNumber, testEmail)(Future.successful(mock[UpdateWriteResult]))

      val res = TestStoreTemporarySubscriptionDataService.storeTemporarySubscriptionData(testVatNumber, testEmail)
      await(res) shouldBe Right(StoreTemporarySubscriptionDataSuccess)
    }

    "return TemporarySubscriptionDataDatabaseFailureVatNumberNotFound" when {
      "the database operation otherwise fails" in {
        mockUpsertEmail(testVatNumber, testEmail)(Future.failed(new Exception))

        val res = TestStoreTemporarySubscriptionDataService.storeTemporarySubscriptionData(testVatNumber, testEmail)
        await(res) shouldBe Left(StoreTemporarySubscriptionDataDatabaseFailure)
      }
    }
  }

}
