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
  import assets.TestUtil
  import play.api.http.Status._
  import play.api.libs.json.Json
  import play.api.mvc.Result
  import play.api.test.FakeRequest
  import uk.gov.hmrc.auth.core.authorise.Predicate
  import uk.gov.hmrc.auth.core.retrieve.{Retrieval, Retrievals}
  import uk.gov.hmrc.auth.core.{Enrolment, Enrolments, InsufficientEnrolments}
  import uk.gov.hmrc.vatsubscription.controllers.actions.mocks.MockVatAuthorised
  import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants._
  import uk.gov.hmrc.vatsubscription.helpers.CustomerDetailsTestConstants._
  import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants._
  import uk.gov.hmrc.vatsubscription.httpparsers.{InvalidVatNumber, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound}
  import uk.gov.hmrc.vatsubscription.models.CustomerDetails
  import uk.gov.hmrc.vatsubscription.service.mocks.MockVatCustomerDetailsRetrievalService

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future

  class RetrieveVatCustomerDetailsControllerSpec extends TestUtil with MockVatAuthorised with MockVatCustomerDetailsRetrievalService {

    object TestRetrieveVatCustomerDetailsController
      extends RetrieveVatCustomerDetailsController(mockVatAuthorised, mockVatCustomerDetailsRetrievalService)


    implicit private val system: ActorSystem = ActorSystem()
    implicit private val materializer: ActorMaterializer = ActorMaterializer()

    val predicate: Predicate = Enrolment("HMRC-MTD-VAT")
      .withIdentifier("VRN", testVatNumber)
      .withDelegatedAuthRule("mtd-vat-auth")

    val retrievals: Retrieval[Enrolments] = Retrievals.allEnrolments

    "the retrieveVatCustomerDetails method" when {

      "the user does not have an mtd vat enrolment" should {
        "return FORBIDDEN" in {
          mockAuthorise(predicate, retrievals)(Future.failed(InsufficientEnrolments()))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

          status(res) shouldBe FORBIDDEN
        }
      }

      "the customer details have been successfully retrieved" should {
        "return the customer details" when {
          "the customer details are populated" in {

            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

            status(res) shouldBe OK
            jsonBodyOf(res) shouldBe customerDetailsJsonMax
          }

          "the customer details and flat rate scheme are populated" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMaxWithFRS)))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

            status(res) shouldBe OK
            jsonBodyOf(res) shouldBe customerDetailsJsonMaxWithFRS
          }
          "the customer details are empty" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(CustomerDetails(None, None, None, None))))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest()))

            status(res) shouldBe OK
            jsonBodyOf(res) shouldBe customerDetailsJsonMin
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


    "the retrieveVatInformation (all details) method" when {

      "the user does not have an mtd vat enrolment" should {
        "return FORBIDDEN" in {
          mockAuthorise(predicate, retrievals)(Future.failed(InsufficientEnrolments()))

          val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest()))

          status(res) shouldBe FORBIDDEN
        }
      }

      "the customer details have been successfully retrieved" should {
        "return the customer details" when {
          "the customer details are populated" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMax)))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest()))

            status(res) shouldBe OK
            jsonBodyOf(res) shouldBe customerInformationOutputJsonMax
          }

          "the customer details and flat rate scheme are populated" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMaxWithFRS)))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest()))

            status(res) shouldBe OK
            jsonBodyOf(res) shouldBe customerInformationOutputJsonMaxWithFRS
          }
          "the customer details are empty" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMin)))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest()))

            status(res) shouldBe OK
            jsonBodyOf(res) shouldBe customerInformationOutputJsonMin
          }
        }
      }

      "the customer details could not be retrieved" when {
        "the vat number is invalid" should {
          "return a BadRequest" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest()))

            status(res) shouldBe BAD_REQUEST
          }
        }

        "the vat number is not found" should {
          "return a NotFound" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(VatNumberNotFound)))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest()))

            status(res) shouldBe NOT_FOUND
          }
        }

        "another failure occurred" should {
          "return the corresponding failure" in {
            mockAuthRetrieveMtdVatEnrolled(predicate)

            val responseBody = "error"

            mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, responseBody))))

            val res: Result = await(TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest()))

            status(res) shouldBe BAD_GATEWAY
            jsonBodyOf(await(res)) shouldBe Json.obj("status" -> INTERNAL_SERVER_ERROR, "body" -> responseBody)
          }
        }
      }
    }

  }