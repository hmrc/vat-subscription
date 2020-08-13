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

package controllers

import play.api.libs.json.Json
import play.api.http.Status._
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsJson, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import connectors.VatNumberNotFound
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants.testVatNumber
import helpers.UpdateVatSubscriptionTestConstants.{updateErrorResponse, updateSuccessResponse}
import models._
import models.updateVatSubscription.response.ErrorModel
import service.mocks.{MockUpdateReturnPeriodService, MockVatCustomerDetailsRetrievalService}
import assets.TestUtil

import scala.concurrent.Future

class UpdateReturnPeriodControllerSpec extends TestUtil with MockVatAuthorised
  with MockUpdateReturnPeriodService with MockVatCustomerDetailsRetrievalService {

  object TestUpdateReturnPeriodController extends UpdateReturnPeriodController(
    mockVatAuthorised,
    mockUpdateReturnPeriodService,
    mockVatCustomerDetailsRetrievalService,
    controllerComponents)

  val maRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MAReturnPeriod(None, None, None)))
  val mbRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MBReturnPeriod(None, None, None)))
  val mcRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MCReturnPeriod(None, None, None)))
  val mmRequest: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.toJson(MMReturnPeriod(None, None, None)))

  "the.updateVatReturnPeriod() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(maRequest))
        status(res) shouldBe FORBIDDEN
      }
    }

    "The user is authorised" should {

      "return a successful response" when {

        "the 'MA' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateReturnPeriod(MAReturnPeriod(None, None, None))(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(maRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "the 'MB' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateReturnPeriod(MBReturnPeriod(None, None, None))(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(mbRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "the 'MC' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateReturnPeriod(MCReturnPeriod(None, None, None))(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(mcRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }

        "the 'MM' return period is supplied and the response from the UpdateVatSubscription service is successful" in {

          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
          mockUpdateReturnPeriod(MMReturnPeriod(None, None, None))(Future.successful(Right(updateSuccessResponse)))

          val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(mmRequest))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe Json.toJson(updateSuccessResponse)
        }
      }

      "return an error response" when {

        "no json body is supplied for the PUT" should {

          val unknownReturnPeriodRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
          lazy val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(unknownReturnPeriodRequest))

          "return status BAD_REQUEST (400)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            status(res) shouldBe BAD_REQUEST
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${unknownReturnPeriodRequest.body}"))
          }
        }

        "a valid return period is supplied but an error is returned from the UpdateVatSubscription Service" should {

          lazy val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(maRequest))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Right(false)))
            mockUpdateReturnPeriod(MAReturnPeriod(None, None, None))(Future.successful(Left(updateErrorResponse)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(updateErrorResponse)
          }
        }

        "a valid return period is supplied but an error is returned instead of a welshIndicator" should {

          lazy val res: Result = await(TestUpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber)(maRequest))

          "return status INTERNAL_SERVER_ERROR (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockExtractWelshIndicator(testVatNumber)(Future(Left(VatNumberNotFound)))
            status(res) shouldBe INTERNAL_SERVER_ERROR
          }

          "return the expected error model" in {
            jsonBodyOf(res) shouldBe Json.toJson(VatNumberNotFound)
          }
        }
      }
    }
  }

}
