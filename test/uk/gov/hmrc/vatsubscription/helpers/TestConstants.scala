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

package uk.gov.hmrc.vatsubscription.helpers

import java.util.UUID

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.vatsubscription.config.Constants.{MtdVatEnrolmentKey, MtdVatReferenceKey}
import uk.gov.hmrc.vatsubscription.models.{CustomerDetails, FlatRateScheme, MTDfBMandated, VatCustomerInformation}


object TestConstants {
  val testVatNumber: String = UUID.randomUUID().toString

  val testMtdVatEnrolment: Enrolment = Enrolment(MtdVatEnrolmentKey).withIdentifier(MtdVatReferenceKey, testVatNumber)

  val testSuccessDesResponse: JsObject = {
    val testIndividualJson = Json.obj("title" -> "00001",
      "firstName" -> "testFirstName",
      "middleName" -> "testMiddleName",
      "lastName" -> "testLastName")

    Json.obj(
      "approvedInformation" -> Json.obj(
        "customerDetails" -> Json.obj("individual" -> testIndividualJson,
          "organisationName" -> "testOrganisationName",
          "tradingName" -> "testTradingName",
          "mandationStatus" -> "1"
        )
      )
    )
  }

  val testErrorMsg = "this is an error"
  val hasFlatRateSchemeNo: Boolean = false
  val hasFlatRateSchemeYes: Boolean = true

  val testCustomerDetails = CustomerDetails(Some("testFirstName"),
    Some("testLastName"),
    Some("testOrganisationName"),
    Some("testTradingName"),
    hasFlatRateSchemeNo)

  val testCustomerDetailsWithFlatRateScheme = CustomerDetails(Some("testFirstName"),
    Some("testLastName"),
    Some("testOrganisationName"),
    Some("testTradingName"),
    hasFlatRateSchemeYes)

  val testCustomerInformation = VatCustomerInformation(MTDfBMandated, testCustomerDetails, None)
  val testCustomerInformationWithFlatRateScheme = VatCustomerInformation(MTDfBMandated, testCustomerDetails,
    Some(FlatRateScheme(Some("001"), Some(BigDecimal("123.12")), Some(true), Some("2001-01-01"))))

}
