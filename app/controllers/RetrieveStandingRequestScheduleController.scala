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

package controllers

import connectors.{InvalidVatNumber, Migration, SrsForbidden, SrsInvalidVatNumber, SrsMigration,
  SrsVatNumberNotFound, UnexpectedStandingRequestScheduleFailure, VatNumberNotFound, Forbidden => ForbiddenResult}
import controllers.actions.VatAuthorised
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RetrieveStandingRequestScheduleController @Inject()(VatAuthorised: VatAuthorised,
                                                          standingRequestScheduleRetrievalService: StandingRequestScheduleRetrievalService,
                                                          cc: ControllerComponents)
                                                         (implicit ec: ExecutionContext) extends BackendController(cc) with LoggingUtil {

  def retrieveStandingRequestScheduleDetails(vatNumber: String): Action[AnyContent] = VatAuthorised.async(vatNumber) {
    implicit user =>
      standingRequestScheduleRetrievalService.retrieveStandingRequestSchedule(vatNumber) map {
        case Right(standingRequests) => Ok(Json.toJson(standingRequests))
        case Left(SrsInvalidVatNumber) =>
          infoLog(s"[RetrieveStandingRequestScheduleController][retrieveStandingRequestScheduleDetails]: " +
            s"SrsInvalidVatNumber returned from StandingRequestScheduleRetrievalService")
          BadRequest(Json.toJson(InvalidVatNumber))
        case Left(SrsVatNumberNotFound) =>
          infoLog(s"[RetrieveStandingRequestScheduleController][retrieveStandingRequestScheduleDetails]: " +
            s"VatNumberNotFound returned from StandingRequestScheduleRetrievalService")
          NotFound(Json.toJson(VatNumberNotFound))
        case Left(SrsForbidden) =>
          infoLog(s"[RetrieveStandingRequestScheduleController][retrieveStandingRequestScheduleDetails]:" +
            s"Forbidden returned from StandingRequestScheduleRetrievalService")
          Forbidden(Json.toJson(ForbiddenResult))
        case Left(UnexpectedStandingRequestScheduleFailure(status, body)) =>
          warnLog(s"[RetrieveStandingRequestScheduleController][retrieveStandingRequestScheduleDetails]:" +
            s"Unexpected Failure returned from StandingRequestScheduleRetrievalService, status - $status")
          Status(status)(Json.obj("status" -> status.toString, "body" -> body))
      }
  }

}
