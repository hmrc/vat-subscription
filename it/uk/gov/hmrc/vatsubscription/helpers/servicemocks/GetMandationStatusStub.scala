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
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.vatsubscription.models.{MTDfBVoluntary, MandationStatus}
import uk.gov.hmrc.vatsubscription.httpparsers.GetMandationStatusHttpParser.MandationStatusKey

object GetMandationStatusStub extends WireMockMethods {
  def stubGetMandationStatus(vatNumber: String)(status: Int, body: JsValue): StubMapping =
    when(method = GET, uri = s"/vat-subscription/$vatNumber/mandation-status")
      .thenReturn(status = status, body = body)

  def mandationStatusBody(mandationStatus: MandationStatus): JsObject = Json.obj(MandationStatusKey -> mandationStatus.Name)
}
