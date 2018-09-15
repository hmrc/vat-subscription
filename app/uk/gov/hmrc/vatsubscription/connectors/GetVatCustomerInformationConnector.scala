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
import play.api.Logger
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.httpparsers._
import uk.gov.hmrc.vatsubscription.models.VatCustomerInformation
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetVatCustomerInformationConnector @Inject()(val http: HttpClient,
                                                   val applicationConfig: AppConfig) {

  private def url(vatNumber: String) = s"${applicationConfig.desUrl}/vat/customer/vrn/$vatNumber/information"

  def getInformation(vatNumber: String)(implicit hc: HeaderCarrier): Future[GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpParserResponse] = {
    val headerCarrier = hc
      .withExtraHeaders(applicationConfig.desEnvironmentHeader)
      .copy(authorization = Some(Authorization(applicationConfig.desAuthorisationToken)))

    Logger.debug(s"[GetVatCustomerInformationConnector][getInformation] URL: ${url(vatNumber)}")
    Logger.debug(s"[GetVatCustomerInformationConnector][getInformation] Headers: ${headerCarrier.headers}")

    http.GET[GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpParserResponse](
      url = url(vatNumber)
    )(
      GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads,
      headerCarrier,
      implicitly[ExecutionContext]
    )
  }

  object GetVatCustomerInformationHttpParser {
    type GetVatCustomerInformationHttpParserResponse = Either[GetVatCustomerInformationFailure, VatCustomerInformation]

    implicit object GetVatCustomerInformationHttpReads extends HttpReads[GetVatCustomerInformationHttpParserResponse] {
      override def read(method: String, url: String, response: HttpResponse): GetVatCustomerInformationHttpParserResponse =
        response.status match {
          case OK =>
            Logger.debug("[CustomerCircumstancesHttpParser][read]: Status OK")
            response.json.validate(VatCustomerInformation.desReader(applicationConfig)) match {
              case JsSuccess(vatCustomerInformation, _) => Right(vatCustomerInformation)
              case _ =>
                Logger.warn(s"[CustomerCircumstancesHttpParser][read]: Invalid Success Response Json")
                Left(UnexpectedGetVatCustomerInformationFailure(OK, response.body))
            }
          case BAD_REQUEST =>
            Logger.warn("[CustomerCircumstancesHttpParser][read]: Unexpected response, status BAD REQUEST returned")
            Left(InvalidVatNumber)
          case NOT_FOUND =>
            Logger.warn("[CustomerCircumstancesHttpParser][read]: Unexpected response, status NOT FOUND returned")
            Left(VatNumberNotFound)
          case status =>
            Logger.warn(s"[CustomerCircumstancesHttpParser][read]: Unexpected response, status $status returned")
            Left(UnexpectedGetVatCustomerInformationFailure(status, response.body))
        }
    }

  }
}

