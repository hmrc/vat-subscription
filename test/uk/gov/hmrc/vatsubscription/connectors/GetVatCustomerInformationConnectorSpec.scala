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

package uk.gov.hmrc.vatsubscription.connectors

import assets.TestUtil
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK, FORBIDDEN}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants.{customerInformationDESJsonMaxWithFRS,
  customerInformationModelMaxWithFRS, migrationDESJson}

class GetVatCustomerInformationConnectorSpec extends TestUtil {

  val testHttpVerb = "GET"
  val testUri = "/"

  "GetVatCustomerInformationConnector GetVatCustomerInformationHttpReads" should {

    val httpClient = mock[HttpClient]
    val mockConnector: GetVatCustomerInformationConnector = new GetVatCustomerInformationConnector(httpClient, mockAppConfig)

    "read" should {

      "parse an OK response with a valid json as a VatCustomerInformation" in {
        val httpResponse = HttpResponse(OK, responseJson = Some(customerInformationDESJsonMaxWithFRS))

        val res = mockConnector.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(customerInformationModelMaxWithFRS)
      }

      "parse an OK response with an invalid json as a UnexpectedGetVatCustomerInformationFailure" in {
        val httpResponse = HttpResponse(OK, responseJson = Some(Json.obj()))

        val res = mockConnector.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(UnexpectedGetVatCustomerInformationFailure(OK, "{ }"))
      }

      "parse a BAD_REQUEST response as a InvalidVatNumber" in {
        val httpResponse = HttpResponse(BAD_REQUEST)

        val res = mockConnector.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(InvalidVatNumber)
      }

      "parse a NOT_FOUND response as a VatNumberNotFound" in {
        val httpResponse = HttpResponse(NOT_FOUND)

        val res = mockConnector.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(VatNumberNotFound)
      }

      "parse a FORBIDDEN response with code MIGRATION as a Migration" in {
        val httpResponse = HttpResponse(FORBIDDEN, Some(migrationDESJson))

        val res = mockConnector.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(Migration)
      }

      "parse a FORBIDDEN response without code MIGRATION as a Forbidden" in {
        val httpResponse = HttpResponse(FORBIDDEN, responseJson = Some(Json.obj()))

        val res = mockConnector.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(Forbidden)
      }

      "parse any other response as a UnexpectedGetVatCustomerInformationFailure" in {
        val httpResponse = HttpResponse(INTERNAL_SERVER_ERROR, responseJson = Some(Json.obj()))

        val res = mockConnector.GetVatCustomerInformationHttpParser.GetVatCustomerInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(UnexpectedGetVatCustomerInformationFailure(INTERNAL_SERVER_ERROR, "{ }"))
      }
    }
  }
}
