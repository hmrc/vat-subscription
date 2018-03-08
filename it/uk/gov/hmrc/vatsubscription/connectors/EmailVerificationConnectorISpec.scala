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

package uk.gov.hmrc.vatsubscription.connectors

import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.helpers.ComponentSpecBase
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.EmailVerificationStub._
import uk.gov.hmrc.vatsubscription.httpparsers.CreateEmailVerificationRequestHttpParser.EmailVerificationRequestSent
import uk.gov.hmrc.vatsubscription.httpparsers.EmailVerified

class EmailVerificationConnectorISpec extends ComponentSpecBase {

  lazy val connector: EmailVerificationConnector = app.injector.instanceOf[EmailVerificationConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "getEmailVerificationState" should {
    "return the email verification state" in {
      stubGetEmailVerified(testEmail)

      val res = connector.getEmailVerificationState(testEmail)

      await(res) shouldBe Right(EmailVerified)
    }
  }

  "createEmailVerificationRequest" should {
    "return EmailVerificationRequestSent" in {
      val continueUrl = app.injector.instanceOf[AppConfig].delegatedVerifyEmailContinueUrl

      stubVerifyEmail(testEmail, continueUrl)(CREATED)

      val res = connector.createEmailVerificationRequest(testEmail, continueUrl)

      await(res) shouldBe Right(EmailVerificationRequestSent)
    }
  }

}
