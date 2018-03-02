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

import java.time.LocalDate
import java.util.UUID

import play.api.http.Status._
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.models.UserDetailsModel
import uk.gov.hmrc.vatsubscription.service.mocks.MockStoreNinoService
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreNinoControllerSpec extends UnitSpec with MockAuthConnector with MockStoreNinoService {

  object TestStoreNinoController
    extends StoreNinoController(mockAuthConnector, mockStoreNinoService)

  val testUserDetails = UserDetailsModel(
    firstName = UUID.randomUUID().toString,
    lastName = UUID.randomUUID().toString,
    dateOfBirth = LocalDate.now(),
    nino = testNino
  )

  "storeNino" when {
    "the Nino has been stored correctly" should {
      "return NO_CONTENT" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreNino(testVatNumber, testUserDetails)(Future.successful(Right(StoreNinoSuccess)))

        val request = FakeRequest() withBody testUserDetails

        val res: Result = await(TestStoreNinoController.storeNino(testVatNumber)(request))

        status(res) shouldBe NO_CONTENT
      }
    }

    "if user doesn't match with a record in CID" should {
      "return FORBIDDEN" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreNino(testVatNumber, testUserDetails)(Future.successful(Left(NoMatchFoundFailure)))

        val request = FakeRequest() withBody testUserDetails

        val res: Result = await(TestStoreNinoController.storeNino(testVatNumber)(request))

        status(res) shouldBe FORBIDDEN
      }
    }

    "if calls to CID failed" should {
      "return INTERNAL_SERVER_ERROR" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreNino(testVatNumber, testUserDetails)(Future.successful(Left(AuthenticatorFailure)))

        val request = FakeRequest() withBody testUserDetails

        val res: Result = await(TestStoreNinoController.storeNino(testVatNumber)(request))

        status(res) shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "if the vat doesn't exist in mongo" should {
      "return NOT_FOUND" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreNino(testVatNumber, testUserDetails)(Future.successful(Left(NinoDatabaseFailureNoVATNumber)))

        val request = FakeRequest() withBody testUserDetails

        val res: Result = await(TestStoreNinoController.storeNino(testVatNumber)(request))

        status(res) shouldBe NOT_FOUND
      }
    }

    "the Nino storage has failed" should {
      "return INTERNAL_SERVER_ERROR" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreNino(testVatNumber, testUserDetails)(Future.successful(Left(NinoDatabaseFailure)))

        val request = FakeRequest() withBody testUserDetails

        val res: Result = await(TestStoreNinoController.storeNino(testVatNumber)(request))

        status(res) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
