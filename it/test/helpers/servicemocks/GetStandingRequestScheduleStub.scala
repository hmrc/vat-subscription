/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.JsValue
import play.api.libs.json.Json


object GetStandingRequestScheduleStub extends WireMockMethods {

  def stubGetStandingRequestSchedule(vatNumber: String)(status: Int, body: JsValue): StubMapping = {
    println(s"Stubbing GET request for VAT Number: $vatNumber with status: $status")
    println(s"Expected Body: ${Json.prettyPrint(body)}")
    when(method = GET, uri = s"/RESTAdapter/VAT/standing-requests/VRN/$vatNumber",
      headers = Map(
        "Authorization" -> "Y2xpZW50SWQ6Y2xpZW50U2VjcmV0",
        "Environment" -> "dev"
      )
    ).thenReturn(status = status, body = body)
  }
}
