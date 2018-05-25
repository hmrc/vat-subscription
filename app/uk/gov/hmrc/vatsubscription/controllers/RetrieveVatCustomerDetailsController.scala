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

import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.auth.core.authorise.RawJsonPredicate
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, Enrolment}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.httpparsers.{InvalidVatNumber, UnexpectedGetVatCustomerInformationFailure, VatNumberNotFound}
import uk.gov.hmrc.vatsubscription.services._
import uk.gov.hmrc.vatsubscription.utils.EnrolmentUtils._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveVatCustomerDetailsController @Inject()(val authConnector: AuthConnector,
                                                     vatCustomerDetailsRetrievalService: VatCustomerDetailsRetrievalService)
                                                    (implicit ec: ExecutionContext)
  extends BaseController with AuthorisedFunctions {

  def retrieveVatCustomerDetails(vatNumber: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(
        RawJsonPredicate(JsArray(Seq(Json.toJson(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", vatNumber)
          .withDelegatedAuthRule("mtd-vat-auth")))))
      ).retrieve(Retrievals.allEnrolments) {
        enrolments =>
          if (enrolments.mtdVatRef.isDefined) {
            vatCustomerDetailsRetrievalService.retrieveVatCustomerDetails(vatNumber) map {
              case Right(customerDetails) => Ok(Json.toJson(customerDetails))
              case Left(InvalidVatNumber) => BadRequest
              case Left(VatNumberNotFound) => NotFound
              case Left(UnexpectedGetVatCustomerInformationFailure(status, body)) => BadGateway(Json.obj("status" -> status, "body" -> body))
            }
          } else {
            Future.successful(Forbidden)
          }
      }
  }

}
