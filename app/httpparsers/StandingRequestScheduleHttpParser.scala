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
import models.{StandingRequestSchedule}
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}

@Singleton
class StandingRequestScheduleHttpParser @Inject()(appConfig: AppConfig) extends LoggingUtil {

  type StandingRequestScheduleHttpParserResponse = Either[GetStandingRequestScheduleFailure, StandingRequestSchedule]

  implicit object GetStandingRequestScheduleHttpReads extends HttpReads[StandingRequestScheduleHttpParserResponse] {
    override def read(method: String, url: String, response: HttpResponse): StandingRequestScheduleHttpParserResponse =
      response.status match {
        case OK =>
          logger.debug("[StandingRequestScheduleHttpParserResponse][read]: Status OK")
          response.json.validate(
            StandingRequestSchedule.reads
          ) match {
            case JsSuccess(standingRequestSchedule, _) =>
              Right(standingRequestSchedule)
            case JsError(errors) =>
              logger.warn(s"Invalid Success Response Json. Error: $errors")
              Left(UnexpectedStandingRequestScheduleFailure(INTERNAL_SERVER_ERROR, "Invalid Success Response Json"))
          }
        case _ => handleErrorResponse(response)
      }
  }

  def handleErrorResponse(response: HttpResponse): Left[GetStandingRequestScheduleFailure, StandingRequestSchedule] = {

    response.status match {
      case BAD_REQUEST =>
        logUnexpectedResponse(response)
        Left(SrsInvalidVatNumber)
      case NOT_FOUND =>
        logger.debug("[StandingRequestScheduleHttpParser][handleErrorResponse] - " +
          s"NOT FOUND returned - Schedule not found. Status: $NOT_FOUND. Body: ${response.body}")
        Left(SrsVatNumberNotFound)
      case FORBIDDEN =>
        logUnexpectedResponse(response)
        Left(SrsForbidden)
      case status =>
        logUnexpectedResponse(response)
        Left(UnexpectedStandingRequestScheduleFailure(status, response.body))
    }
  }

  def logUnexpectedResponse(response: HttpResponse, message: String = "Unexpected response"): Unit = {
    logger.warn(
      s"[StandingRequestScheduleHttpParser][logUnexpectedResponse]: $message. " +
        s"Status: ${response.status}. " +
        s"Body: ${response.body} " +
        s"CorrelationId: ${response.header("CorrelationId").getOrElse("Not found in header")}"
    )
  }
}
