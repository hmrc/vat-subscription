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
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object MatchUserHttpParser {
  type MatchUserResponse = Either[UserMatchFailureResponseModel, Option[String]]

  implicit object MatchUserHttpReads extends HttpReads[MatchUserResponse] {
    override def read(method: String, url: String, response: HttpResponse): MatchUserResponse =
      response.status match {
        case OK => Right((response.json \ "nino").validateOpt[String].get)
        case UNAUTHORIZED =>
          response.json.validate[UserMatchFailureResponseModel] match {
            case JsSuccess(UserMatchUnexpectedError, _) =>
              Left(UserMatchUnexpectedError)
            case JsSuccess(_, _) =>
              Right(None)
            case error@JsError(_) =>
              Left(UserMatchFailureResponseModel(error))
          }
        case _ =>
          Left(UserMatchFailureResponseModel(response))
      }
  }

}

case class UserMatchSuccessResponseModel(nino: String)

object UserMatchSuccessResponseModel {
  implicit val format: OFormat[UserMatchSuccessResponseModel] = Json.format[UserMatchSuccessResponseModel]
}

case class UserMatchFailureResponseModel(errors: String)

object UserMatchUnexpectedError extends UserMatchFailureResponseModel("Internal error: unexpected result from matching")

object UserMatchFailureResponseModel {
  implicit val format: OFormat[UserMatchFailureResponseModel] = Json.format[UserMatchFailureResponseModel]

  def apply(response: HttpResponse): UserMatchFailureResponseModel =
    UserMatchFailureResponseModel(s"status: ${response.status} body: ${response.body}")

  def apply(jsError: JsError): UserMatchFailureResponseModel = UserMatchFailureResponseModel(jsError.errors.toString)
}
