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

import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.httpparsers.AgentClientRelationshipsHttpParser._
import uk.gov.hmrc.vatsubscription.models.{CheckAgentClientRelationshipResponseFailure, HaveRelationshipResponse, NoRelationshipResponse}

class AgentClientRelationshipHttpParserSpec extends UnitSpec {
  val testHttpVerb = "GET"
  val testUri = "/"

  "CheckAgentClientRelationshipHttpReads" when {
    "read" should {
      "parse a OK response as a HaveRelationshipResponse" in {
        val httpResponse = HttpResponse(OK)

        val res = CheckAgentClientRelationshipHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(HaveRelationshipResponse)
      }

      "parse a NOT_FOUND response with the expected message as a NoRelationshipResponse" in {
        val httpResponse = HttpResponse(NOT_FOUND, Some(Json.obj("code" -> NoRelationshipCode)))

        val res = CheckAgentClientRelationshipHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(NoRelationshipResponse)
      }

      "parse a NOT_FOUND response as a CheckAgentClientRelationshipResponseFailure" in {
        val httpResponse = HttpResponse(NOT_FOUND, Some(Json.obj("code" -> NoRelationshipCode)))

        val res = CheckAgentClientRelationshipHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(NoRelationshipResponse)
      }

      "parse any other response as a CheckAgentClientRelationshipResponseFailure" in {
        val httpResponse = HttpResponse(BAD_REQUEST, Some(Json.obj()))

        val res = CheckAgentClientRelationshipHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Left(CheckAgentClientRelationshipResponseFailure(httpResponse.status))
      }
    }
  }
}
