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

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import controllers.actions.VatAuthorised
import models.ReturnPeriod
import services.{UpdateReturnPeriodService, VatCustomerDetailsRetrievalService}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateReturnPeriodController @Inject()(VatAuthorised: VatAuthorised,
                                             updateReturnPeriodService: UpdateReturnPeriodService,
                                             vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService,
                                             cc: ControllerComponents)
                                            (implicit ec: ExecutionContext) extends BackendController(cc)
                                            with MicroserviceBaseController {

  def updateVatReturnPeriod(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      parseJsonBody[ReturnPeriod](user, ReturnPeriod.frontendRds) match {
        case Right(period) =>
          vatCustomerDetailsRetrievalService.extractWelshIndicator(vrn).flatMap {
            case Right(welshIndicator) =>
              updateReturnPeriodService.updateReturnPeriod(period, welshIndicator).map {
                case Right(success) => Ok(Json.toJson(success))
                case Left(error) =>
                  warnLog(s"[UpdateReturnPeriodController][updateVatReturnPeriod] Failed to update Vat Return Period: ${error.reason}")
                  InternalServerError(Json.toJson(error))
              }
            case Left(error) =>
              warnLog(s"[UpdateReturnPeriodController][updateVatReturnPeriod] Failed trying to extract welsh indicator: ${error}")
              Future.successful(InternalServerError(Json.toJson(error)))
          }
          case Left(error) =>
            Future.successful(BadRequest(Json.toJson(error)))
        }
      }

}
