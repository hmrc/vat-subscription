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

import config.AppConfig

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import controllers.actions.VatAuthorised
import models.post.PPOBPost
import services.{UpdatePPOBService, VatCustomerDetailsRetrievalService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdatePPOBController @Inject()(VatAuthorised: VatAuthorised,
                                     updatePPOBService: UpdatePPOBService,
                                     vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService,
                                     cc: ControllerComponents,
                                     appConfig: AppConfig)
                                    (implicit ec: ExecutionContext) extends BackendController(cc) with MicroserviceBaseController {

  def updatePPOB(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
        parseJsonBody[PPOBPost] match {
          case Right(updatedPPOB) =>
            vatCustomerDetailsRetrievalService.extractWelshIndicator(vrn).flatMap {
              case Right(welshIndicator) => {
                updatePPOBService.updatePPOB(updatedPPOB, welshIndicator, appConfig).map {
                  case Right(success) => Ok(Json.toJson(success))
                  case Left(error) if error.code == "CONFLICT" =>
                    warnLog(s"[UpdatePPOBController][updatePPOB] Failed to update PPOB conflict: ${error.reason}")
                    Conflict(Json.toJson(error))
                  case Left(error) =>
                    warnLog(s"[UpdatePPOBController][updatePPOB] Failed to update PPOB: ${error.reason}")
                    InternalServerError(Json.toJson(error))
                }
              }
              case Left(error) =>
                warnLog(s"[UpdatePPOBController][updatePPOB] Failed trying to get welsh indicator: ${error}")
                Future.successful(InternalServerError(Json.toJson(error)))
            }
          case Left(error) =>
            Future.successful(BadRequest(Json.toJson(error)))
        }
      }
}





