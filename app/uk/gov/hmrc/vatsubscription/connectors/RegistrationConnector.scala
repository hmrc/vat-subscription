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
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.config.Constants.Des._
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.vatsubscription.httpparsers.RegisterWithMultipleIdentifiersHttpParser._

import scala.concurrent.Future

class RegistrationConnector @Inject()(val http: HttpClient,
                                      val applicationConfig: AppConfig) {
  def registerCompany(vatNumber: String,
                      companyNumber: String
                     )(implicit hc: HeaderCarrier): Future[RegisterWithMultipleIdentifiersResponse] =
    registerWithMultipleIdentifiers(
      vatNumber = vatNumber,
      companyNumber = Some(companyNumber),
      nino = None
    )

  def registerIndividual(vatNumber: String,
                         nino: String
                        )(implicit hc: HeaderCarrier): Future[RegisterWithMultipleIdentifiersResponse] =
    registerWithMultipleIdentifiers(
      vatNumber = vatNumber,
      companyNumber = None,
      nino = Some(nino)
    )

  private def registerWithMultipleIdentifiers(vatNumber: String,
                                              companyNumber: Option[String],
                                              nino: Option[String]
                                             )(implicit hc: HeaderCarrier) = {
    http.POST[JsObject, RegisterWithMultipleIdentifiersResponse](
      url = applicationConfig.registerWithMultipleIdentifiersUrl,
      body = buildRequest(vatNumber, companyNumber, nino),
      headers = Seq(
        applicationConfig.desAuthorisationToken,
        applicationConfig.desEnvironment
      )
    )
  }

  private[connectors] def buildRequest(vatNumber: String, companyNumber: Option[String], nino: Option[String]) = {
    val companyNumberObject = companyNumber map {
      companyNumber =>
        Json.obj(
          IdTypeKey -> CrnKey,
          IdValueKey -> companyNumber
        )
    }

    val ninoObject = nino map {
      nino =>
        Json.obj(
          IdTypeKey -> NinoKey,
          IdValueKey -> nino
        )
    }

    val vatNumberObject = Json.obj(
      IdTypeKey -> VrnKey,
      IdValueKey -> vatNumber
    )

    val identifiers = List(vatNumberObject) ++ companyNumberObject ++ ninoObject

    Json.obj(
      RegistrationRequestKey -> Json.obj(
        IdentificationKey -> identifiers
      )
    )
  }
}
