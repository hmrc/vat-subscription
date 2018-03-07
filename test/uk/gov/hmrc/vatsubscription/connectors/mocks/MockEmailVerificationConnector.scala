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

package uk.gov.hmrc.vatsubscription.connectors.mocks

import org.mockito.ArgumentMatchers
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.vatsubscription.connectors.EmailVerificationConnector
import org.mockito.Mockito._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.httpparsers.CreateEmailVerificationRequestHttpParser.CreateEmailVerificationRequestResponse
import uk.gov.hmrc.vatsubscription.httpparsers.GetEmailVerificationStateHttpParser.GetEmailVerificationStateResponse

import scala.concurrent.Future

trait MockEmailVerificationConnector extends MockitoSugar with BeforeAndAfterEach {
  this: Suite =>

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockEmailVerificationConnector)
  }

  val mockEmailVerificationConnector: EmailVerificationConnector = mock[EmailVerificationConnector]

  def mockGetEmailVerificationState(emailAddress: String)(response: Future[GetEmailVerificationStateResponse]): Unit =
    when(mockEmailVerificationConnector.getEmailVerificationState(
      ArgumentMatchers.eq(emailAddress)
    )(ArgumentMatchers.any[HeaderCarrier])) thenReturn response

  def mockCreateEmailVerificationRequest(emailAddress: String)(response: Future[CreateEmailVerificationRequestResponse]): Unit =
    when(mockEmailVerificationConnector.createEmailVerificationRequest(
      ArgumentMatchers.eq(emailAddress)
    )(ArgumentMatchers.any[HeaderCarrier])) thenReturn response
}
