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

import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads

class GetVatCustomerInformationHttpParserSpec extends UnitSpec {
  val testHttpVerb = "GET"
  val testUri = "/"

  "CustomerSignUpHttpReads" when {
    "read" should {
      "parse an OK response with a valid json as a VatCustomerInformation" in {
        val httpResponse = HttpResponse(OK, responseJson = Some(customerInformationDESJsonMaxWithFRS))

        val res = GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(customerInformationModelMaxWithFRS)
      }

      "parse an OK response with an ivalid json as a UnexpectedGetVatCustomerInformationFailure" in {
        val httpResponse = HttpResponse(OK, responseJson = Some(Json.obj()))

        val res = GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(UnexpectedGetVatCustomerInformationFailure(OK, "{ }"))
      }

      "parse a BAD_REQUEST response as a InvalidVatNumber" in {
        val httpResponse = HttpResponse(BAD_REQUEST)

        val res = GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(InvalidVatNumber)
      }

      "parse a NOT_FOUND response as a VatNumberNotFound" in {
        val httpResponse = HttpResponse(NOT_FOUND)

        val res = GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(VatNumberNotFound)
      }

      "parse any other response as a UnexpectedGetVatCustomerInformationFailure" in {
        val httpResponse = HttpResponse(INTERNAL_SERVER_ERROR, responseJson = Some(Json.obj()))

        val res = GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, "{ }"))
      }
    }
  }
}
