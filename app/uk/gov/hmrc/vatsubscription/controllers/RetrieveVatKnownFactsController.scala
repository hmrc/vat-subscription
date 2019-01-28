/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.vatsubscription.services.VatKnownFactsRetrievalService._
import uk.gov.hmrc.vatsubscription.services._
import uk.gov.hmrc.vatsubscription.services.VatKnownFactsRetrievalService.{Migration, Forbidden => ForbiddenResponse}

import scala.concurrent.ExecutionContext

@Singleton
class RetrieveVatKnownFactsController @Inject()(vatKnownFactsRetrievalService: VatKnownFactsRetrievalService)
                                               (implicit ec: ExecutionContext) extends BaseController {

  def retrieveVatKnownFacts(vatNumber: String): Action[AnyContent] = Action.async {
    implicit user =>
      vatKnownFactsRetrievalService.retrieveVatKnownFacts(vatNumber) map {
        case Right(knownFacts) => Ok(Json.toJson(knownFacts))
        case Left(InvalidVatNumber) =>
          Logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: InvalidVatNumber returned from CustomerDetailsRetrieval Service")
          BadRequest
        case Left(VatNumberNotFound) =>
          Logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: VatNumberNotFound returned from CustomerDetailsRetrieval Service")
          NotFound
        case Left(InvalidVatKnownFacts) =>
          Logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: InvalidVatKnownFacts returned from CustomerDetailsRetrieval Service")
          BadGateway
        case Left(ForbiddenResponse) =>
          Logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: Forbidden returned from CustomerDetailsRetrieval Service")
          Forbidden
        case Left(Migration) =>
          Logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: Forbidden (Migration) returned from CustomerDetailsRetrieval Service")
          PreconditionFailed
        case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) =>
          Logger.debug(s"[RetrieveVatKnownFactsController][retrieveVatKnownFacts]: " +
            s"Unexpected Failure returned from CustomerDetailsRetrieval Service, status - $status")
          Status(status)(Json.obj("status" -> status, "body" -> body))
      }
  }
}
