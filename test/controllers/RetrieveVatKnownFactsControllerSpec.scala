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

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import controllers.actions.mocks.MockVatAuthorised
import helpers.BaseTestConstants._
import helpers.VatKnownFactsTestConstants._
import service.mocks.MockVatKnownFactsRetrievalService
import services.VatKnownFactsRetrievalService._
import assets.TestUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveVatKnownFactsControllerSpec extends TestUtil with MockVatAuthorised with MockVatKnownFactsRetrievalService {

  object TestRetrieveVatKnownFactsController
    extends RetrieveVatKnownFactsController(mockVatKnownFactsRetrievalService, controllerComponents)

  "retrieveVatKnownFacts" should {
    "return OK and Deregistered" when {
      "the deregistered object is returned from GetVatCustomerInformation" in {
        val testJson = Json.obj("deregistered" -> true)
        mockAuthorise()(Future.successful(Unit))
        mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Right(DeregisteredUser)))

        val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

        status(res) shouldBe OK
        jsonBodyOf(res) shouldBe testJson
      }
    }

    "return OK and the known facts" when {
      "the known facts is returned from the VatKnownFactsRetrievalService" when {
        "the user is not overseas" in {
          mockAuthorise()(Future.successful(Unit))

          mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Right(vatKnownFacts(isOverseas = false))))

          val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe vatKnownFactsJson(isOverseas = false)
        }

        "the user is overseas" in {
          mockAuthorise()(Future.successful(Unit))

          mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Right(vatKnownFacts(isOverseas = true))))

          val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

          status(res) shouldBe OK
          jsonBodyOf(res) shouldBe vatKnownFactsJson(isOverseas = true)
        }
      }
    }

    "return BAD_REQUEST" when {
      "InvalidVatNumber is returned from the VatKnownFactsRetrievalService" in {

        mockAuthorise()(Future.successful(Unit))

        mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

        val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

        status(res) shouldBe BAD_REQUEST
      }
    }
    "return NOT_FOUND" when {
      "VatNumberNotFound is returned from the VatKnownFactsRetrievalService" in {

        mockAuthorise()(Future.successful(Unit))

        mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Left(VatNumberNotFound)))

        val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

        status(res) shouldBe NOT_FOUND
      }
    }

    "return FORBIDDEN" when {
      "Forbidden with no json body is returned from the from VatKnownFactsRetrievalService" in {
        mockAuthorise()(Future.successful(Unit))

        mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Left(Forbidden)))

        val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

        status(res) shouldBe FORBIDDEN
      }
    }

    "return PRECONDITION_FAILED" when {
      "Forbidden with MIGRATION code in json body is returned from VatKnownFactsRetrievalService" in {

        mockAuthorise()(Future.successful(Unit))

        mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Left(Migration)))

        val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))
        status(res) shouldBe PRECONDITION_FAILED
      }
    }

    "return BAD_GATEWAY" when {
      "InvalidVatKnownFacts is returned from the VatKnownFactsRetrievalService" in {

        mockAuthorise()(Future.successful(Unit))

        mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Left(InvalidVatKnownFacts)))

        val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

        status(res) shouldBe BAD_GATEWAY
      }
    }

    "return the correct status and error message" when {
      "UnexpectedGetVatCustomerInformationFailure is returned from the VatKnownFactsRetrievalService" in {
        val responseBody = "error"

        mockAuthorise()(Future.successful(Unit))

        mockRetrieveVatKnownFacts(testVatNumber)(Future.successful(Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, responseBody))))

        val res: Result = await(TestRetrieveVatKnownFactsController.retrieveVatKnownFacts(testVatNumber)(FakeRequest()))

        status(res) shouldBe INTERNAL_SERVER_ERROR
        jsonBodyOf(await(res)) shouldBe Json.obj("status" -> INTERNAL_SERVER_ERROR, "body" -> responseBody)
      }
    }
  }
}
