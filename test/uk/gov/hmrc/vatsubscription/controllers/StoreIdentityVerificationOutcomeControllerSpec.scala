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
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.IdentityVerified
import uk.gov.hmrc.vatsubscription.service.mocks.MockStoreIdentityVerificationOutcomeService
import uk.gov.hmrc.vatsubscription.services.IdentityVerificationOrchestrationService.
{IdentityNotVerified, IdentityVerificationConnectionFailure, IdentityVerificationDatabaseFailure}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreIdentityVerificationOutcomeControllerSpec extends UnitSpec with MockAuthConnector with MockStoreIdentityVerificationOutcomeService {

  object TestStoreIdentityVerificationOutcomeController
    extends StoreIdentityVerificationOutcomeController(mockAuthConnector, mockIdentityVerificationOrchestrationService)

  implicit private val system: ActorSystem = ActorSystem()
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  "storeIdentityVerificationOutcome" when {
    "a successful identity verification journey has been stored correctly" should {
      "return NoContent" in {

        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreIdentityVerificationOutcome(testVatNumber, testJourneyLink)(Future.successful(Right(IdentityVerified)))

        val request = FakeRequest() withBody testJourneyLink

        val res: Result = await(TestStoreIdentityVerificationOutcomeController.storeIdentityVerificationOutcome(testVatNumber)(request))

        status(res) shouldBe NO_CONTENT
      }
    }

    "a failed identity verification journey has not been stored" should {
      "return Forbidden" in {

        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
        mockStoreIdentityVerificationOutcome(testVatNumber, testJourneyLink)(Future.successful(Left(IdentityNotVerified)))

        val request = FakeRequest() withBody testJourneyLink

        val res: Result = await(TestStoreIdentityVerificationOutcomeController.storeIdentityVerificationOutcome(testVatNumber)(request))

        status(res) shouldBe FORBIDDEN
      }
    }

    "a identity verification journey has failed to be stored" when {
      "the database fails" should {
        "return InternalServerError" in {

          mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
          mockStoreIdentityVerificationOutcome(testVatNumber, testJourneyLink)(Future.successful(Left(IdentityVerificationDatabaseFailure)))

          val request = FakeRequest() withBody testJourneyLink

          val res: Result = await(TestStoreIdentityVerificationOutcomeController.storeIdentityVerificationOutcome(testVatNumber)(request))

          status(res) shouldBe INTERNAL_SERVER_ERROR
        }
      }
      "the connection to identity verification fails" should {
        "return BadGateway" in {

          mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Unit))
          mockStoreIdentityVerificationOutcome(testVatNumber, testJourneyLink)(Future.successful(Left(IdentityVerificationConnectionFailure)))

          val request = FakeRequest() withBody testJourneyLink

          val res: Result = await(TestStoreIdentityVerificationOutcomeController.storeIdentityVerificationOutcome(testVatNumber)(request))

          status(res) shouldBe BAD_GATEWAY
        }
      }
    }
  }

}
