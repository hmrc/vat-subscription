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
import services.VatKnownFactsRetrievalService.{Migration, Forbidden => ForbiddenResponse, _}
import services._
import utils.LoggerUtil
import scala.concurrent.ExecutionContext

@Singleton
class RetrieveVatKnownFactsController @Inject()(vatKnownFactsRetrievalService: VatKnownFactsRetrievalService,
                                                cc: ControllerComponents)
                                               (implicit ec: ExecutionContext) extends BackendController(cc) with LoggerUtil {

  def retrieveVatKnownFacts(vatNumber: String): Action[AnyContent] = Action.async {
    implicit user =>
      vatKnownFactsRetrievalService.retrieveVatKnownFacts(vatNumber) map {
        case Right(DeregisteredUser) =>
          Ok(Json.obj("deregistered" -> true))
        case Right(knownFacts: VatKnownFacts) =>
          Ok(Json.toJson(knownFacts))
        case Left(InvalidVatNumber) =>
          logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: InvalidVatNumber returned from CustomerDetailsRetrieval Service")
          BadRequest
        case Left(VatNumberNotFound) =>
          logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: VatNumberNotFound returned from CustomerDetailsRetrieval Service")
          NotFound
        case Left(InvalidVatKnownFacts) =>
          logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: InvalidVatKnownFacts returned from CustomerDetailsRetrieval Service")
          BadGateway
        case Left(ForbiddenResponse) =>
          logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: Forbidden returned from CustomerDetailsRetrieval Service")
          Forbidden
        case Left(Migration) =>
          logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: Forbidden (Migration) returned from CustomerDetailsRetrieval Service")
          PreconditionFailed
        case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) =>
          logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: " +
            s"Unexpected Failure returned from CustomerDetailsRetrieval Service, status - $status")
          Status(status)(Json.obj("status" -> status, "body" -> body))
      }
  }
}
