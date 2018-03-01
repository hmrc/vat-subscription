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

import play.api.http.Status.{BAD_REQUEST, NO_CONTENT}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.httpparsers.TaxEnrolmentsHttpParser.TaxEnrolmentsHttpReads

class TaxEnrolmentsConnectorSpec extends UnitSpec {
  val testHttpVerb = "PUT"
  val testUri = "/"

  "TaxEnrolmentsHttpReads" when {
    "read" should {
      "parse a NO_CONTENT response as a SuccessfulTaxEnrolment" in {
        val httpResponse = HttpResponse(NO_CONTENT)

        val res = TaxEnrolmentsHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(SuccessfulTaxEnrolment)
      }

      "parse any other response with the expected message as a FailedTaxEnrolment" in {
        val httpResponse = HttpResponse(BAD_REQUEST)

        val res = TaxEnrolmentsHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(FailedTaxEnrolment(BAD_REQUEST))
      }
    }
  }
}
