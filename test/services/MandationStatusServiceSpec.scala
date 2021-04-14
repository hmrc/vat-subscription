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

package services

import org.scalatest.EitherValues
import uk.gov.hmrc.play.test.UnitSpec
import connectors.mocks.MockGetVatCustomerInformationConnector
import helpers.BaseTestConstants._
import connectors.{Forbidden, InvalidVatNumber, Migration}
import helpers.CustomerInformationTestConstants.customerInformationModelMin

import scala.concurrent.Future

class MandationStatusServiceSpec extends UnitSpec with EitherValues
  with MockGetVatCustomerInformationConnector {

  object TestMandationStatusService extends MandationStatusService(
    mockConnector
  )

  "getMandationStatus" should {

    "return the mandation status if successful" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMin)))

      await(TestMandationStatusService.getMandationStatus(testVatNumber)) shouldBe Right(customerInformationModelMin.mandationStatus)
    }

    "return a Forbidden if forbidden returned" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(Forbidden)))

      await(TestMandationStatusService.getMandationStatus(testVatNumber)) shouldBe Left(Forbidden)
    }

    "return a Migration if migration returned" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(Migration)))

      await(TestMandationStatusService.getMandationStatus(testVatNumber)) shouldBe Left(Migration)
    }

    "return the failure if unsuccessful" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

      await(TestMandationStatusService.getMandationStatus(testVatNumber)) shouldBe Left(InvalidVatNumber)
    }
  }

}
