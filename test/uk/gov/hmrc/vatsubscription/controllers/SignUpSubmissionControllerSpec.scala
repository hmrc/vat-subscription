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
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.service.mocks.MockSignUpSubmissionService

import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.services.SignUpSubmissionService._

import scala.concurrent.Future

class SignUpSubmissionControllerSpec extends UnitSpec
  with MockAuthConnector with MockSignUpSubmissionService {

  object TestSignUpSubmissionController extends SignUpSubmissionController(
    mockAuthConnector,
    mockSignUpSubmissionService
  )

  "submitSignUpRequest" when {
    "the sign up service returns a success" should {
      "return NO_CONTENT" in {
        mockAuthorise()(Future.successful(Unit))
        mockSubmitSignUpRequest(testVatNumber)(Future.successful(Right(SignUpRequestSubmitted)))

        val res = TestSignUpSubmissionController.submitSignUpRequest(testVatNumber)(FakeRequest())

        status(res) shouldBe NO_CONTENT
      }
    }
    "the sign up service returns InsufficientData" should {
      "return BAD_REQUEST"in {
        mockAuthorise()(Future.successful(Unit))
        mockSubmitSignUpRequest(testVatNumber)(Future.successful(Left(InsufficientData)))

        val res = TestSignUpSubmissionController.submitSignUpRequest(testVatNumber)(FakeRequest())

        status(res) shouldBe BAD_REQUEST
      }
    }
    "the sign up service returns any other error" should {
      "return BAD_GATEWAY" in {
        mockAuthorise()(Future.successful(Unit))
        mockSubmitSignUpRequest(testVatNumber)(Future.successful(Left(EmailVerificationFailure)))

        val res = TestSignUpSubmissionController.submitSignUpRequest(testVatNumber)(FakeRequest())

        status(res) shouldBe BAD_GATEWAY
      }
    }
    "the sign up service throws an exception" should {
      "return INTERNAL_SERVER_ERROR" in {
        mockAuthorise()(Future.successful(Unit))
        mockSubmitSignUpRequest(testVatNumber)(Future.failed(new Exception()))

        val res = TestSignUpSubmissionController.submitSignUpRequest(testVatNumber)(FakeRequest())

        status(res) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
