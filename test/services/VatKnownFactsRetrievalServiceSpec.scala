/*
 * Copyright 2023 HM Revenue & Customs
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
import connectors.mocks.MockGetVatCustomerInformationConnector
import helpers.BaseTestConstants._
import helpers.CustomerInformationTestConstants._
import helpers.VatKnownFactsTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import services.VatKnownFactsRetrievalService._
import scala.concurrent.Future


class VatKnownFactsRetrievalServiceSpec extends AnyWordSpecLike with Matchers with MockGetVatCustomerInformationConnector {

  object TestVatKnownFactsRetrievalService extends VatKnownFactsRetrievalService(mockConnector)

  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(FakeRequest())

  "getVatKnownFacts" should {

    "return DeregisteredUser" when {

      "the deregistration object exists in the json supplied by GetVatCustomerInformation" in {
        mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMax)))
        val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
        await(res) shouldBe Right(DeregisteredUser)
      }
    }

    "return the VatKnownFacts model" when {
      "the known facts exist in the vat customers details" in {
        mockGetVatCustomerInformationConnector(testVatNumber)(
          Future.successful(Right(customerInformationModelMaxWithFRS.copy(deregistration = None))))
        val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
        await(res) shouldBe Right(vatKnownFacts(isOverseas = false))
      }

      "the known facts exist in the vat customers details and the overseas indicator is true" in {
        mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMinWithOverseas)))
        val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
        await(res) shouldBe Right(vatKnownFacts(isOverseas = true))
      }
    }

    "return InvalidKnownFacts if any of the known facts are absent in the vat customer details" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMin)))
      val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
      await(res) shouldBe Left(InvalidVatKnownFacts)
    }

    "return an InvalidVatNumber if the connection to get vat customer details returns an InvalidVatNumber" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(connectors.InvalidVatNumber)))
      val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
      await(res) shouldBe Left(InvalidVatNumber)
    }

    "return an VatNumberNotFound if the connection to get vat customer details returns a VatNumberNotFound" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(connectors.VatNumberNotFound)))
      val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
      await(res) shouldBe Left(VatNumberNotFound)
    }

    "return a Forbidden" when {
      "the connection to get vat customer details returns a Forbidden" in {
        mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(connectors.Forbidden)))
        val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
        await(res) shouldBe Left(Forbidden)
      }

      "the connection to get vat customer details returns a Migration" in {
        mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(connectors.Migration)))
        val res = TestVatKnownFactsRetrievalService.retrieveVatKnownFacts(testVatNumber)
        await(res) shouldBe Left(Migration)
      }
    }

  }

}
