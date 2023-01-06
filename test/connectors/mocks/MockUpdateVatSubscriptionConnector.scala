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

package connectors.mocks

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import connectors.UpdateVatSubscriptionConnector
import models.User
import models.updateVatSubscription.request.UpdateVatSubscription
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import scala.concurrent.{ExecutionContext, Future}

trait MockUpdateVatSubscriptionConnector extends AnyWordSpecLike with Matchers with MockitoSugar with BeforeAndAfterEach {

  val mockUpdateVatSubscriptionConnector: UpdateVatSubscriptionConnector = mock[UpdateVatSubscriptionConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUpdateVatSubscriptionConnector)
  }

  def mockUpdateVatSubscriptionResponse(response: Either[ErrorModel, SuccessModel]): Unit = {
    when(mockUpdateVatSubscriptionConnector.updateVatSubscription
      (any[User[_]](), any[UpdateVatSubscription](), any[HeaderCarrier]())
      (any[ExecutionContext]()))
        .thenReturn(Future.successful(response))
  }
}
