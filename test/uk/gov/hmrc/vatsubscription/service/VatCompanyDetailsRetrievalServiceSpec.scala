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
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.CustomerDetailsTestConstants._
import uk.gov.hmrc.vatsubscription.connectors.InvalidVatNumber
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class VatCompanyDetailsRetrievalServiceSpec extends UnitSpec with MockGetVatCustomerInformationConnector {

  object TestVatCompanyDetailsRetrievalService extends VatCustomerDetailsRetrievalService(
    mockConnector
  )

  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(FakeRequest().headers)

  "retrieveVatCustomerDetails" should {

    "retrieve the vat customers details from valid customer information" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMin)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveVatCustomerDetails(testVatNumber)
      await(res) shouldBe Right(customerDetailsModelMin)
    }

    "retrieve the vat customers details from valid customer information including flat rate scheme" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMaxWithFRS)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveVatCustomerDetails(testVatNumber)
      await(res) shouldBe Right(customerDetailsModelMaxWithFRS)
    }

    "return a failed response when the customer information cannot be retrieved" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(InvalidVatNumber)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveVatCustomerDetails(testVatNumber)
      await(res) shouldBe Left(InvalidVatNumber)
    }

  }

  "retrieveVatCustomerInformation" should {

    "retrieve the all the vat customers information" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMax)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveCircumstanceInformation(testVatNumber)
      await(res) shouldBe Right(customerInformationModelMax)
    }

    "retrieve the vat customers details from valid customer information including flat rate scheme" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMaxWithFRS)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveCircumstanceInformation(testVatNumber)
      await(res) shouldBe Right(customerInformationModelMaxWithFRS)
    }

    "return a failed response when the customer information cannot be retrieved" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(InvalidVatNumber)))
      val res = TestVatCompanyDetailsRetrievalService.retrieveCircumstanceInformation(testVatNumber)
      await(res) shouldBe Left(InvalidVatNumber)
    }

  }
}
