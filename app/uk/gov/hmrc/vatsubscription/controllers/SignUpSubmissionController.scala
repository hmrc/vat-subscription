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

import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.services.SignUpSubmissionService
import uk.gov.hmrc.vatsubscription.services.SignUpSubmissionService._

import scala.concurrent.ExecutionContext

class SignUpSubmissionController @Inject()(val authConnector: AuthConnector,
                                           signUpSubmissionService: SignUpSubmissionService)
                                          (implicit ec: ExecutionContext)
  extends BaseController with AuthorisedFunctions {

  def submitSignUpRequest(vatNumber: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(Retrievals.allEnrolments) {
        enrolments =>
          signUpSubmissionService.submitSignUpRequest(vatNumber, enrolments) map {
            case Right(SignUpRequestSubmitted) => NoContent
            case Left(InsufficientData) => BadRequest
            case _ => BadGateway
          }
      }
  }

}
