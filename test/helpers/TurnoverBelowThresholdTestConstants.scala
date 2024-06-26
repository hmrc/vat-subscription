/*
 * Copyright 2024 HM Revenue & Customs
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

package helpers

import play.api.libs.json.{JsValue, Json}
import models.updateVatSubscription.request.deregistration.{BelowPast12Months, TurnoverAlreadyBelow, TurnoverBelowThreshold}

object TurnoverBelowThresholdTestConstants {

  val nextTwelveMonthsTurnoverAmt: BigDecimal = 650.55

  val turnoverBelowThresholdFrontendJsonMax: JsValue = Json.obj(
    "belowThreshold" -> BelowPast12Months.value,
    "nextTwelveMonthsTurnover" -> nextTwelveMonthsTurnoverAmt,
    "whyTurnoverBelow" -> WhyTurnoverBelowTestConstants.whyTurnoverBelowJsonMax
  )

  val turnoverBelowThresholdDESJsonMax: JsValue = Json.obj(
    "aboveBelowThreshold" -> BelowPast12Months.desValue,
    "taxableSuppliesValue" -> nextTwelveMonthsTurnoverAmt,
    "reason" -> WhyTurnoverBelowTestConstants.whyTurnoverBelowModelMax.toString
  )

  val turnoverBelowThresholdFrontendJsonMin: JsValue = Json.obj(
    "belowThreshold" -> BelowPast12Months.value,
    "nextTwelveMonthsTurnover" -> nextTwelveMonthsTurnoverAmt
  )

  val turnoverBelowThresholdDESJsonMin: JsValue = Json.obj(
    "aboveBelowThreshold" -> BelowPast12Months.desValue,
    "taxableSuppliesValue" -> nextTwelveMonthsTurnoverAmt,
    "reason" -> "Already below the threshold"
  )

  val turnoverBelowThresholdModelMax: TurnoverBelowThreshold = TurnoverBelowThreshold(
    BelowPast12Months,
    nextTwelveMonthsTurnoverAmt,
    WhyTurnoverBelowTestConstants.whyTurnoverBelowModelMax
  )

  val turnoverBelowThresholdModelMin: TurnoverBelowThreshold = TurnoverBelowThreshold(
    BelowPast12Months,
    nextTwelveMonthsTurnoverAmt,
    TurnoverAlreadyBelow
  )


}
