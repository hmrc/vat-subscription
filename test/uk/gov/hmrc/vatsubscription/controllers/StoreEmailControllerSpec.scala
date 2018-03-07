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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.config.Constants.EmailVerification.EmailVerifiedKey
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.service.mocks.MockStoreEmailService
import uk.gov.hmrc.vatsubscription.services.StoreEmailService._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreEmailControllerSpec extends UnitSpec with MockAuthConnector with MockStoreEmailService {

  object TestStoreEmailController
    extends StoreEmailController(mockAuthConnector, mockStoreEmailService)

  implicit private val system: ActorSystem = ActorSystem()
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  "storeCompanyNumber" when {
    "the CRN has been stored correctly" should {
      "return OK with the email verification state" in {
        val emailVerified = true

        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreCompanyNumber(testVatNumber, testEmail)(Future.successful(Right(StoreEmailSuccess(emailVerified))))

        val request = FakeRequest() withBody testEmail

        val res: Result = await(TestStoreEmailController.storeEmail(testVatNumber)(request))

        status(res) shouldBe OK
        jsonBodyOf(res) shouldBe Json.obj(EmailVerifiedKey -> emailVerified)
      }
    }

    "if vat doesn't exist" should {
      "return NOT_FOUND" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreCompanyNumber(testVatNumber, testEmail)(Future.successful(Left(EmailDatabaseFailureNoVATNumber)))

        val request = FakeRequest() withBody testEmail

        val res: Result = await(TestStoreEmailController.storeEmail(testVatNumber)(request))

        status(res) shouldBe NOT_FOUND
      }
    }

    "the CRN storage has failed" should {
      "return INTERNAL_SERVER_ERROR" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreCompanyNumber(testVatNumber, testEmail)(Future.successful(Left(EmailDatabaseFailure)))

        val request = FakeRequest() withBody testEmail

        val res: Result = await(TestStoreEmailController.storeEmail(testVatNumber)(request))

        status(res) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
