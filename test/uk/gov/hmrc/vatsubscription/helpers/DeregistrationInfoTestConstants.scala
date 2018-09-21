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

import java.time.LocalDate

import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration.{CeasedTrading, DeregistrationInfo, DeregistrationReason, TurnoverBelowThreshold}

object DeregistrationInfoTestConstants {

  val deregDateString = "2018-12-22"
  val deregLaterDateString = "2018-12-30"
  val deregDate: LocalDate = LocalDate.parse(deregDateString)
  val deregLaterDate: LocalDate = LocalDate.parse(deregLaterDateString)
  val optionToTaxValue: BigDecimal = 500.21
  val stocksValue: BigDecimal = 600.99
  val captialAssetsValue: BigDecimal = 12.99

  val deregistrationInfoFrontendJsonMax: JsValue = Json.obj(
    "deregReason" -> CeasedTrading.value,
    "deregDate" -> deregDateString,
    "deregLaterDate" -> deregLaterDateString,
    "turnoverBelowThreshold" -> TurnoverBelowThresholdTestConstants.turnoverBelowThresholdFrontendJsonMax,
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> optionToTaxValue,
    "stocksValue" -> stocksValue,
    "capitalAssetsValue" -> captialAssetsValue
  )

  val deregistrationInfoModelMax: DeregistrationInfo = DeregistrationInfo(
    CeasedTrading,
    Some(deregDate),
    Some(deregLaterDate),
    Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdModelMax),
    optionToTax = true,
    intendSellCapitalAssets = true,
    additionalTaxInvoices = true,
    cashAccountingScheme = true,
    Some(optionToTaxValue),
    Some(stocksValue),
    Some(captialAssetsValue)
  )

  val deregistrationInfoDESJsonMax: JsValue = Json.obj(
    "deregReason" -> CeasedTrading.desValue,
    "deregDate" -> deregDateString,
    "deregLaterDate" -> deregLaterDateString,
    "turnoverBelowDeregLimit" -> TurnoverBelowThresholdTestConstants.turnoverBelowThresholdDESJsonMax,
    "deregDetails" -> Json.obj(
      "optionTaxProperty" -> true,
      "intendSellCapitalAssets" -> true,
      "additionalTaxInvoices" -> true,
      "cashAccountingScheme" -> true,
      "OTTStocksAssetsValue" -> (optionToTaxValue + captialAssetsValue + stocksValue)
    )
  )


  val deregistrationInfoFrontendJsonMin: JsValue = Json.obj(
    "deregReason" -> CeasedTrading.value,
    "optionToTax" -> false,
    "intendSellCapitalAssets" -> false,
    "additionalTaxInvoices" -> false,
    "cashAccountingScheme" -> true
  )

  val deregistrationInfoModelMin: DeregistrationInfo = DeregistrationInfo(
    CeasedTrading,
    None,
    None,
    None,
    optionToTax = false,
    intendSellCapitalAssets = false,
    additionalTaxInvoices = false,
    cashAccountingScheme = true,
    None,
    None,
    None
  )

  val deregistrationInfoDESJsonMin: JsValue = Json.obj(
    "deregReason" -> CeasedTrading.desValue,
    "deregDetails" -> Json.obj(
      "optionTaxProperty" -> false,
      "intendSellCapitalAssets" -> false,
      "additionalTaxInvoices" -> false,
      "cashAccountingScheme" -> true,
      "OTTStocksAssetsValue" -> 0
    )
  )
}
