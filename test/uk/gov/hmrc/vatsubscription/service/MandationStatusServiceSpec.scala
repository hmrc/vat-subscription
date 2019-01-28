/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.vatsubscription.connectors.{Forbidden, InvalidVatNumber, Migration}
import uk.gov.hmrc.vatsubscription.helpers.PPOBTestConstants.ppobModelMax
import uk.gov.hmrc.vatsubscription.models.{CustomerDetails, MTDfBMandated, VatCustomerInformation}
import uk.gov.hmrc.vatsubscription.services.MandationStatusService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MandationStatusServiceSpec extends UnitSpec with EitherValues
  with MockGetVatCustomerInformationConnector {

  object TestMandationStatusService extends MandationStatusService(
    mockConnector
  )

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  "getMandationStatus" should {

    "return the mandation status if successful" in {
      val testSuccessfulResponse =
        VatCustomerInformation(
          mandationStatus = MTDfBMandated,
          customerDetails = CustomerDetails(
            firstName = None,
            lastName = None,
            organisationName = None,
            tradingName = None,
            vatRegistrationDate = None,
            welshIndicator = None,
            isPartialMigration = false),
          flatRateScheme = None,
          ppob = ppobModelMax,
          bankDetails = None,
          returnPeriod = None,
          deregistration = None,
          changeIndicators = None,
          pendingChanges = None
        )

      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(testSuccessfulResponse)))

      await(TestMandationStatusService.getMandationStatus(testVatNumber)) shouldBe Right(testSuccessfulResponse.mandationStatus)
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