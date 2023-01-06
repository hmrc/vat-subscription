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

package services.mocks

import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.{BusinessName, CustomerDetails, TradingName, User}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.Suite
import org.scalatestplus.mockito.MockitoSugar
import services.UpdateOrganisationDetailsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockUpdateOrganisationDetailsService extends MockitoSugar {

  self: Suite =>

  val mockUpdateOrganisationDetailsService: UpdateOrganisationDetailsService = mock[UpdateOrganisationDetailsService]

  def mockUpdateTradingName(newTradingName: TradingName, customerDetails: CustomerDetails)(response: Future[UpdateVatSubscriptionResponse]): Unit = {
    when(mockUpdateOrganisationDetailsService
      .updateTradingName(ArgumentMatchers.eq(newTradingName),ArgumentMatchers.eq(customerDetails))(
        ArgumentMatchers.any[User[_]],
        ArgumentMatchers.any[HeaderCarrier],
        ArgumentMatchers.any[ExecutionContext]
      )
    ).thenReturn(response)
  }

  def mockUpdateBusinessName(newBusinessName: BusinessName)(response: Future[UpdateVatSubscriptionResponse]): Unit = {
    when(mockUpdateOrganisationDetailsService
      .updateBusinessName(ArgumentMatchers.eq(newBusinessName),ArgumentMatchers.any[CustomerDetails])(
        ArgumentMatchers.any[User[_]],
        ArgumentMatchers.any[HeaderCarrier],
        ArgumentMatchers.any[ExecutionContext]
      )
    ).thenReturn(response)
  }

}
