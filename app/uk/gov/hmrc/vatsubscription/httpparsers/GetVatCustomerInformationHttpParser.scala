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

import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.vatsubscription.models._

object GetVatCustomerInformationHttpParser {
  type GetVatCustomerInformationHttpParserResponse = Either[GetVatCustomerInformationFailure, VatCustomerInformation]

  implicit object GetVatCustomerInformationHttpReads extends HttpReads[GetVatCustomerInformationHttpParserResponse] {
    override def read(method: String, url: String, response: HttpResponse): GetVatCustomerInformationHttpParserResponse =
      response.status match {
        case OK =>
          Logger.debug("[CustomerCircumstancesHttpParser][read]: Status OK")
          response.json.validate(VatCustomerInformation.desReader) match {
            case JsSuccess(vatCustomerInformation, _) => Right(vatCustomerInformation)
            case _ =>
              Logger.warn(s"[CustomerCircumstancesHttpParser][read]: Invalid Success Response Json")
              Left(UnexpectedGetVatCustomerInformationFailure(OK, response.body))
          }
        case BAD_REQUEST =>
          Logger.warn("[CustomerCircumstancesHttpParser][read]: Unexpected response, status BAD REQUEST returned")
          Left(InvalidVatNumber)
        case NOT_FOUND =>
          Logger.warn("[CustomerCircumstancesHttpParser][read]: Unexpected response, status NOT FOUND returned")
          Left(VatNumberNotFound)
        case status =>
          Logger.warn(s"[CustomerCircumstancesHttpParser][read]: Unexpected response, status $status returned")
          Left(UnexpectedGetVatCustomerInformationFailure(status, response.body))
      }
  }

}

sealed trait GetVatCustomerInformationFailure

case object InvalidVatNumber extends GetVatCustomerInformationFailure

case object VatNumberNotFound extends GetVatCustomerInformationFailure

case class UnexpectedGetVatCustomerInformationFailure(status: Int, body: String) extends GetVatCustomerInformationFailure
