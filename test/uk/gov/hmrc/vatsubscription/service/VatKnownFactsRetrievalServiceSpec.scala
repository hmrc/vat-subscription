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
import uk.gov.hmrc.vatsubscription.helpers.VatKnownFactsTestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers
import uk.gov.hmrc.vatsubscription.services.VatKnownFactsRetrievalService._
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatKnownFactsRetrievalServiceSpec extends UnitSpec with MockGetVatCustomerInformationConnector {

  object TestVatKnownFactsRetrievalService extends VatKnownFactsRetrievalService(
    mockConnector
  )

  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(FakeRequest().headers)

  "getVatKnownFacts" should {

    "retrieve the known facts if they exists in the vat customers details " in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMaxWithFRS)))
      val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
      await(res) shouldBe Right(vatKnownFacts)
    }

    "return a failure if any of the known facts are absent in the vat customer details" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMin)))
      val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
      await(res) shouldBe Left(InvalidVatKnownFacts)
    }

    "return a failure if the connection to get vat customer details fails" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(httpparsers.InvalidVatNumber)))
      val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
      await(res) shouldBe Left(InvalidVatNumber)
    }

  }

}
