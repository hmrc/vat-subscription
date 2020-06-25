/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.mvc.Results.Ok
import play.api.libs.json.Json
import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.vatsubscription.controllers.actions.VatAuthorised
import uk.gov.hmrc.vatsubscription.models.CommsPreference
import uk.gov.hmrc.vatsubscription.models.post.CommsPreferencePost

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateContactPreferenceController @Inject()(VatAuthorised: VatAuthorised,
                                                  cc: ControllerComponents)
                                                 (implicit ec: ExecutionContext) extends BackendController(cc)
                                                  with MicroserviceBaseController {

  def updateContactPreference(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      parseJsonBody[CommsPreferencePost] match {
        case Right(updatedCommsPreference) =>
          Future(Ok)
        case Left(error) =>
          Future.successful(BadRequest(Json.toJson(error)))
      }
  }
}
