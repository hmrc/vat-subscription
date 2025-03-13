/*
 * Copyright 2024 HM Revenue & Customs
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
import helpers.StandingRequestScheduleConstants._
import helpers.TestUtil
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import services.mocks.MockStandingRequestScheduleRetrievalService
import scala.concurrent.Future

class RetrieveStandingRequestScheduleControllerSpec extends TestUtil
  with MockVatAuthorised
  with MockStandingRequestScheduleRetrievalService {

  object TestRetrieveStandingRequestScheduleController
    extends RetrieveStandingRequestScheduleController(mockVatAuthorised,
                                                mockStandingRequestScheduleRetrievalService,
                                                controllerComponents)

  "the retrieveStandingRequestSchedule method" when {

    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

        val res: Future[Result] = TestRetrieveStandingRequestScheduleController.retrieveStandingRequestScheduleDetails(testVatNumber)(FakeRequest())

        status(res) shouldBe FORBIDDEN
      }
    }

    "the standing requests schedule could not be retrieved" when {
      "the vat number is invalid" should {
        "return a BadRequest" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveStandingRequestsSchedule(testVatNumber)(Future.successful(Left(SrsInvalidVatNumber)))

          val res: Future[Result] = TestRetrieveStandingRequestScheduleController.retrieveStandingRequestScheduleDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe BAD_REQUEST
          contentAsJson(res) shouldBe Json.toJson(InvalidVatNumber)
        }
      }

      "the vat number is not found" should {
        "return a NotFound" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveStandingRequestsSchedule(testVatNumber)(Future.successful(Left(SrsVatNumberNotFound)))

          val res: Future[Result] = TestRetrieveStandingRequestScheduleController.retrieveStandingRequestScheduleDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe NOT_FOUND
          contentAsJson(res) shouldBe Json.toJson(VatNumberNotFound)
        }
      }

      "response status is Forbidden with no json body" should {
        "return a FORBIDDEN" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRetrieveStandingRequestsSchedule(testVatNumber)(Future.successful(Left(SrsForbidden)))

          val res: Future[Result] = TestRetrieveStandingRequestScheduleController.retrieveStandingRequestScheduleDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe FORBIDDEN
          contentAsJson(res) shouldBe Json.toJson(Forbidden)
        }
      }

      "the user does not have an mtd vat enrolment" should {
        "return FORBIDDEN" in {
          mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          val res: Future[Result] = TestRetrieveStandingRequestScheduleController.retrieveStandingRequestScheduleDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe FORBIDDEN
        }
      }

      "the standing requests schedule have been successfully retrieved" should {
        "return the standing requests schedule" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockRetrieveStandingRequestsSchedule(testVatNumber)(Future.successful(Right(standingRequestEmptyStandingRequestsModel)))

            val res: Future[Result] = TestRetrieveStandingRequestScheduleController.retrieveStandingRequestScheduleDetails(testVatNumber)(FakeRequest())

            status(res) shouldBe OK
            contentAsJson(res) shouldBe standingRequestEmptyJson
        }
      } 

      "another failure occurred" should {
        "return the corresponding failure" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

          val responseBody = "error"

          mockRetrieveStandingRequestsSchedule(testVatNumber)(Future.successful(Left(UnexpectedStandingRequestScheduleFailure(INTERNAL_SERVER_ERROR, responseBody))))

          val res: Future[Result] = TestRetrieveStandingRequestScheduleController.retrieveStandingRequestScheduleDetails(testVatNumber)(FakeRequest())

          status(res) shouldBe INTERNAL_SERVER_ERROR
          contentAsJson(res) shouldBe Json.obj("status" -> INTERNAL_SERVER_ERROR.toString, "body" -> responseBody)
        }
      }
    }
  }
}
