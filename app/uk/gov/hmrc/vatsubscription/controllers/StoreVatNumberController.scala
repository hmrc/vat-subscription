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
import play.api.mvc.Action
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.config.Constants._
import uk.gov.hmrc.vatsubscription.httpparsers.AgentClientRelationshipsHttpParser.NoRelationshipCode
import uk.gov.hmrc.vatsubscription.models.StoreVatNumberRequest
import uk.gov.hmrc.vatsubscription.services.StoreVatNumberService._
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.ExecutionContext


@Singleton
class StoreVatNumberController @Inject()(val authConnector: AuthConnector,
                                         storeVatNumberService: StoreVatNumberService
                                        )(implicit ec: ExecutionContext)
  extends BaseController with AuthorisedFunctions {

  def storeVatNumber: Action[StoreVatNumberRequest] =
    Action.async(parse.json[StoreVatNumberRequest]) {
      implicit req =>
        val requestObj = req.body

        authorised().retrieve(Retrievals.allEnrolments) {
          enrolments =>
            storeVatNumberService.storeVatNumber(requestObj.vatNumber, enrolments, requestObj.postCode, requestObj.registrationDate) map {
              case Right(StoreVatNumberSuccess) =>
                Created
              case Left(DoesNotMatchEnrolment) =>
                Forbidden(Json.obj(HttpCodeKey -> "DoesNotMatchEnrolment"))
              case Left(InsufficientEnrolments) =>
                Forbidden(Json.obj(HttpCodeKey -> "InsufficientEnrolments"))
              case Left(RelationshipNotFound) =>
                Forbidden(Json.obj(HttpCodeKey -> NoRelationshipCode))
              case Left(KnownFactsMismatch) =>
                Forbidden(Json.obj(HttpCodeKey -> "KNOWN_FACTS_MISMATCH"))
              case Left(VatNotFound | VatInvalid) =>
                PreconditionFailed
              case Left(Ineligible) =>
                UnprocessableEntity
              case Left(AlreadySubscribed) =>
                Conflict
              case Left(VatNumberDatabaseFailure) =>
                InternalServerError
              case Left(AgentServicesConnectionFailure | VatSubscriptionConnectionFailure) =>
                BadGateway
            }
        }
    }

}
