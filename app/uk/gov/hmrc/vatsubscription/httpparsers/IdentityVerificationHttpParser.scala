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
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object IdentityVerificationHttpParser {

  type GetIdentityVerificationOutcome = Either[IdentityVerificationOutcomeErrorResponse, IdentityVerificationOutcome]

  val SuccessCode = "Success"

  implicit object GetIdentityVerificationOutcomeHttpReads extends HttpReads[GetIdentityVerificationOutcome] {

    override def read(method: String, url: String, response: HttpResponse): GetIdentityVerificationOutcome = {

      lazy val code = (response.json \ "result").as[String]

      response.status match {
        case OK => if(code == SuccessCode) Right(IdentityVerified) else Right(IdentityNotVerified(code))
        case status => Left(IdentityVerificationOutcomeErrorResponse(status, response.body))
      }
    }
  }
}


sealed trait IdentityVerificationOutcome

case object IdentityVerified extends IdentityVerificationOutcome

case class IdentityNotVerified(code: String) extends IdentityVerificationOutcome

case class IdentityVerificationOutcomeErrorResponse(status: Int, body: String)
