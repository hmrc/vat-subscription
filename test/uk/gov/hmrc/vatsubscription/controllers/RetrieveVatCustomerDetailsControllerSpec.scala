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
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import uk.gov.hmrc.auth.core.authorise.RawJsonPredicate
import uk.gov.hmrc.auth.core.retrieve.{EmptyRetrieval, Retrievals}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.{InvalidVatNumber, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound}
import uk.gov.hmrc.vatsubscription.models.CustomerDetails
import uk.gov.hmrc.vatsubscription.service.mocks.MockVatCustomerDetailsRetrievalService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveVatCustomerDetailsControllerSpec extends UnitSpec with MockAuthConnector with MockVatCustomerDetailsRetrievalService {

  object TestRetrieveVatCustomerDetailsController
    extends RetrieveVatCustomerDetailsController(mockAuthConnector, mockVatCustomerDetailsRetrievalService)


  implicit private val system: ActorSystem = ActorSystem()
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  val expectedClientDetailsWithFlatRateScheme: JsValue = Json.obj(
    "firstName" -> "testFirstName",
    "lastName" ->"testLastName",
    "organisationName" -> "testOrganisationName",
    "tradingName" -> "testTradingName",
    "hasFlatRateScheme" -> true)

  val expectedClientDetails: JsValue = Json.obj(
    "firstName" -> "testFirstName",
    "lastName" ->"testLastName",
    "organisationName" -> "testOrganisationName",
    "tradingName" -> "testTradingName",
    "hasFlatRateScheme" -> false)

  val baseJson: JsObject = Json.obj("hasFlatRateScheme" -> false)

  val predicate = RawJsonPredicate(JsArray(Seq(Json.toJson(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", testVatNumber)
    .withDelegatedAuthRule("mtd-vat-auth")))))

  "retrieveVatCustomerDetails" when {
    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        mockAuthorise(predicate = predicate, retrievals = Retrievals.allEnrolments)(Future.successful(Enrolments(Set.empty)))

        val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

        status(res) shouldBe FORBIDDEN
      }
    }

    "the customer details have been successfully retrieved" should {
      "return the customer details" when {
        "the customer details are populated" in {
          mockAuthRetrieveMtdVatEnrolled(predicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(testCustomerDetails)))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe expectedClientDetails
        }
        "the customer details are empty" in {
          mockAuthRetrieveMtdVatEnrolled(predicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(CustomerDetails(None, None, None, None))))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe baseJson
        }
      }
    }
  }

  "retrieveVatCustomerDetails" when {
    "the customer details and flat rate scheme have been successfully retrieved" should {
      "return the customer details" when {
        "the customer details and flat rate scheme are populated" in {
          mockAuthRetrieveMtdVatEnrolled(predicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(testCustomerDetailsWithFlatRateScheme)))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe expectedClientDetailsWithFlatRateScheme
        }
        "the customer details are empty" in {
          mockAuthRetrieveMtdVatEnrolled(predicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(CustomerDetails(None, None, None, None))))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe baseJson
        }
      }
    }

    "the customer details could not be retrieved" when {
      "the vat number is invalid" should {
        "return a BadRequest" in {
          mockAuthRetrieveMtdVatEnrolled(predicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe BAD_REQUEST
        }
      }

      "the vat number is not found" should {
        "return a NotFound" in {
          mockAuthRetrieveMtdVatEnrolled(predicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(VatNumberNotFound)))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe NOT_FOUND
        }
      }

      "another failure occurred" should {
        "return the corresponding failure" in {
          mockAuthRetrieveMtdVatEnrolled(predicate)

          val responseBody = "error"

          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, responseBody))))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe BAD_GATEWAY
          jsonBodyOf(await(res)) shouldBe Json.obj("status" -> INTERNAL_SERVER_ERROR, "body" -> responseBody)
        }
      }
    }

  }
}