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
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.{InvalidVatNumber, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound}
import uk.gov.hmrc.vatsubscription.models.MTDfBMandated
import uk.gov.hmrc.vatsubscription.service.mocks.MockMandationStatusService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MandationStatusControllerSpec extends UnitSpec
  with MockAuthConnector with MockMandationStatusService {

  object TestMandationStatusController
    extends MandationStatusController(mockAuthConnector, mockMandationStatusService)

  implicit private val system: ActorSystem = ActorSystem()
  implicit private val materializer: ActorMaterializer = ActorMaterializer()


  "getMandationStatus" should {
    "return OK when successful" in {
      mockAuthorise()(Future.successful(Unit))

      val testStatus = MTDfBMandated
      mockGetMandationStatus(testVatNumber)(Future.successful(Right(testStatus)))

      val res = TestMandationStatusController.getMandationStatus(testVatNumber)(FakeRequest())
      status(res) shouldBe OK

      jsonBodyOf(await(res)) shouldBe Json.obj(TestMandationStatusController.mandationStatusKey -> testStatus.toString)
    }

    "return the BAD_REQUEST when InvalidVatNumber" in {
      mockAuthorise()(Future.successful(Unit))

      val testStatus = MTDfBMandated
      mockGetMandationStatus(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

      val res = TestMandationStatusController.getMandationStatus(testVatNumber)(FakeRequest())
      status(res) shouldBe BAD_REQUEST
    }

    "return the NOT_FOUND when VatNumberNotFound" in {
      mockAuthorise()(Future.successful(Unit))

      val testStatus = MTDfBMandated
      mockGetMandationStatus(testVatNumber)(Future.successful(Left(VatNumberNotFound)))

      val res = TestMandationStatusController.getMandationStatus(testVatNumber)(FakeRequest())
      status(res) shouldBe NOT_FOUND
    }

    "return the INTERNAL_SERVER_ERROR and the error when failed unexpectedly" in {
      mockAuthorise()(Future.successful(Unit))

      val testStatus = MTDfBMandated
      mockGetMandationStatus(testVatNumber)(Future.successful(Left(UnexpectedGetVatCustomerInformationFailure(OK, "failure"))))

      val res = TestMandationStatusController.getMandationStatus(testVatNumber)(FakeRequest())
      status(res) shouldBe INTERNAL_SERVER_ERROR

      jsonBodyOf(await(res)) shouldBe Json.obj("status" -> OK, "body" -> "failure")
    }
  }

}
