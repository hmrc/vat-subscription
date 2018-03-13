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

import org.scalatest.EitherValues
import play.api.http.Status._
import reactivemongo.api.commands.UpdateWriteResult
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks._
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers
import uk.gov.hmrc.vatsubscription.httpparsers.IdentityVerified
import uk.gov.hmrc.vatsubscription.repositories.mocks.MockSubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.services.IdentityVerificationOrchestrationService.{IdentityNotVerified, IdentityVerificationConnectionFailure, IdentityVerificationDatabaseFailure}
import uk.gov.hmrc.vatsubscription.services._
import uk.gov.hmrc.auth.core.ConfidenceLevel._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IdentityVerificationOrchestrationServiceSpec extends UnitSpec with EitherValues
  with MockSubscriptionRequestRepository
  with MockIdentityVerificationConnector {

  object TestIdentityVerificationOrchestrationService extends IdentityVerificationOrchestrationService(
    mockSubscriptionRequestRepository,
    mockIdentityVerificationConnector
  )

  private implicit val hc: HeaderCarrier = HeaderCarrier()


  "checkIdentityVerification" when {
    "confidence level is less than 200" when {
      "Identity verification returned verified" when {
        "update to database is successful" should {
          "return IdentityVerified" in {
            mockGetIdentityVerificationOutcome(testToken)(Future.successful(Right(IdentityVerified)))
            mockUpsertIdentityVerified(testVatNumber)(mock[UpdateWriteResult])

            val res = TestIdentityVerificationOrchestrationService.updateIdentityVerificationState(testVatNumber, testToken, L0)

            await(res) shouldBe Right(IdentityVerified)
          }
        }

        "mongo returned errors" should {
          "return IdentityVerificationDatabaseFailure" in {
            mockGetIdentityVerificationOutcome(testToken)(Future.successful(Right(IdentityVerified)))
            mockUpsertIdentityVerified(testVatNumber)(Future.failed(new Exception))

            val res = TestIdentityVerificationOrchestrationService.updateIdentityVerificationState(testVatNumber, testToken, L0)

            await(res) shouldBe Left(IdentityVerificationDatabaseFailure)
          }
        }
      }

      "Identity verification returned not verified" should {
        "return IdentityNotVerified" in {
          mockGetIdentityVerificationOutcome(testToken)(Future.successful(Right(httpparsers.IdentityNotVerified(""))))

          val res = TestIdentityVerificationOrchestrationService.updateIdentityVerificationState(testVatNumber, testToken, L0)

          await(res) shouldBe Left(IdentityNotVerified)
        }
      }

      "Identity verification returned not verified" should {
        "return IdentityVerificationConnectionFailure" in {
          mockGetIdentityVerificationOutcome(testToken)(Future.successful(Left(httpparsers.IdentityVerificationOutcomeErrorResponse(BAD_REQUEST, ""))))

          val res = TestIdentityVerificationOrchestrationService.updateIdentityVerificationState(testVatNumber, testToken, L0)

          await(res) shouldBe Left(IdentityVerificationConnectionFailure)
        }
      }

    }
    "confidence level is 200 or greater" when {
      "update to database is successful" should {
        "return IdentityVerified" in {
          mockUpsertIdentityVerified(testVatNumber)(mock[UpdateWriteResult])

          val res = TestIdentityVerificationOrchestrationService.updateIdentityVerificationState(testVatNumber, testToken, L200)

          await(res) shouldBe Right(IdentityVerified)
        }
      }

      "mongo returned errors" should {
        "return IdentityVerificationDatabaseFailure" in {
          mockUpsertIdentityVerified(testVatNumber)(Future.failed(new Exception))

          val res = TestIdentityVerificationOrchestrationService.updateIdentityVerificationState(testVatNumber, testToken, L200)

          await(res) shouldBe Left(IdentityVerificationDatabaseFailure)
        }
      }
    }
  }


}