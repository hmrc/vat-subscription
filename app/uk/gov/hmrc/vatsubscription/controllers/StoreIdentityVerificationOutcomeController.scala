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

import javax.inject.Inject

import play.api.libs.json.JsPath
import play.api.mvc.Action
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.httpparsers.IdentityVerified
import uk.gov.hmrc.vatsubscription.services.IdentityVerificationOrchestrationService
import uk.gov.hmrc.vatsubscription.services.IdentityVerificationOrchestrationService.
{IdentityNotVerified, IdentityVerificationConnectionFailure, IdentityVerificationDatabaseFailure}

import scala.concurrent.ExecutionContext

class StoreIdentityVerificationOutcomeController @Inject()(val authConnector: AuthConnector,
                                                           identityVerificationOrchestrationService: IdentityVerificationOrchestrationService)
                                                          (implicit ec: ExecutionContext)
  extends BaseController with AuthorisedFunctions {

  private val journeyIdKey = "journeyId"

  def storeIdentityVerificationOutcome(vatNumber: String): Action[String] =
    Action.async(parse.json((JsPath \ journeyIdKey).read[String])) {
      implicit req =>
        authorised() {
            val journeyId = req.body
            identityVerificationOrchestrationService.checkIdentityVerification(vatNumber, journeyId) map {
              case Right(IdentityVerified) => NoContent
              case Left(IdentityNotVerified) => Forbidden
              case Left(IdentityVerificationDatabaseFailure) => InternalServerError
              case Left(IdentityVerificationConnectionFailure) => BadGateway
            }
        }
    }
}
