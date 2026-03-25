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

package httpparsers

import config.AppConfig
import connectors._

import javax.inject.{Inject, Singleton}
import models.VatCustomerInformation
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggingUtil

@Singleton
class GetVatCustomerInformationHttpParser @Inject()(appConfig: AppConfig) extends LoggingUtil {

  type GetVatCustomerInformationHttpParserResponse = Either[GetVatCustomerInformationFailure, VatCustomerInformation]

  private val MaxBodyLength = 300

  implicit object GetVatCustomerInformationHttpReads extends HttpReads[GetVatCustomerInformationHttpParserResponse] {
    override def read(method: String, url: String, response: HttpResponse): GetVatCustomerInformationHttpParserResponse =
      response.status match {
        case OK =>
          logger.debug("[CustomerCircumstancesHttpParser][read]: Status OK")
          response.json.validate(
            VatCustomerInformation.reads(appConfig)
          ) match {
            case JsSuccess(vatCustomerInformation, _) =>
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
        logUnexpectedResponse(response)
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
        logUnexpectedResponse(response)
        Left(Forbidden)
      case status =>
        val body = Option(response.body).getOrElse("").trim

        classifyBody(body) match {
          case EmptyBody =>
            logger.warn(s"[CustomerCircumstancesHttpParser][handleErrorResponse] Empty response body received. " +
              s"Status: $status. CorrelationId: ${response.header("CorrelationId").getOrElse("Not found in header")}")
            Left(UnexpectedGetVatCustomerInformationFailure(status, "Downstream returned empty body"))

          case XmlBody =>
            val xmlMessage = extractXmlMessage(body)
            logger.info(s"[CustomerCircumstancesHttpParser][handleErrorResponse] XML response received. " +
              s"Status: $status. Message: $xmlMessage. " +
              s"CorrelationId: ${response.header("CorrelationId").getOrElse("Not found in header")}")
            Left(UnexpectedGetVatCustomerInformationFailure(status, xmlMessage))

          case HtmlBody =>
            logger.info(s"[CustomerCircumstancesHttpParser][handleErrorResponse] HTML response received from downstream. " +
              s"Status: $status. " +
              s"CorrelationId: ${response.header("CorrelationId").getOrElse("Not found in header")}")
            Left(UnexpectedGetVatCustomerInformationFailure(status, "Received HTML response from downstream"))

          case JsonBody =>
            val truncated = body.take(MaxBodyLength)
            logger.warn(s"[CustomerCircumstancesHttpParser][handleErrorResponse] Unexpected JSON error body structure: $truncated")
            Left(UnexpectedGetVatCustomerInformationFailure(status, truncated))

          case UnknownBody =>
            val truncated = body.take(MaxBodyLength)
            logger.warn(s"[CustomerCircumstancesHttpParser][handleErrorResponse] Unknown response format: $truncated")
            Left(UnexpectedGetVatCustomerInformationFailure(status, truncated))
        }
    }
  }

  def logUnexpectedResponse(response: HttpResponse, message: String = "Unexpected response"): Unit = {
    logger.warn(
      s"[CustomerCircumstancesHttpParser][logUnexpectedResponse]: $message. " +
        s"Status: ${response.status}. " +
        s"Body: ${response.body} " +
        s"CorrelationId: ${response.header("CorrelationId").getOrElse("Not found in header")}"
    )
  }

  private def extractXmlMessage(xml: String): String = {
    def extract(tag: String): Option[String] =
      s"<$tag>(.*?)</$tag>".r.findFirstMatchIn(xml).map(_.group(1))

    List(
      extract("am:message"),
      extract("am:description")
    ).flatten.mkString(" - ") match {
      case "" => "XML fault received"
      case message => message
    }
  }

  private def classifyBody(body: String): BodyType = body match {
    case "" => EmptyBody
    case b if b.startsWith("{") || b.startsWith("[") => JsonBody
    case b if b.contains("<am:fault") => XmlBody
    case b if b.toLowerCase.contains("<html") => HtmlBody
    case _ => UnknownBody
  }

  private sealed trait BodyType

  private case object EmptyBody extends BodyType

  private case object JsonBody extends BodyType

  private case object XmlBody extends BodyType

  private case object HtmlBody extends BodyType

  private case object UnknownBody extends BodyType

}