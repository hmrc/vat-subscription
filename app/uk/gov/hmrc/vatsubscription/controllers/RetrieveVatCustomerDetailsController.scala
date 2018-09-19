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

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.controllers.actions.VatAuthorised
import uk.gov.hmrc.vatsubscription.connectors.{InvalidVatNumber, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound}
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.ExecutionContext

@Singleton
class RetrieveVatCustomerDetailsController @Inject()(VatAuthorised: VatAuthorised,
                                                     vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService)
                                                    (implicit ec: ExecutionContext) extends BaseController {

  def retrieveVatCustomerDetails(vatNumber: String): Action[AnyContent] = VatAuthorised.async(vatNumber) {
    implicit user =>
      vatCustomerDetailsRetrievalService.retrieveVatCustomerDetails(vatNumber) map {
        case Right(customerDetails) => Ok(Json.toJson(customerDetails))
        case Left(InvalidVatNumber) =>
          Logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]: InvalidVatNumber returned from CustomerDetailsRetrieval Service")
          BadRequest
        case Left(VatNumberNotFound) =>
          Logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]: VatNumberNotFound returned from CustomerDetailsRetrieval Service")
          NotFound
        case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) =>
          Logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatCustomerDetails]:" +
            s"Unexpected Failure returned from CustomerDetailsRetrieval Service, status - $status")
          BadGateway(Json.obj("status" -> status, "body" -> body))
      }
  }

  def retrieveVatInformation(vatNumber: String): Action[AnyContent] = VatAuthorised.async(vatNumber) {
    implicit user =>
      vatCustomerDetailsRetrievalService.retrieveCircumstanceInformation(vatNumber) map {
        case Right(vatInformation) => Ok(Json.toJson(vatInformation))
        case Left(InvalidVatNumber) =>
          Logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]: InvalidVatNumber returned from CustomerDetailsRetrieval Service")
          BadRequest
        case Left(VatNumberNotFound) =>
          Logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]: VatNumberNotFound returned from CustomerDetailsRetrieval Service")
          NotFound
        case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) =>
          Logger.debug(s"[RetrieveVatCustomerDetailsController][retrieveVatInformation]:" +
            s"Unexpected Failure returned from CustomerDetailsRetrieval Service, status - $status")
          BadGateway(Json.obj("status" -> status, "body" -> body))
      }
  }
}
