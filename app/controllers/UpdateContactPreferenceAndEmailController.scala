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
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import controllers.actions.VatAuthorised
import models.post.CommsPreferenceEmailPost
import services.{UpdateContactPreferenceService, VatCustomerDetailsRetrievalService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateContactPreferenceAndEmailController @Inject()(VatAuthorised: VatAuthorised,
                                                          updateContactPreferenceService: UpdateContactPreferenceService,
                                                          vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService,
                                                          cc: ControllerComponents)
                                                         (implicit ec: ExecutionContext) extends BackendController(cc)
                                                                                         with MicroserviceBaseController {

  def update(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      parseJsonBody[CommsPreferenceEmailPost] match {
        case Right(model) =>
          vatCustomerDetailsRetrievalService.retrieveCircumstanceInformation(vrn).flatMap {
            case Right(details) =>
              updateContactPreferenceService.updatePreferenceAndEmail(model.emailAddress, details).map {
                case Right(success) => Ok(Json.toJson(success))
                case Left(error) =>
                  Logger.warn("[UpdateContactPreferenceAndEmailController][update] Error occurred when updating " +
                    "contact preference and email address")
                  InternalServerError(Json.toJson(error))
              }
            case Left(error) =>
              Logger.warn("[UpdateContactPreferenceAndEmailController][update] Error retrieved when retrieving " +
                "current user details")
              Future.successful(InternalServerError(Json.toJson(error)))
          }
        case Left(error) =>
          Logger.warn("[UpdateContactPreferenceAndEmailController][update] Email address not valid in request")
          Future.successful(BadRequest(Json.toJson(error)))
      }
  }
}
