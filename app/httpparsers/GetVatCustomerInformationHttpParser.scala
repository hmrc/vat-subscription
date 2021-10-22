/*
 * Copyright 2021 HM Revenue & Customs
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

package httpparsers

import config.AppConfig
import connectors._
import javax.inject.{Inject, Singleton}
import models.VatCustomerInformation
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

@Singleton
class GetVatCustomerInformationHttpParser @Inject()(appConfig: AppConfig) extends LoggerUtil {

  type GetVatCustomerInformationHttpParserResponse = Either[GetVatCustomerInformationFailure, VatCustomerInformation]

  implicit object GetVatCustomerInformationHttpReads extends HttpReads[GetVatCustomerInformationHttpParserResponse] {
    override def read(method: String, url: String, response: HttpResponse): GetVatCustomerInformationHttpParserResponse =
      response.status match {
        case OK =>
          logger.debug("[CustomerCircumstancesHttpParser][read]: Status OK")
          response.json.validate(
            VatCustomerInformation.reads(appConfig)

          ) match {
            case JsSuccess(vatCustomerInformation, _) =>
              logger.debug(s"[CustomerCircumstancesHttpParser][read]: Json Body: \n\n${response.body}")
              Right(vatCustomerInformation)
            case JsError(errors) =>
              logger.warn(s"Invalid Success Response Json. Error: $errors")
              Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, "Invalid Success Response Json"))
          }
        case _ => handleErrorResponse(response)
      }
  }

  def handleErrorResponse(response: HttpResponse): Left[GetVatCustomerInformationFailure, VatCustomerInformation] = {

    response.status match {
      case BAD_REQUEST =>
        logUnexpectedResponse(response, logBody = true)
        Left(InvalidVatNumber)
      case NOT_FOUND =>
        logger.debug("[CustomerCircumstancesHttpParser][handleErrorResponse] - " +
          s"NOT FOUND returned - Subscription not found. Status: $NOT_FOUND. Body: ${response.body}")
        Left(VatNumberNotFound)
      case FORBIDDEN if response.body.contains("MIGRATION") =>
        logger.debug("[CustomerCircumstancesHttpParser][handleErrorResponse] - " +
          "FORBIDDEN returned with MIGRATION - Migration in progress.")
        Left(Migration)
      case FORBIDDEN =>
        logUnexpectedResponse(response, logBody = true)
        Left(Forbidden)
      case status =>
        logUnexpectedResponse(response, logBody = true)
        Left(UnexpectedGetVatCustomerInformationFailure(status, response.body))
    }
  }

  def logUnexpectedResponse(response: HttpResponse, message: String = "Unexpected response", logBody: Boolean = false): Unit = {
    logger.warn(
      s"[CustomerCircumstancesHttpParser][logUnexpectedResponse]: $message. " +
        s"Status: ${response.status}. " +
        s"${ if(logBody) s"Body: ${response.body} " else "" } " +
        s"CorrelationId: ${response.header("CorrelationId").getOrElse("Not found in header")}"
    )
  }
}
