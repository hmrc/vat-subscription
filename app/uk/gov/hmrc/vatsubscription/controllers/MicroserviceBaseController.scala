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

import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.mvc.AnyContentAsJson
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.models.User
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.ErrorModel

trait MicroserviceBaseController extends BaseController {

  def parseJsonBody[T](implicit user: User[_], rds: Reads[T]): Either[ErrorModel, T] = user.body match {
    case body: AnyContentAsJson => body.json.validate[T] match {
      case e: JsError => {
        Logger.debug(s"[MicroserviceBaseController][parseJsonBody] Json received, but did not validate. Errors: $e")
        Logger.warn("[MicroserviceBaseController][parseJsonBody] Json received, but did not validate")
        Left(ErrorModel("INVALID_JSON", s"Json received, but did not validate"))
      }
      case s: JsSuccess[T] => Right(s.value)
    }
    case _ =>
      Logger.warn("[MicroserviceBaseController][parseJsonBody] Body of request was not JSON")
      Left(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${user.body}"))
  }

}
