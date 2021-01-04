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

package service.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Suite}
import uk.gov.hmrc.http.HeaderCarrier
import connectors.GetVatCustomerInformationFailure
import models.MandationStatus
import services.MandationStatusService

import scala.concurrent.Future


trait MockMandationStatusService extends MockitoSugar with BeforeAndAfterEach {
  self: Suite =>

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockMandationStatusService)
  }

  val mockMandationStatusService: MandationStatusService = mock[MandationStatusService]

  def mockGetMandationStatus(vatNumber: String
                            )(response: Future[Either[GetVatCustomerInformationFailure, MandationStatus]]): Unit = {
    when(mockMandationStatusService.getMandationStatus(
      ArgumentMatchers.eq(vatNumber)
    )(ArgumentMatchers.any[HeaderCarrier])) thenReturn response
  }

}
