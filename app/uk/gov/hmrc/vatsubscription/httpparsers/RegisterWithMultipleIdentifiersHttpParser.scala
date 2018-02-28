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

package uk.gov.hmrc.vatsubscription.httpparsers

import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.vatsubscription.config.Constants.Des._

object RegisterWithMultipleIdentifiersHttpParser {
  type RegisterWithMultipleIdentifiersResponse = Either[RegistrationFailure, RegistrationSuccess]

  implicit object RegisterWithMultipleIdentifiersHttpReads extends HttpReads[RegisterWithMultipleIdentifiersResponse] {
    override def read(method: String, url: String, response: HttpResponse): RegisterWithMultipleIdentifiersResponse = {
      response.status match {
        case OK =>
          (for {
            idType <- (response.json \ IdentificationKey \ 0 \ IdTypeKey).validate[String]
            if idType == SafeIdKey
            safeId <- (response.json \ IdentificationKey \ 0 \ IdValueKey).validate[String]
          } yield safeId) match {
            case JsSuccess(safeId, _) => Right(RegistrationSuccess(safeId))
            case error: JsError => Left(InvalidJsonResponse(error))
          }
        case status =>
          Left(RegistrationErrorResponse(status, response.body))
      }
    }
  }

}

sealed trait RegistrationFailure

case class RegistrationSuccess(safeId: String)

case class InvalidJsonResponse(jsError: JsError) extends RegistrationFailure

case class RegistrationErrorResponse(status: Int, body: String) extends RegistrationFailure
