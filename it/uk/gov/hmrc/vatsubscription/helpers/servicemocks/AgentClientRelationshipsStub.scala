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

object AgentClientRelationshipsStub extends WireMockMethods {
  def checkAgentClientRelationship(agentNumber: String, vatNumber: String): String =
    s"/agent/$agentNumber/service/HMCE-VATDEC-ORG/client/$vatNumber/"

  def stubCheckAgentClientRelationship[T](agentNumber: String, vatNumber: String)(status: Int, body: T)(implicit writes: Writes[T]): StubMapping = {
    when(method = GET, uri = checkAgentClientRelationship(agentNumber, vatNumber))
      .thenReturn(status = status, body = writes.writes(body))
  }
}
