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
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.service.mocks.MockStoreVatNumberService
import uk.gov.hmrc.vatsubscription.services.{StoreVatNumberSuccess, VatNumberDatabaseFailure}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreVatNumberControllerSpec extends UnitSpec with MockAuthConnector with MockStoreVatNumberService {

  object TestStoreVatNumberController
    extends StoreVatNumberController(mockAuthConnector, mockStoreVatNumberService)

  "storeVrn" when {
    "the CRN has been stored correctly" should {
      "return NO_CONTENT" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful())
        mockStoreVatNumber(testVatNumber)(Future.successful(Right(StoreVatNumberSuccess)))

        val request = FakeRequest() withBody testVatNumber

        val res: Result = await(TestStoreVatNumberController.storeVatNumber()(request))

        status(res) shouldBe NO_CONTENT
      }
    }

    "the CRN storage has failed" should {
      "return INTERNAL_SERVER_ERROR" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful())
        mockStoreVatNumber(testVatNumber)(Future.successful(Left(VatNumberDatabaseFailure)))

        val request = FakeRequest() withBody testVatNumber

        val res: Result = await(TestStoreVatNumberController.storeVatNumber()(request))

        status(res) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
