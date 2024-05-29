/*
 * Copyright 2024 HM Revenue & Customs
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

import models.User
import models.updateVatSubscription.response.ErrorModel
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.mvc.AnyContentAsJson
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggingUtil


trait MicroserviceBaseController extends BackendController with LoggingUtil {

  def parseJsonBody[T](implicit user: User[_], rds: Reads[T]): Either[ErrorModel, T] = user.body match {
    case body: AnyContentAsJson => body.json.validate[T] match {
      case e: JsError =>
        logger.debug(s"[MicroserviceBaseController][parseJsonBody] Json received, but did not validate. Errors: $e")
        warnLog("[MicroserviceBaseController][parseJsonBody] Json received, but did not validate")
        Left(ErrorModel("INVALID_JSON", s"Json received, but did not validate"))
      case s: JsSuccess[T] => Right(s.value)
    }
    case _ =>
      warnLog("[MicroserviceBaseController][parseJsonBody] Body of request was not JSON")
      Left(ErrorModel("INVALID_JSON", s"Body of request was not JSON, ${user.body}"))
  }

}
