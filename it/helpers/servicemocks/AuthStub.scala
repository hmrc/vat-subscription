/*
 * Copyright 2021 HM Revenue & Customs
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

package helpers.servicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json, Writes}
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.auth.core.ConfidenceLevel.L0
import config.Constants._
import helpers.IntegrationTestConstants._

object AuthStub extends WireMockMethods {
  val authority = "/auth/authorise"

  def stubAuth[T](status: Int, body: T)(implicit writes: Writes[T]): StubMapping = {
    when(method = POST, uri = authority)
      .thenReturn(status = status, body = writes.writes(body))
  }

  private val credentials = Json.obj(
    "providerId" -> "123456-credid",
    "providerType" -> "GovernmmentGateway"
  )

  private def exceptionHeaders(value: String) = Map(HeaderNames.WWW_AUTHENTICATE -> s"""MDTP detail="$value"""")

  def stubAuthFailure(): StubMapping = {
    when(method = POST, uri = authority)
      .thenReturn(status = UNAUTHORIZED, headers = exceptionHeaders("MissingBearerToken"))
  }

  def successfulAuthResponse(confidenceLevel: ConfidenceLevel, enrolments: JsObject*): JsObject = Json.obj(
    "allEnrolments" -> enrolments,
    "confidenceLevel" -> confidenceLevel.level,
    "optionalCredentials" -> credentials
  )

  def successfulAuthResponse(enrolments: JsObject*): JsObject =
    successfulAuthResponse(L0, enrolments: _*)

  val mtdVatEnrolment: JsObject = Json.obj(
    "key" -> MtdVatEnrolmentKey,
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> MtdVatReferenceKey,
        "value" -> testVatNumber
      )
    )
  )

  val agentServicesEnrolments: JsObject = Json.obj(
    "key" -> AgentServicesEnrolment,
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> AgentServicesReference,
        "value" -> testArn
      )
    )
  )

}
