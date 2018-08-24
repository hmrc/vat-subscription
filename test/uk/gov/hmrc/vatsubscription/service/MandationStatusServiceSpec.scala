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

package uk.gov.hmrc.vatsubscription.service


import org.scalatest.EitherValues
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockGetVatCustomerInformationConnector
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.InvalidVatNumber
import uk.gov.hmrc.vatsubscription.models.{CustomerDetails, MTDfBMandated, VatCustomerInformation}
import uk.gov.hmrc.vatsubscription.services.MandationStatusService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MandationStatusServiceSpec extends UnitSpec with EitherValues
  with MockGetVatCustomerInformationConnector {

  object TestMandationStatusService extends MandationStatusService(
    mockGetVatCustomerInformationConnector
  )

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  "getMandationStatus" should {

    "return the mandation status if successful" in {
      val testSuccessfulResponse = VatCustomerInformation(MTDfBMandated, CustomerDetails(None, None, None, None, None), None, None, None, None, None, None)

      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(testSuccessfulResponse)))

      await(TestMandationStatusService.getMandationStatus(testVatNumber)) shouldBe Right(testSuccessfulResponse.mandationStatus)
    }

    "return the failure if unsuccessful" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(InvalidVatNumber)))

      await(TestMandationStatusService.getMandationStatus(testVatNumber)) shouldBe Left(InvalidVatNumber)
    }
  }

}
