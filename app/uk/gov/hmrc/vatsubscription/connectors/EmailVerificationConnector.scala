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

import javax.inject.Inject

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.config.Constants.EmailVerification._
import uk.gov.hmrc.vatsubscription.httpparsers.CreateEmailVerificationRequestHttpParser._
import uk.gov.hmrc.vatsubscription.httpparsers.GetEmailVerificationStateHttpParser._

import scala.concurrent.Future

class EmailVerificationConnector @Inject()(http: HttpClient,
                                           appConfig: AppConfig) {
  def getEmailVerificationState(emailAddress: String)(implicit hc: HeaderCarrier): Future[GetEmailVerificationStateResponse] = {
    http.GET[GetEmailVerificationStateResponse](appConfig.getEmailVerifiedUrl(emailAddress))
  }

  def createEmailVerificationRequest(emailAddress: String)(implicit hc: HeaderCarrier): Future[CreateEmailVerificationRequestResponse] = {
    val jsonBody =
      Json.obj(
        EmailKey -> emailAddress,
        TemplateIdKey -> "verifyEmailAddress",
        TemplateParametersKey -> Json.arr(),
        LinkExpiryDurationKey -> "P1D",
        ContinueUrlKey -> appConfig.verifyEmailContinueUrl
      )

    http.POST[JsObject, CreateEmailVerificationRequestResponse](appConfig.verifyEmailUrl, jsonBody)
  }
}
