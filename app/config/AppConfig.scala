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

package config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import java.util.Base64

@Singleton
class AppConfig @Inject()(implicit val configuration: Configuration, servicesConfig: ServicesConfig){

  import servicesConfig._

  def desUrl: String =
    getString(
      "microservice.services.des.url"
    )

  def hipUrl: String =
    getString(
      "microservice.services.hip.url"
    )
  private val clientIdV1: String = getString("microservice.services.hip.clientId")
  private val secretV1: String   = getString("microservice.services.hip.secret")
  def hipAuthorisationToken: String = Base64.getEncoder.encodeToString(s"$clientIdV1:$secretV1".getBytes("UTF-8"))

  val hipServiceOriginatorIdKeyV1: String = getString("microservice.services.hip.originatoridkey")
  val hipServiceOriginatorIdV1: String    = getString("microservice.services.hip.originatoridvalue")

  lazy val hipEnvironmentHeader: (String, String) =
    "Environment" -> getString("microservice.services.hip.environment")

  lazy val desAuthorisationToken: String = s"Bearer ${getString("microservice.services.des.authorisation-token")}"
  lazy val desEnvironmentHeader: (String, String) =
    "Environment" -> getString("microservice.services.des.environment")

  lazy val features = new Features

}
