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
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants.ControlList
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.{MtdEligible, MtdIneligible}

object KnownFactsAndControlListInformationStub extends WireMockMethods {

  private def stubGetKnownFactsAndControlListInformation(vatNumber: String)(status: Int, body: Option[JsValue]): StubMapping =
    when(method = GET, uri = s"/vat/known-facts/control-list/$vatNumber",
      headers = Map(
        "Authorization" -> "Bearer dev",
        "Environment" -> "dev"
      )
    ).thenReturn(status = status, body = body)

  def stubGetKnownFactsAndControlListInformation(vatNumber: String, response: MtdEligible): Unit = {
    val body = Json.obj(
      "postcode" -> response.businessPostcode,
      "dateOfReg" -> response.vatRegistrationDate,
      "controlListInformation" -> ControlList.eligible
    )
    stubGetKnownFactsAndControlListInformation(vatNumber)(OK, Some(body))
  }

  def stubGetKnownFactsAndControlListInformation(vatNumber: String, response: MtdIneligible.type): Unit = {
    val body = Json.obj(
      "postcode" -> testPostCode,
      "dateOfReg" -> testDateOfRegistration,
      "controlListInformation" -> ControlList.ineligible
    )
    stubGetKnownFactsAndControlListInformation(vatNumber)(OK, Some(body))
  }

  def stubSuccessGetKnownFactsAndControlListInformation(vatNumber: String): StubMapping =
    stubGetKnownFactsAndControlListInformation(vatNumber)(OK, Some(successResponseBody))

  def stubFailureControlListVatNumberNotFound(vatNumber: String): StubMapping =
    stubGetKnownFactsAndControlListInformation(vatNumber)(NOT_FOUND, None)

  def stubFailureKnownFactsInvalidVatNumber(vatNumber: String): StubMapping =
    stubGetKnownFactsAndControlListInformation(vatNumber)(BAD_REQUEST, None)

  private def successResponseBody =
    Json.obj(
      "postcode" -> testPostCode,
      "dateOfReg" -> testDateOfRegistration,
      "controlListInformation" -> ControlList.eligible
    )


}
