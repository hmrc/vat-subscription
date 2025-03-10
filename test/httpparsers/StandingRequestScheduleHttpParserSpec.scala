/*
 * Copyright 2024 HM Revenue & Customs
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

package httpparsers

import connectors._
import helpers.StandingRequestScheduleConstants._
import helpers.TestUtil
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

class StandingRequestScheduleHttpParserSpec extends TestUtil {

  val testHttpVerb = "GET"
  val testUri = "/"

  "StandingRequestScheduleHttpParserReads" should {

    val httpParser = new StandingRequestScheduleHttpParser(mockAppConfig)

    "read" should {

      "parse an OK response with a valid json as a StandingRequestSchedule" in {

        val httpResponse = HttpResponse(OK, standingRequestScheduleJson.toString)

        val res = httpParser.GetStandingRequestScheduleHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(standingRequestScheduleModel)
      }

      "parse an INTERNAL_SERVER_ERROR response with an invalid json as a UnexpectedStandingRequestScheduleFailure" in {

        val httpResponse = HttpResponse(OK,Json.obj().toString)

        val res = httpParser.GetStandingRequestScheduleHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(UnexpectedStandingRequestScheduleFailure(INTERNAL_SERVER_ERROR, "Invalid Success Response Json"))
      }

      "parse a BAD_REQUEST response as a SrsInvalidVatNumber" in {

        val httpResponse = HttpResponse(BAD_REQUEST, "")

        val res = httpParser.GetStandingRequestScheduleHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(SrsInvalidVatNumber)
      }

      "parse a NOT_FOUND response as a SrsVatNumberNotFound" in {

        val httpResponse = HttpResponse(NOT_FOUND, "")

        val res = httpParser.GetStandingRequestScheduleHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(SrsVatNumberNotFound)
      }

      "parse a FORBIDDEN response as a SrsForbidden" in {

        val httpResponse = HttpResponse(FORBIDDEN, standingRequestErrorJson.toString)

        val res = httpParser.GetStandingRequestScheduleHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(SrsForbidden)
      }

      "parse any other response as a UnexpectedStandingRequestScheduleFailure" in {

        val httpResponse = HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString)

        val res = httpParser.GetStandingRequestScheduleHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(UnexpectedStandingRequestScheduleFailure(INTERNAL_SERVER_ERROR, "{}"))
      }
    }
  }
}
