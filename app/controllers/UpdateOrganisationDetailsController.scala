/*
 * Copyright 2021 HM Revenue & Customs
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

import controllers.actions.VatAuthorised
import javax.inject.{Inject, Singleton}
import models.updateVatSubscription.response.ErrorModel
import models.{BusinessName, CustomerDetails, TradingName, User}
import play.api.Logger
import play.api.libs.json.Json
import services.{UpdateOrganisationDetailsService, VatCustomerDetailsRetrievalService}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateOrganisationDetailsController @Inject()(VatAuthorised: VatAuthorised,
                                                    updateOrganisationDetailsService: UpdateOrganisationDetailsService,
                                                    vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService,
                                                    cc: ControllerComponents)
                                                   (implicit ec: ExecutionContext) extends BackendController(cc)
                                           with MicroserviceBaseController {

  def updateTradingName(vrn:String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      parseJsonBody[TradingName] match {
        case Right(tradingName) =>
          handleCustomerInfoCall(vrn, tradingName)
        case Left(error) =>
          Future.successful(BadRequest(Json.toJson(error)))
      }
  }

  def handleCustomerInfoCall(vrn: String, tradingName: TradingName)(implicit user: User[_]): Future[Result] = {
    vatCustomerDetailsRetrievalService.retrieveVatCustomerDetails(vrn).flatMap {
      case Right(customerInfo) if noNamesDefined(customerInfo) =>
        Logger.warn("[UpdateOrganisationDetailsController][updateTradingName] CustomerDetails were returned, but " +
          "the user has no individual or org names defined")
        Future.successful(InternalServerError(Json.toJson(ErrorModel("INTERNAL_SERVER_ERROR", "The service returned " +
          "CustomerDetails with no defined individual or org names"))))
      case Right(customerInfo) =>
        updateOrganisationDetailsService.updateTradingName(tradingName, customerInfo).map {
          case Right(success) => Ok(Json.toJson(success))
          case Left(error) if error.code == "CONFLICT" => Conflict(Json.toJson(error))
          case Left(error) => InternalServerError(Json.toJson(error))
        }
      case Left(error) => Future.successful(InternalServerError(Json.toJson(error)))
    }
  }

  def noNamesDefined(info: CustomerDetails): Boolean = {
    info.organisationName.isEmpty &&
      info.firstName.isEmpty &&
      info.middleName.isEmpty &&
      info.lastName.isEmpty
  }

  def updateBusinessName(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      parseJsonBody[BusinessName] match {
        case Right(businessName) =>
          vatCustomerDetailsRetrievalService.retrieveVatCustomerDetails(vrn).flatMap {
            case Right(details) =>
              updateOrganisationDetailsService.updateBusinessName(businessName, details).map {
                case Right(success) => Ok(Json.toJson(success))
                case Left(error) if error.code == "CONFLICT" => Conflict(Json.toJson(error))
                case Left(error) => InternalServerError(Json.toJson(error))
              }
            case Left(error) => Future.successful(InternalServerError(Json.toJson(error)))
          }
        case Left(error) =>
          Future.successful(BadRequest(Json.toJson(error)))
      }
  }
}
