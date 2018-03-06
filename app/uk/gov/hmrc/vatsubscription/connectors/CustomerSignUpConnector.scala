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

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{JsObject, Json, Writes}
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.httpparsers.CustomerSignUpHttpParser._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class CustomerSignUpConnector @Inject()(val http: HttpClient,
                                        val applicationConfig: AppConfig) {


  private def url = applicationConfig.desUrl + "/customer/signup/VATC"

  import CustomerSignUpConnector._


  def signUp(safeId: String, vatNumber: String, email: String, emailVerified: Boolean
            )(implicit hc: HeaderCarrier): Future[CustomerSignUpResponse] = {
    val headerCarrier = hc
      .withExtraHeaders(applicationConfig.desEnvironmentHeader)
      .copy(authorization = Some(Authorization(applicationConfig.desAuthorisationToken)))

    http.POST[JsObject, CustomerSignUpResponse](
      url = url,
      body = buildRequest(safeId, vatNumber, email, emailVerified)
    )(
      implicitly[Writes[JsObject]],
      implicitly[HttpReads[CustomerSignUpResponse]],
      headerCarrier,
      implicitly[ExecutionContext]
    )
  }

}

object CustomerSignUpConnector {

  import uk.gov.hmrc.vatsubscription.config.Constants.Des._

  private[connectors] def buildRequest(safeId: String, vatNumber: String, email: String, emailVerified: Boolean): JsObject = {
    Json.obj(
      "signUpRequest" -> Json.obj(
        "identification" ->
          Json.arr(
            Json.obj(IdTypeKey -> SafeIdKey, idNumberKey -> safeId),
            Json.obj(IdTypeKey -> VrnKey, idNumberKey -> vatNumber)
          ),
        "additionalInformation" ->
          Json.arr(
            Json.obj(
              "typeOfField" -> emailKey,
              "fieldContents" -> email,
              "infoVerified" -> emailVerified
            )
          )
      )
    )
  }

}
