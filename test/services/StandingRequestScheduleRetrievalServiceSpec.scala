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

package services

import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import helpers.BaseTestConstants._
import helpers.StandingRequestScheduleConstants._
import models.User
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import connectors.mocks.MockStandingRequestScheduleConnector
import scala.concurrent.Future
import connectors.{SrsInvalidVatNumber}

class StandingRequestScheduleRetrievalServiceSpec extends AnyWordSpecLike with Matchers with MockStandingRequestScheduleConnector {

  object TestStandingRequestScheduleRetrievalService extends StandingRequestScheduleRetrievalService(
    mockConnector
  )
  implicit  val testUser: User[_] = User(testVatNumber, None, testCredentials.providerId)(fakeRequest)
  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(FakeRequest())

  "retrieveStandingRequestSchedule" should {

    "retrieve the standing request schedule from valid vat number" in {
      mockStandingRequestScheduleConnector(testVatNumber, Future.successful(Right(standingRequestScheduleModel)))
      val res = TestStandingRequestScheduleRetrievalService.retrieveStandingRequestSchedule(testVatNumber)
      await(res) shouldBe Right(standingRequestScheduleModel)
    }

    "retrieve only requestCategory '3' standing requests schedule from valid vat number" in {
      mockStandingRequestScheduleConnector(testVatNumber, Future.successful(Right(standingRequestMultipleModel)))
      val res = TestStandingRequestScheduleRetrievalService.retrieveStandingRequestSchedule(testVatNumber)
      await(res) shouldBe Right(standingRequestCategory3ModelSingle)
    }

    "return failed response when no requestCategory '3' standing requests schedule from valid vat number" in {
      mockStandingRequestScheduleConnector(testVatNumber, Future.successful(Right(standingRequestNonPOACategoryModel)))
      val res = TestStandingRequestScheduleRetrievalService.retrieveStandingRequestSchedule(testVatNumber)
      await(res) shouldBe Right(standingRequestEmptyStandingRequestsModel)
    }

    "return a failed response when the customer information cannot be retrieved" in {
      mockStandingRequestScheduleConnector(testVatNumber, Future.successful(Left(SrsInvalidVatNumber)))
      val res = TestStandingRequestScheduleRetrievalService.retrieveStandingRequestSchedule(testVatNumber)
      await(res) shouldBe Left(SrsInvalidVatNumber)
    }
  }
}