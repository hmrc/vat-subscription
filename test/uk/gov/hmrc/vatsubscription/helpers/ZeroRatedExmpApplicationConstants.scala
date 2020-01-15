/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration.ZeroRatedExmpApplication

object ZeroRatedExmpApplicationConstants {

  val zeroRatedSuppliesValue =21.43
  val estTotalTaxTurnoverValue= 46.79

  val zeroRatedExmpApplicationFrontendJsonMax: JsValue = Json.obj(
    "natureOfSupplies" ->"12345",
    "repaymentSituation" -> false,
    "zeroRatedSuppliesValue" -> zeroRatedSuppliesValue,
    "estTotalTaxTurnover" -> estTotalTaxTurnoverValue
  )

  val zeroRatedExmpApplicationFrontendJsonMin: JsValue = Json.obj(
    "repaymentSituation" -> false,
    "zeroRatedSuppliesValue" -> zeroRatedSuppliesValue,
    "estTotalTaxTurnover" -> estTotalTaxTurnoverValue
  )

  val zeroRatedExmpApplicationFrontendModelMax: ZeroRatedExmpApplication = ZeroRatedExmpApplication(
    Some("12345"),
    false,
     zeroRatedSuppliesValue,
     estTotalTaxTurnoverValue
  )

  val zeroRatedExmpApplicationFrontendModelMin: ZeroRatedExmpApplication = ZeroRatedExmpApplication(
    None,
    false,
    zeroRatedSuppliesValue,
    estTotalTaxTurnoverValue
  )
}
