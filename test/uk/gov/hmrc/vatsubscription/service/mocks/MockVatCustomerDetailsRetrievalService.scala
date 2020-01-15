/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.service.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.Suite
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.GetVatCustomerInformationFailure
import uk.gov.hmrc.vatsubscription.models.{CustomerDetails, VatCustomerInformation}
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.Future

trait MockVatCustomerDetailsRetrievalService extends MockitoSugar {
  self: Suite =>

  val mockVatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService = mock[VatCustomerDetailsRetrievalService]

  def mockRetrieveVatCustomerDetails(vatNumber: String)
                        (response: Future[Either[GetVatCustomerInformationFailure, CustomerDetails]]): Unit = {
    when(mockVatCustomerDetailsRetrievalService.retrieveVatCustomerDetails(ArgumentMatchers.eq(vatNumber))
                                                                          (ArgumentMatchers.any[HeaderCarrier])) thenReturn response
  }

  def mockExtractWelshIndicator(vrn: String)(response: Future[Either[GetVatCustomerInformationFailure, Boolean]]): Unit = {
    when(mockVatCustomerDetailsRetrievalService.extractWelshIndicator(ArgumentMatchers.eq(vrn))
                                                                     (ArgumentMatchers.any[HeaderCarrier])) thenReturn response
  }

  def mockRetrieveVatInformation(vatNumber: String)
                                    (response: Future[Either[GetVatCustomerInformationFailure, VatCustomerInformation]]): Unit = {
    when(mockVatCustomerDetailsRetrievalService.retrieveCircumstanceInformation(ArgumentMatchers.eq(vatNumber))
    (ArgumentMatchers.any[HeaderCarrier])) thenReturn response
  }
}
