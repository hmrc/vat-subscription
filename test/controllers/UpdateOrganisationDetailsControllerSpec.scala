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

import connectors.VatNumberNotFound
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants.testVatNumber
import helpers.CustomerDetailsTestConstants.{customerDetailsModelMax, customerDetailsModelMin}
import helpers.OrganisationDetailsTestConstants._
import helpers.TestUtil
import helpers.UpdateVatSubscriptionTestConstants.{updateConflictResponse, updateErrorModel, updateErrorResponse, updateSuccessResponse}
import models.updateVatSubscription.response.ErrorModel
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsJson, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, contentAsJson, defaultAwaitTimeout, status}
import services.mocks.{MockUpdateOrganisationDetailsService, MockVatCustomerDetailsRetrievalService}
import uk.gov.hmrc.auth.core.InsufficientEnrolments

import scala.concurrent.Future

class UpdateOrganisationDetailsControllerSpec extends TestUtil
                                              with MockVatAuthorised
                                              with MockUpdateOrganisationDetailsService
                                              with MockVatCustomerDetailsRetrievalService {

  object TestUpdateOrganisationDetailsController
    extends UpdateOrganisationDetailsController(
      mockVatAuthorised,
      mockUpdateOrganisationDetailsService,
      mockVatCustomerDetailsRetrievalService,
      controllerComponents)

  val tradingNamePostRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(tradingNameModel))
  val businessNamePostRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(businessNameModel))

  "the .updateTradingName() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        val res: Result = await(TestUpdateOrganisationDetailsController.updateTradingName(testVatNumber)(tradingNamePostRequest))
        status(Future.successful(res)) shouldBe FORBIDDEN
      }
    }

    "the user is authorised" should {

      "return a successful response" when {

        "a valid TradingName is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))
          mockUpdateTradingName(tradingNameModel, customerDetailsModelMax)(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateOrganisationDetailsController.updateTradingName(testVatNumber)(tradingNamePostRequest))

          status(Future.successful(res)) shouldBe OK
          contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "the UpdateVatSubscription service response has no individual or org names" should {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMin)))
          mockUpdateTradingName(tradingNameModel, customerDetailsModelMin)(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateOrganisationDetailsController.updateTradingName(testVatNumber)(tradingNamePostRequest))

          "return status INTERNAL_SERVER_ERROR" in {
            status(Future.successful(res)) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(ErrorModel("INTERNAL_SERVER_ERROR", "The service returned " +
            "CustomerDetails with no defined individual or org names"))
          }
        }

        "no json body is supplied for the PUT" should {

          val emptyRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateTradingName(testVatNumber)(emptyRequest))

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(Future.successful(res)) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${emptyRequest.body}"))
          }
        }

        "a valid TradingName is supplied but a Conflict error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateTradingName(testVatNumber)(tradingNamePostRequest))

          "return status CONFLICT (409)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))
            mockUpdateTradingName(tradingNameModel, customerDetailsModelMax)(Future.successful(Left(updateConflictResponse)))
            status(Future.successful(res)) shouldBe CONFLICT
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateConflictResponse)
          }
        }

        "a valid TradingName is supplied but an error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateTradingName(testVatNumber)(tradingNamePostRequest))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))
            mockUpdateTradingName(tradingNameModel, customerDetailsModelMax)(Future.successful(Left(updateErrorResponse)))
            status(Future.successful(res)) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateErrorResponse)
          }
        }

        "a valid TradingName is supplied but a VatNumberNotFound error is returned" should {

          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateTradingName(testVatNumber)(tradingNamePostRequest))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(VatNumberNotFound)))
            mockUpdateTradingName(tradingNameModel, customerDetailsModelMax)(Future(Left(updateErrorResponse)))
            status(Future.successful(res)) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateErrorModel)
          }
        }
      }
    }

  }

  "the.updateBusinessName() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        val res: Result = await(TestUpdateOrganisationDetailsController.updateBusinessName(testVatNumber)(businessNamePostRequest))
        status(Future.successful(res)) shouldBe FORBIDDEN
      }
    }

    "The user is authorised" should {

      "return a successful response" when {

        "a valid BusinessName is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))
          mockUpdateBusinessName(businessNameModel)(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateOrganisationDetailsController.updateBusinessName(testVatNumber)(businessNamePostRequest))

          status(Future.successful(res)) shouldBe OK
          contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "no json body is supplied for the PUT" should {

          val emptyRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateBusinessName(testVatNumber)(emptyRequest))

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(Future.successful(res)) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${emptyRequest.body}"))
          }
        }

        "a valid BusinessName is supplied but a Conflict error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateBusinessName(testVatNumber)(businessNamePostRequest))

          "return status CONFLICT (409)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))
            mockUpdateBusinessName(businessNameModel)(Future.successful(Left(updateConflictResponse)))
            status(Future.successful(res)) shouldBe CONFLICT
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateConflictResponse)
          }
        }

        "a valid BusinessName is supplied but an error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateBusinessName(testVatNumber)(businessNamePostRequest))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Right(customerDetailsModelMax)))
            mockUpdateBusinessName(businessNameModel)(Future.successful(Left(updateErrorResponse)))
            status(Future.successful(res)) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateErrorResponse)
          }
        }

        "a valid BusinessName is supplied but an error is returned instead of customer details" should {

          lazy val res: Result = await(TestUpdateOrganisationDetailsController.updateBusinessName(testVatNumber)(businessNamePostRequest))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveVatCustomerDetails(testVatNumber)(Future.successful(Left(VatNumberNotFound)))
            mockUpdateBusinessName(businessNameModel)(Future.successful(Left(updateErrorResponse)))
            status(Future.successful(res)) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            contentAsJson(Future.successful(res)) shouldBe Json.toJson(updateErrorModel)
          }
        }
      }
    }
  }
}
