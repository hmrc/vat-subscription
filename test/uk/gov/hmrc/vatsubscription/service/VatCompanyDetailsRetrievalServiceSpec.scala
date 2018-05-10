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

import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockGetVatCustomerInformationConnector
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.InvalidVatNumber
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class VatCompanyDetailsRetrievalServiceSpec extends UnitSpec with MockGetVatCustomerInformationConnector {

  object TestVatCompanyDetailsRetrievalService extends VatCustomerDetailsRetrievalService(
    mockGetVatCustomerInformationConnector
  )

  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(FakeRequest().headers)

  "retrieveVatCustomerDetails" should {

    "retrieve the vat customers details from valid customer information" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(testCustomerInformation)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveVatCustomerDetails(testVatNumber)
      await(res) shouldBe Right(testCustomerDetails)
    }

    "retrieve the vat customers details from valid customer information including flat rate scheme" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(testCustomerInformationWithFlatRateScheme)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveVatCustomerDetails(testVatNumber)
      await(res) shouldBe Right(testCustomerDetailsWithFlatRateScheme)
    }

    "return a failed response when the customer information cannot be retrieved" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(InvalidVatNumber)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveVatCustomerDetails(testVatNumber)
      await(res) shouldBe Left(InvalidVatNumber)
    }

  }
}
