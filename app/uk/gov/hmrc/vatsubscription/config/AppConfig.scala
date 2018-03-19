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

package uk.gov.hmrc.vatsubscription.config

import javax.inject.{Inject, Singleton}

import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.config.ServicesConfig

@Singleton
class AppConfig @Inject()(val runModeConfiguration: Configuration, environment: Environment) extends ServicesConfig {

  override protected def mode: Mode = environment.mode

  private def loadConfig(key: String) = runModeConfiguration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  lazy val agentClientRelationshipUrl: String = baseUrl("agent-client-relationships") + "/agent-client-relationships"

  lazy val taxEnrolmentsUrl: String = baseUrl("tax-enrolments") + "/tax-enrolments"

  lazy val desUrl: String = loadConfig("microservice.services.des.url")
  lazy val desAuthorisationToken: String = s"Bearer ${loadConfig("microservice.services.des.authorisation-token")}"
  lazy val desEnvironmentHeader: (String, String) =
    "Environment" -> loadConfig("microservice.services.des.environment")

  lazy val registerWithMultipleIdentifiersUrl: String = s"$desUrl/cross-regime/register/VATC"

  lazy val authenticatorUrl: String = baseUrl("authenticator")

  lazy val emailVerificationUrl: String = baseUrl("email-verification")

  def getEmailVerifiedUrl(email: String): String = s"$emailVerificationUrl/email-verification/verified-email-addresses/$email"

  lazy val verifyEmailUrl = s"$emailVerificationUrl/email-verification/verification-requests"

  lazy val frontendBaseUrl: String = loadConfig("microservice.services.vat-subscription-frontend.url")

  lazy val principalVerifyEmailContinueUrl = s"$frontendBaseUrl/vat-through-software/sign-up/email-verified"

  lazy val delegatedVerifyEmailContinueUrl = s"$frontendBaseUrl/vat-through-software/sign-up/client/email-verified"

  lazy val identityVerificationFrontendUrl: String = baseUrl("identity-verification-frontend")

  lazy val timeToLiveSeconds: Long = loadConfig("mongodb.timeToLiveSeconds").toLong

}
