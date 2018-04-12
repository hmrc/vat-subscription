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

package uk.gov.hmrc.vatsubscription.controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.httpparsers.{InvalidVatNumber, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound}
import uk.gov.hmrc.vatsubscription.services.MandationStatusService

import scala.concurrent.ExecutionContext

@Singleton
class MandationStatusController @Inject()(val authConnector: AuthConnector,
                                          mandationStatusService: MandationStatusService
                                         )(implicit ec: ExecutionContext)
  extends BaseController with AuthorisedFunctions {

  val mandationStatusKey = "mandationStatus"

  def getMandationStatus(vatNumber: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        mandationStatusService.getMandationStatus(vatNumber) map {
          case Right(status) => Ok(Json.obj(mandationStatusKey -> status))
          case Left(InvalidVatNumber) => BadRequest
          case Left(VatNumberNotFound) => NotFound
          case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) => InternalServerError(Json.obj("status" -> status, "body" -> body))
        }
      }
  }

}
