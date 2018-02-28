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
import uk.gov.hmrc.vatsubscription.models._

object CustomerSignUpHttpParser {
  type CustomerSignUpResponse = Either[CustomerSignUpResponseFailure, CustomerSignUpResponseSuccess.type]

  implicit object CustomerSignUpHttpReads extends HttpReads[CustomerSignUpResponse] {
    override def read(method: String, url: String, response: HttpResponse): CustomerSignUpResponse =
      response.status match {
        case OK => Right(CustomerSignUpResponseSuccess)
        case status => Left(CustomerSignUpResponseFailure(status))
      }
  }

}
