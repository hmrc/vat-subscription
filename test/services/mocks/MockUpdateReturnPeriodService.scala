/*
 * Copyright 2024 HM Revenue & Customs
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
import uk.gov.hmrc.http.HeaderCarrier
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.{ReturnPeriod, User}
import org.scalatestplus.mockito.MockitoSugar
import services._
import scala.concurrent.{ExecutionContext, Future}

trait MockUpdateReturnPeriodService extends MockitoSugar {
  self: Suite =>

  val mockUpdateReturnPeriodService: UpdateReturnPeriodService = mock[UpdateReturnPeriodService]

  def mockUpdateReturnPeriod(newPeriod: ReturnPeriod)(response: Future[UpdateVatSubscriptionResponse]): Unit = {
    when(mockUpdateReturnPeriodService
      .updateReturnPeriod(ArgumentMatchers.eq(newPeriod),ArgumentMatchers.any[Boolean])(
        ArgumentMatchers.any[User[_]],
        ArgumentMatchers.any[HeaderCarrier],
        ArgumentMatchers.any[ExecutionContext]
      )
    ).thenReturn(response)
  }
}
