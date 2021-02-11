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

package services.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.Suite
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.{User, VatCustomerInformation}
import models.post.CommsPreferencePost
import services.UpdateContactPreferenceService

import scala.concurrent.{ExecutionContext, Future}

trait MockUpdateContactPreferenceService extends MockitoSugar {
  self: Suite =>

  val mockUpdateContactPreferenceService: UpdateContactPreferenceService = mock[UpdateContactPreferenceService]

  def mockUpdateContactPreference(newContactPref: CommsPreferencePost)(response: Future[UpdateVatSubscriptionResponse]): Unit = {
    when(mockUpdateContactPreferenceService
    .updateContactPreference(ArgumentMatchers.eq(newContactPref), ArgumentMatchers.any[Boolean])(
      ArgumentMatchers.any[User[_]],
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )
    ).thenReturn(response)
  }

  def mockUpdatePreferenceAndEmail()(response: Future[UpdateVatSubscriptionResponse]): Unit = {
    when(mockUpdateContactPreferenceService
      .updatePreferenceAndEmail(ArgumentMatchers.any[String], ArgumentMatchers.any[VatCustomerInformation])(
        ArgumentMatchers.any[User[_]],
        ArgumentMatchers.any[HeaderCarrier],
        ArgumentMatchers.any[ExecutionContext]
      )
    ).thenReturn(response)
  }
}
