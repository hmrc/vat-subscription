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

package uk.gov.hmrc.vatsubscription.helpers.servicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json, Writes}
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.config.Constants._

object AuthStub extends WireMockMethods {
  val authority = "/auth/authorise"

  def stubAuth[T](status: Int, body: T)(implicit writes: Writes[T]): StubMapping = {
    when(method = POST, uri = authority)
      .thenReturn(status = status, body = writes.writes(body))
  }

  private def exceptionHeaders(value: String) = Map(HeaderNames.WWW_AUTHENTICATE -> s"""MDTP detail="$value"""")

  def stubAuthFailure(): StubMapping = {
    when(method = POST, uri = authority)
      .thenReturn(status = UNAUTHORIZED, headers = exceptionHeaders("MissingBearerToken"))
  }

  def successfulAuthResponse(enrolments: JsObject*): JsObject = Json.obj(
    "allEnrolments" -> enrolments
  )

  val agentEnrolment: JsObject = Json.obj(
    "key" -> AgentEnrolmentKey,
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "AgentReferenceNumber",
        "value" -> testAgentNumber
      )
    )
  )
}
