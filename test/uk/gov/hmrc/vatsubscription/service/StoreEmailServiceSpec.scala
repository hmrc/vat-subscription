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
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockEmailVerificationConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.CreateEmailVerificationRequestHttpParser._
import uk.gov.hmrc.vatsubscription.repositories.mocks.MockSubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.services.StoreEmailService
import uk.gov.hmrc.vatsubscription.services.StoreEmailService.{EmailDatabaseFailure, EmailVerificationFailure, StoreEmailSuccess}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StoreEmailServiceSpec extends UnitSpec with MockSubscriptionRequestRepository
  with MockEmailVerificationConnector with EitherValues {

  object TestStoreEmailService extends StoreEmailService(
    mockSubscriptionRequestRepository,
    mockEmailVerificationConnector
  )

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "storeEmail" when {
    "the email stores successfully" when {
      "the email verification is sent successfully" should {
        "return a StoreEmailSuccess with an emailVerified of false" in {
          mockUpsertEmail(testVatNumber, testEmail)(Future.successful(mock[UpdateWriteResult]))
          mockCreateEmailVerificationRequest(testEmail)(Future.successful(Right(EmailVerificationRequestSent)))

          val res = await(TestStoreEmailService.storeEmail(testVatNumber, testEmail))

          res.right.value shouldBe StoreEmailSuccess(emailVerified = false)
        }
      }

      "the email address has already been verified" should {
        "return a StoreEmailSuccess with an emailVerified of true" in {
          mockUpsertEmail(testVatNumber, testEmail)(Future.successful(mock[UpdateWriteResult]))
          mockCreateEmailVerificationRequest(testEmail)(Future.successful(Right(EmailAlreadyVerified)))

          val res = await(TestStoreEmailService.storeEmail(testVatNumber, testEmail))

          res.right.value shouldBe StoreEmailSuccess(emailVerified = true)
        }
      }

      "the email address verification request failed" should {
        "return an EmailVerificationFailure" in {
          mockUpsertEmail(testVatNumber, testEmail)(Future.successful(mock[UpdateWriteResult]))
          mockCreateEmailVerificationRequest(testEmail)(Future.successful(Left(EmailVerificationRequestFailure(BAD_REQUEST, ""))))

          val res = await(TestStoreEmailService.storeEmail(testVatNumber, testEmail))

          res.left.value shouldBe EmailVerificationFailure
        }
      }
    }
    "the vat number has not previously been stored" should {
      "return an EmailDatabaseFailureNoVATNumber" in {
        mockUpsertEmail(testVatNumber, testEmail)(Future.failed(new NoSuchElementException))
      }
    }

    "the email fails to store" should {
      "return an EmailDatabaseFailure" in {
        mockUpsertEmail(testVatNumber, testEmail)(Future.failed(new Exception))

        val res = await(TestStoreEmailService.storeEmail(testVatNumber, testEmail))

        res.left.value shouldBe EmailDatabaseFailure
      }
    }
  }
}
