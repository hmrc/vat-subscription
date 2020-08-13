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

package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import controllers.actions.VatAuthorised
import models.updateVatSubscription.request.deregistration.DeregistrationInfo
import services.{RequestDeregistrationService, VatCustomerDetailsRetrievalService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RequestDeregistrationController @Inject()(VatAuthorised: VatAuthorised,
                                                requestDeregistrationService: RequestDeregistrationService,
                                                vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService,
                                                cc: ControllerComponents)
                                               (implicit ec: ExecutionContext) extends BackendController(cc)
                                                with MicroserviceBaseController {

  def deregister(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      parseJsonBody[DeregistrationInfo] match {
        case Right(period) =>
          vatCustomerDetailsRetrievalService.extractWelshIndicator(vrn).flatMap {
            case Right(welshIndicator) =>
              requestDeregistrationService.deregister(period, welshIndicator).map {
                case Right(success) => Ok(Json.toJson(success))
                case Left(error) => InternalServerError(Json.toJson(error))
              }
            case Left(error) => Future.successful(InternalServerError(Json.toJson(error)))
          }
        case Left(error) =>
          Future.successful(BadRequest(Json.toJson(error)))
      }
  }

}
