/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.actions

import play.api.http.Status._
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.auth.core.authorise.{EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments, InsufficientEnrolments}
import config.Constants
import connectors.mocks.MockAuthConnector
import helpers.BaseTestConstants.{testMtdVatEnrolment, testVatNumber}
import assets.TestUtil

import scala.concurrent.Future


class VatAuthorisedSpec extends TestUtil with MockAuthConnector {

  object TestVatAuthorised extends VatAuthorised(mockAuthConnector, controllerComponents, mockAppConfig)

  def mockAuthRetrieveCredentialsNone(predicate: Predicate = EmptyPredicate): Unit =
    mockAuthorise(predicate = predicate, retrievals = retrievals)(
      Future.successful(
        new ~ (Enrolments(Set(testMtdVatEnrolment)), None)
      )
    )

  def result: Future[Result] = TestVatAuthorised.async(testVatNumber) {
    _ =>
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
      "unable to retrieve Credentials.providerId from auth profile" should {

        "Return a forbidden response" in {
          mockAuthRetrieveCredentialsNone(authPredicate)
          status(result) shouldBe FORBIDDEN
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
