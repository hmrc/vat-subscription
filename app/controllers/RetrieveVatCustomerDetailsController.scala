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

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import config.AppConfig
import controllers.actions.VatAuthorised
import connectors.{InvalidVatNumber, Migration, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound, Forbidden => ForbiddenResult}
import services._
import utils.LoggerUtil
import scala.concurrent.ExecutionContext

@Singleton
class RetrieveVatCustomerDetailsController @Inject()(VatAuthorised: VatAuthorised,
                                                     vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService,
                                                     appConfig: AppConfig,
                                                     cc: ControllerComponents)
                                                    (implicit ec: ExecutionContext) extends BackendController(cc) with LoggerUtil {

  def retrieveVatCustomerDetails(vatNumber: String): Action[AnyContent] = VatAuthorised.async(vatNumber) {
    implicit user =>
      vatCustomerDetailsRetrievalService.retrieveVatCustomerDetails(vatNumber) map {
        case Right(customerDetails) => Ok(Json.toJson(customerDetails))
        case Left(InvalidVatNumber) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]: InvalidVatNumber returned from CustomerDetailsRetrieval Service")
          BadRequest(Json.toJson(InvalidVatNumber.status))
        case Left(VatNumberNotFound) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]: VatNumberNotFound returned from CustomerDetailsRetrieval Service")
          NotFound(Json.toJson(VatNumberNotFound.status))
        case Left(ForbiddenResult) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]:" +
            s"Forbidden returned from CustomerDetailsRetrieval Service")
          Forbidden(Json.toJson(ForbiddenResult.status))
        case Left(Migration) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]:" +
            s"Forbidden (MIGRATION) returned from CustomerDetailsRetrieval Service")
          PreconditionFailed(Json.toJson(Migration.status))
        case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]:" +
            s"Unexpected Failure returned from CustomerDetailsRetrieval Service, status - $status")
          Status(status)(Json.obj("status" -> status.toString, "body" -> body))
      }
  }

  def retrieveVatInformation(vatNumber: String): Action[AnyContent] = VatAuthorised.async(vatNumber) {
    implicit user =>
      vatCustomerDetailsRetrievalService.retrieveCircumstanceInformation(vatNumber) map {
        case Right(vatInformation) =>
          if (vatInformation.changeIndicators.isEmpty)
            logger.debug("[RetrieveVatCustomerDetailsController][retrieveVatInformation]: No changeIndicators object returned from GetCustomerInformation")
          Ok(Json.toJson(vatInformation))
        case Left(InvalidVatNumber) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]: InvalidVatNumber returned from CustomerDetailsRetrieval Service")
          BadRequest(Json.toJson(InvalidVatNumber.status))
        case Left(VatNumberNotFound) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]: VatNumberNotFound returned from CustomerDetailsRetrieval Service")
          NotFound(Json.toJson(VatNumberNotFound.status))
        case Left(ForbiddenResult) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]:" +
            s"Forbidden returned from CustomerDetailsRetrieval Service")
          Forbidden(Json.toJson(ForbiddenResult.status))
        case Left(Migration) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]:" +
            s"Forbidden (MIGRATION) returned from CustomerDetailsRetrieval Service")
          PreconditionFailed(Json.toJson(Migration.status))
        case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) =>
          logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]:" +
            s"Unexpected Failure returned from CustomerDetailsRetrieval Service, status - $status")
          Status(status)(Json.obj("status" -> status.toString, "body" -> body))
      }
  }

}
