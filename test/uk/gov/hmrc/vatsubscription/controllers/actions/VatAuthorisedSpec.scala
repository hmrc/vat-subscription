/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.controllers.actions

import assets.TestUtil
import play.api.http.Status._
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.{Enrolment, InsufficientEnrolments}
import uk.gov.hmrc.vatsubscription.config.Constants
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants.testVatNumber

import scala.concurrent.Future


class VatAuthorisedSpec extends TestUtil with MockAuthConnector {

  object TestVatAuthorised extends VatAuthorised(mockAuthConnector, mockAppConfig)

  def result: Future[Result] = TestVatAuthorised.async(testVatNumber) {
    implicit user =>
      Future.successful(Ok)
  }(ec)(fakeRequest)

  val authPredicate: Predicate =
    Enrolment(Constants.MtdVatEnrolmentKey)
      .withIdentifier(Constants.MtdVatReferenceKey, testVatNumber)
      .withDelegatedAuthRule(Constants.MtdVatDelegatedAuth)

  "The VatAuthorised.async method" should {

    "For a User" when {

      "an authorised result is returned from the Auth Connector" should {

        "Successfully authenticate and process the request" in {
          mockAuthRetrieveMtdVatEnrolled(authPredicate)
          status(result) shouldBe OK
        }
      }

      "an authorisation exception is returned from the Auth Connector" should {

        "Return a forbidden response" in {
          mockAuthorise(authPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
          status(result) shouldBe FORBIDDEN
        }
      }
    }


    "For an Agent" when {

      "they are Signed Up to MTD VAT" should {

        "Successfully authenticate and process the request" in {
          mockAuthRetrieveAgentServicesEnrolled(authPredicate)
          status(result) shouldBe OK
        }
      }

      "they are NOT Signed Up to MTD VAT" should {

        "Return a forbidden response" in {
          mockAuthorise(authPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
          status(result) shouldBe FORBIDDEN
        }
      }
    }
  }
}
