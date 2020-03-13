/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, NOT_FOUND, OK, PRECONDITION_FAILED, INTERNAL_SERVER_ERROR}
import play.api.libs.json.{JsSuccess, Json, Writes}
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.config.featureSwitch.{Api1363R10, Api1363R8}
import uk.gov.hmrc.vatsubscription.models.VatCustomerInformation

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetVatCustomerInformationConnector @Inject()(val http: HttpClient,
                                                   val applicationConfig: AppConfig) {

  private def url(vatNumber: String) = s"${applicationConfig.desUrl}/vat/customer/vrn/$vatNumber/information"

  def getInformation(vatNumber: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext)
  : Future[GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpParserResponse] = {
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
            response.json.validate(

             applicationConfig.features.api1363Version() match {
               case Api1363R8 => VatCustomerInformation.release8Reads(applicationConfig)
               case Api1363R10 => VatCustomerInformation.release10Reads(applicationConfig)
             }

            ) match {
              case JsSuccess(vatCustomerInformation, _) =>
                Logger.debug(s"[CustomerCircumstancesHttpParser][read]: Json Body: \n\n${response.body}")
                Right(vatCustomerInformation)
              case _ =>
                logUnexpectedResponse(response, "Invalid Success Response Json")
                Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, "Invalid Success Response Json"))
            }
          case _ => handleErrorResponse(response)
        }
    }
  }

  private def handleErrorResponse(response: HttpResponse): Left[GetVatCustomerInformationFailure, VatCustomerInformation] = {
    response.status match {
      case BAD_REQUEST =>
        logUnexpectedResponse(response, logBody = true)
        Left(InvalidVatNumber)
      case NOT_FOUND =>
        Logger.debug("[CustomerCircumstancesHttpParser][read] - " +
          s"NOT FOUND returned - Subscription not found. Status: $NOT_FOUND. Body: ${response.body}")
        Left(VatNumberNotFound)
      case FORBIDDEN if response.body.contains("MIGRATION") =>
        logUnexpectedResponse(response, "FORBIDDEN returned with MIGRATION - Migration in progress")
        Left(Migration)
      case FORBIDDEN =>
        logUnexpectedResponse(response, logBody = true)
        Left(Forbidden)
      case status =>
        logUnexpectedResponse(response, logBody = true)
        Left(UnexpectedGetVatCustomerInformationFailure(status, response.body))
    }
  }

  private def logUnexpectedResponse(response: HttpResponse, message: String = "Unexpected response", logBody: Boolean = false): Unit = {
    Logger.warn(
      s"[CustomerCircumstancesHttpParser][read]: $message. " +
      s"Status: ${response.status}. " +
      s"${ if(logBody) s"Body: ${response.body} " else "" } " +
      s"CorrelationId: ${response.header("CorrelationId").getOrElse("Not found in header")}"
    )
  }
}

sealed trait GetVatCustomerInformationFailure {
  val status: Int
  val body: String
}

object GetVatCustomerInformationFailure {
  implicit val writes: Writes[GetVatCustomerInformationFailure] = Writes {
    error => Json.obj("status" -> error.status.toString, "body" -> error.body)
  }
}

case object InvalidVatNumber extends GetVatCustomerInformationFailure {
  override val status: Int = BAD_REQUEST
  override val body = "Bad request"
}

case object VatNumberNotFound extends GetVatCustomerInformationFailure {
  override val status: Int = NOT_FOUND
  override val body = "Not found"
}

case object Forbidden extends GetVatCustomerInformationFailure {
  override val status: Int = FORBIDDEN
  override val body: String = "Forbidden"
}

case object Migration extends GetVatCustomerInformationFailure {
  override val status: Int = PRECONDITION_FAILED
  override val body: String = "Migration"
}

case class UnexpectedGetVatCustomerInformationFailure(override val status: Int, override val body: String)
  extends GetVatCustomerInformationFailure
