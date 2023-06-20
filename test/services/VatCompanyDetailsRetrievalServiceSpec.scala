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
import connectors.{InvalidVatNumber, VatNumberNotFound}
import connectors.mocks.MockGetVatCustomerInformationConnector
import helpers.BaseTestConstants._
import helpers.CustomerDetailsTestConstants._
import helpers.CustomerInformationTestConstants._
import models.User
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.Future

class VatCompanyDetailsRetrievalServiceSpec extends AnyWordSpecLike with Matchers with MockGetVatCustomerInformationConnector {

  object TestVatCompanyDetailsRetrievalService extends VatCustomerDetailsRetrievalService(
    mockConnector
  )

  implicit  val testUser: User[_] = User(testVatNumber, None, testCredentials.providerId)(fakeRequest)
  implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(FakeRequest())

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

  "extractWelshIndicator" should {

    "return true when customer details has welshIndicator set to true" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMaxWithFRS)))
      val res = TestVatCompanyDetailsRetrievalService.extractWelshIndicator(testVatNumber)
      await(res) shouldBe Right(true)
    }

    "return false when customer details has welshIndicator set to false" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelMax)))
      val res = TestVatCompanyDetailsRetrievalService.extractWelshIndicator(testVatNumber)
      await(res) shouldBe Right(false)
    }

    "return false when customer details doesn't have a welsh indicator" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Right(customerInformationModelNoWelshIndicator)))
      val res = TestVatCompanyDetailsRetrievalService.extractWelshIndicator(testVatNumber)
      await(res) shouldBe Right(false)
    }

    "return a Left when there is an error" in {
      mockGetVatCustomerInformationConnector(testVatNumber)(Future.successful(Left(VatNumberNotFound)))
      val res = TestVatCompanyDetailsRetrievalService.extractWelshIndicator(testVatNumber)
      await(res) shouldBe Left(VatNumberNotFound)
    }
  }
}