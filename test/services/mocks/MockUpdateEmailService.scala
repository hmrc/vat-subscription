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

import config.AppConfig
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.Suite
import uk.gov.hmrc.http.HeaderCarrier
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.post.EmailPost
import models.User
import org.scalatestplus.mockito.MockitoSugar
import services._

import scala.concurrent.{ExecutionContext, Future}

trait MockUpdateEmailService extends MockitoSugar {
  self: Suite =>

  val mockUpdateEmailService: UpdateEmailService = mock[UpdateEmailService]

  def mockUpdateEmail(newPPOBEmail: EmailPost)(response: Future[UpdateVatSubscriptionResponse]): Unit = {
    when(mockUpdateEmailService
      .updateEmail(ArgumentMatchers.eq(newPPOBEmail),ArgumentMatchers.any[Boolean], ArgumentMatchers.any[AppConfig])(
        ArgumentMatchers.any[User[_]],
        ArgumentMatchers.any[HeaderCarrier],
        ArgumentMatchers.any[ExecutionContext]
      )
    ).thenReturn(response)
  }
}
