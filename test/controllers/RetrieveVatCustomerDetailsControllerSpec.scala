/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import connectors._
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants._
import helpers.CustomerDetailsTestConstants._
import helpers.CustomerInformationTestConstants._
import helpers.TestUtil
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import services.mocks.MockVatCustomerDetailsRetrievalService
import scala.concurrent.Future

class RetrieveVatCustomerDetailsControllerSpec extends TestUtil
  with MockVatAuthorised
  with MockVatCustomerDetailsRetrievalService {

  object TestRetrieveVatCustomerDetailsController
    extends RetrieveVatCustomerDetailsController(mockVatAuthorised,
                                                mockVatCustomerDetailsRetrievalService,
                                                mockAppConfig,
                                                controllerComponents)

  "the retrieveVatCustomerDetails method" when {

    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

        val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

        status(res) shouldBe FORBIDDEN
      }
    }

    "the customer details have been successfully retrieved" should {
      "return the customer details" when {
        "the customer details are populated" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe OK
          contentAsJson(res) shouldBe customerDetailsJsonMax
        }

        "the customer details and flat rate scheme are populated" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMaxWithFRS)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe OK
          contentAsJson(res) shouldBe customerDetailsJsonMaxWithFRS
        }
        "the customer details are empty" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMin)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe OK
          contentAsJson(res) shouldBe customerDetailsJsonMin
        }
      }
    }

    "the customer details could not be retrieved" when {
      "the vat number is invalid" should {
        "return a BadRequest" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe BAD_REQUEST
          contentAsJson(res) shouldBe Json.toJson(InvalidVatNumber)
        }
      }

      "the vat number is not found" should {
        "return a NotFound" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(VatNumberNotFound)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe NOT_FOUND
          contentAsJson(res) shouldBe Json.toJson(VatNumberNotFound)
        }
      }

      "response status is Forbidden with no json body" should {
        "return a FORBIDDEN" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(Forbidden)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe FORBIDDEN
          contentAsJson(res) shouldBe Json.toJson(Forbidden)
        }
      }

      "response status is Forbidden with MIGRATION code in json body" should {
        "return a PRECONDITION_FAILED" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(Migration)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe PRECONDITION_FAILED
          contentAsJson(res) shouldBe Json.toJson(Migration)
        }


        "another failure occurred" should {
          "return the corresponding failure" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

            val responseBody = "error"

            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(
              UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, responseBody)
            )))

            val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber)(FakeRequest())

            status(res) shouldBe INTERNAL_SERVER_ERROR
            contentAsJson(res) shouldBe Json.obj("status" -> INTERNAL_SERVER_ERROR.toString, "body" -> responseBody)
          }
        }
      }
    }

    "the retrieveVatInformation (all details) method" when {

      "the user does not have an mtd vat enrolment" should {
        "return FORBIDDEN" in {
          mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())

          status(res) shouldBe FORBIDDEN
        }
      }

      "the customer details have been successfully retrieved" should {
        "return the customer details" when {
          "the customer details are populated" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMax)))

            val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())

            status(res) shouldBe OK
            contentAsJson(res) shouldBe customerInformationOutputJsonMax
          }
          "the customer details are populated, with true overseas indicator" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMaxWithTrueOverseas)))

            val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())

            status(res) shouldBe OK
            contentAsJson(res) shouldBe customerInformationOutputJsonMaxWithTrueOverseas
          }

          "the customer details and flat rate scheme are populated" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMaxWithFRS)))

            val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())

            status(res) shouldBe OK
            contentAsJson(res) shouldBe customerInformationOutputJsonMaxWithFRS
          }
          "the customer details are empty" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Right(customerInformationModelMin)))

            val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())

            status(res) shouldBe OK
            contentAsJson(res) shouldBe customerInformationOutputJsonMin
          }
        }
      }

      "the customer details could not be retrieved" when {
        "the vat number is invalid" should {
          "return a BadRequest" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

            val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())
            status(res) shouldBe BAD_REQUEST
            contentAsJson(res) shouldBe Json.toJson(InvalidVatNumber)
          }
        }
      }

      "the vat number is not found" should {
        "return a NotFound" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(VatNumberNotFound)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())
          status(res) shouldBe NOT_FOUND
          contentAsJson(res) shouldBe Json.toJson(VatNumberNotFound)
        }
      }

      "response status is Forbidden with no json body" should {
        "return a FORBIDDEN" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(Forbidden)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())
          status(res) shouldBe FORBIDDEN
          contentAsJson(res) shouldBe Json.toJson(Forbidden)
        }
      }

      "response status is Forbidden with MIGRATION code in json body" should {
        "return a PRECONDITION_FAILED" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(Migration)))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())
          status(res) shouldBe PRECONDITION_FAILED
          contentAsJson(res) shouldBe Json.toJson(Migration)
        }
      }

      "another failure occurred" should {
        "return the corresponding failure" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

          val responseBody = "error"

          mockRetrieveVatInformation(testVatNumber)(Future.successful(Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, responseBody))))

          val res: Future[Result] = TestRetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber)(FakeRequest())

          status(res) shouldBe INTERNAL_SERVER_ERROR
          contentAsJson(res) shouldBe Json.obj("status" -> INTERNAL_SERVER_ERROR.toString, "body" -> responseBody)
        }
      }
    }
  }
}
