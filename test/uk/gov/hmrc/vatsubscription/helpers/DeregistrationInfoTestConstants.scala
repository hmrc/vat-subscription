/*
 * Copyright 2019 HM Revenue & Customs
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

import PPOBTestConstants.email
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration.{CeasedTrading, DeregistrationInfo, ReducedTurnover, ZeroRated}

object DeregistrationInfoTestConstants {

  val deregDateString = "2018-12-22"
  val deregLaterDateString = "2018-12-30"
  val deregDate: LocalDate = LocalDate.parse(deregDateString)
  val deregLaterDate: LocalDate = LocalDate.parse(deregLaterDateString)
  val optionToTaxValue: BigDecimal = 500.21
  val stocksValue: BigDecimal = 600.99
  val captialAssetsValue: BigDecimal = 12.99

  val deregInfoCeasedTradingFrontendJson: JsValue = Json.obj(
    "deregReason" -> CeasedTrading.value,
    "deregDate" -> deregDateString,
    "deregLaterDate" -> deregLaterDateString,
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> optionToTaxValue,
    "stocksValue" -> stocksValue,
    "capitalAssetsValue" -> captialAssetsValue,
    "transactorOrCapacitorEmail" -> email
  )



  val deregInfoReducedTurnoverFrontendJson: JsValue = Json.obj(
    "deregReason" -> ReducedTurnover.value,
    "turnoverBelowThreshold" -> TurnoverBelowThresholdTestConstants.turnoverBelowThresholdFrontendJsonMax,
    "optionToTax" -> false,
    "intendSellCapitalAssets" -> false,
    "additionalTaxInvoices" -> false,
    "cashAccountingScheme" -> true
  )

  val deregInfoZeroRatedExmpApplicationFrontendJson: JsValue = Json.obj(
    "deregReason" -> ZeroRated.value,
    "zeroRatedExmpApplication" -> ZeroRatedExmpApplicationConstants.zeroRatedExmpApplicationFrontendModelMax,
    "optionToTax" -> false,
    "intendSellCapitalAssets" -> false,
    "additionalTaxInvoices" -> false,
    "cashAccountingScheme" -> true
  )

  val deregInfoCeasedTradingDESJson: JsValue = Json.obj(
    "deregReason" -> CeasedTrading.desValue,
    "deregDate" -> deregDateString,
    "deregLaterDate" -> deregLaterDateString,
    "deregDetails" -> Json.obj(
      "optionTaxProperty" -> true,
      "intendSellCapitalAssets" -> true,
      "additionalTaxInvoices" -> true,
      "cashAccountingScheme" -> true,
      "OTTStocksAssetsValue" -> (optionToTaxValue + captialAssetsValue + stocksValue)
    )
  )

  val deregInfoReducedTurnoverDESJson: JsValue = Json.obj(
    "deregReason" -> ReducedTurnover.desValue,
    "deregDate" -> LocalDate.now(),
    "turnoverBelowDeregLimit" -> TurnoverBelowThresholdTestConstants.turnoverBelowThresholdDESJsonMax,
    "deregDetails" -> Json.obj(
      "optionTaxProperty" -> false,
      "intendSellCapitalAssets" -> false,
      "additionalTaxInvoices" -> false,
      "cashAccountingScheme" -> true,
      "OTTStocksAssetsValue" -> 0
    )
  )

  val deregInfoZeroRatedExmpApplicationDESJson: JsValue = Json.obj(
    "deregReason" -> ZeroRated.desValue,
    "deregDate" -> LocalDate.now(),
    "zeroRatedExmpApplication" -> ZeroRatedExmpApplicationConstants.zeroRatedExmpApplicationFrontendModelMax,
    "deregDetails" -> Json.obj(
      "optionTaxProperty" -> false,
      "intendSellCapitalAssets" -> false,
      "additionalTaxInvoices" -> false,
      "cashAccountingScheme" -> true,
      "OTTStocksAssetsValue" -> 0
    )
  )
  val deregInfoCeasedTradingModel: DeregistrationInfo = DeregistrationInfo(
    CeasedTrading,
    Some(deregDate),
    Some(deregLaterDate),
    None,
    None,
    optionToTax = true,
    intendSellCapitalAssets = true,
    additionalTaxInvoices = true,
    cashAccountingScheme = true,
    Some(optionToTaxValue),
    Some(stocksValue),
    Some(captialAssetsValue),
    Some(email)
  )

  val deregistrationInfoReducedTurnoverModel: DeregistrationInfo = DeregistrationInfo(
    ReducedTurnover,
    None,
    None,
    Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdModelMax),
    None,
    optionToTax = false,
    intendSellCapitalAssets = false,
    additionalTaxInvoices = false,
    cashAccountingScheme = true,
    None,
    None,
    None,
    None
  )

  val deregistrationInfoZeroRatedExmpApplicationModel: DeregistrationInfo = DeregistrationInfo(
    ZeroRated,
    None,
    None,
    None,
    Some(ZeroRatedExmpApplicationConstants.zeroRatedExmpApplicationFrontendModelMax),
    optionToTax = false,
    intendSellCapitalAssets = false,
    additionalTaxInvoices = false,
    cashAccountingScheme = true,
    None,
    None,
    None,
    None
  )

}
