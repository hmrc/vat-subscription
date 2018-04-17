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
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.vatsubscription.models._

object KnownFactsAndControlListInformationHttpParser {
  type KnownFactsAndControlListInformationHttpParserResponse = Either[KnownFactsAndControlListInformationFailure, KnownFactsAndControlListInformation]

  implicit object KnownFactsAndControlListInformationHttpReads extends HttpReads[KnownFactsAndControlListInformationHttpParserResponse] {
    override def read(method: String, url: String, response: HttpResponse): KnownFactsAndControlListInformationHttpParserResponse = {

      response.status match {
        case OK => response.json.validate[KnownFactsAndControlListInformation] match {
          case JsSuccess(knownFactsControlList, _) => Right(knownFactsControlList)
          case _ => Left(UnexpectedKnownFactsAndControlListInformationFailure(OK, response.body))
        }
        case BAD_REQUEST => Left(KnownFactsInvalidVatNumber)
        case NOT_FOUND => Left(ControlListInformationVatNumberNotFound)
        case status => Left(UnexpectedKnownFactsAndControlListInformationFailure(status, response.body))
      }
    }
  }

}

sealed trait KnownFactsAndControlListInformationFailure

case object KnownFactsInvalidVatNumber extends KnownFactsAndControlListInformationFailure

case object ControlListInformationVatNumberNotFound extends KnownFactsAndControlListInformationFailure

case class UnexpectedKnownFactsAndControlListInformationFailure(status: Int, body: String) extends KnownFactsAndControlListInformationFailure
