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

import connectors.{InvalidVatNumber, Migration, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound, Forbidden => ForbiddenResponse}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.MandationStatusService
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class MandationStatusController @Inject()(val authConnector: AuthConnector,
                                          mandationStatusService: MandationStatusService,
                                          cc: ControllerComponents)
                                         (implicit ec: ExecutionContext)
  extends BackendController(cc) with AuthorisedFunctions with LoggingUtil {

  val mandationStatusKey = "mandationStatus"

  def getMandationStatus(vatNumber: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        mandationStatusService.getMandationStatus(vatNumber) map {
          case Right(status) => Ok(Json.obj(mandationStatusKey -> status.value))
          case Left(InvalidVatNumber) =>
            infoLog(s"[MandationStatusController][getMandationStatus]: InvalidVatNumber returned from MandationStatusService")
            BadRequest(Json.toJson(InvalidVatNumber))
          case Left(VatNumberNotFound) =>
            infoLog(s"[MandationStatusController][getMandationStatus]: VatNumberNotFound returned from MandationStatusService")
            NotFound(Json.toJson(VatNumberNotFound))
          case Left(ForbiddenResponse) =>
            infoLog(s"[MandationStatusController][getMandationStatus]: Forbidden returned from MandationStatusService")
            Forbidden(Json.toJson(ForbiddenResponse))
          case Left(Migration) =>
            infoLog(s"[MandationStatusController][getMandationStatus]: Migration returned from MandationStatusService")
            PreconditionFailed(Json.toJson(Migration))
          case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) =>
            warnLog(s"[MandationStatusController][getMandationStatus]: Unexpected Failure returned from MandationStatusService, status - $status")
            Status(status)(Json.obj("status" -> status.toString, "body" -> body))
        }
      }
  }
}
