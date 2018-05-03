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

import uk.gov.hmrc.play.test.UnitSpec
import SendEmailHttpParser.SendEmailHttpReads.read
import play.api.http.Status._
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.vatsubscription.httpparsers.SendEmailHttpParser.{EmailQueued, SendEmailFailure}

class SendEmailHttpParserSpec extends UnitSpec {
  val testHttpVerb = "POST"
  val testUri = "/"

  "SendEmailHttpReads" when {
    "read" should {
      "parse a NO_CONTENT as EmailQueued" in {
        val httpResponse = HttpResponse(NO_CONTENT)

        read(testHttpVerb, testUri, httpResponse) shouldBe Right(EmailQueued)
      }
      "parse any other status as a SendEmailFailure" in {
        val httpResponse = HttpResponse(BAD_REQUEST)

        read(testHttpVerb, testUri, httpResponse) shouldBe Left(SendEmailFailure(BAD_REQUEST, httpResponse.body))
      }
    }
  }
}
