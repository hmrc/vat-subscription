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
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.services.VatKnownFactsRetrievalService._
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.ExecutionContext

@Singleton
class RetrieveVatKnownFactsController @Inject()(vatKnownFactsRetrievalService: VatKnownFactsRetrievalService)
                                               (implicit ec: ExecutionContext) extends BaseController {

  def retrieveVatKnownFacts(vatNumber: String): Action[AnyContent] = Action.async {
    implicit user =>
      vatKnownFactsRetrievalService.retrieveVatKnownFacts(vatNumber) map {
        case Right(knownFacts) => Ok(Json.toJson(knownFacts))
        case Left(InvalidVatNumber) => BadRequest
        case Left(VatNumberNotFound) => NotFound
        case Left(InvalidVatKnownFacts) => BadGateway
        case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) => BadGateway(Json.obj("status" -> status, "body" -> body))
      }
  }

}
