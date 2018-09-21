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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration

import java.time.LocalDate

import play.api.libs.functional.syntax.unlift
import play.api.libs.json
import play.api.libs.json._

case class DeregistrationInfo(deregReason: DeregistrationReason,
                              deregDate: Option[LocalDate],
                              deregLaterDate: Option[LocalDate],
                              turnoverBelowThreshold: Option[TurnoverBelowThreshold],
                              optionToTax: Boolean,
                              intendSellCapitalAssets: Boolean,
                              additionalTaxInvoices: Boolean,
                              cashAccountingScheme: Boolean,
                              optionToTaxValue: Option[BigDecimal],
                              stocksValue: Option[BigDecimal],
                              capitalAssetsValue: Option[BigDecimal]) {

  val ottStocksAssetsValue: BigDecimal = List(optionToTaxValue, stocksValue, capitalAssetsValue).flatten.sum

}

object DeregistrationInfo {

  implicit val frontendReads: Reads[DeregistrationInfo] = Json.reads[DeregistrationInfo]

  implicit val desWrites: Writes[DeregistrationInfo] = Writes { model =>
    JsObject(
      List(
        Some("deregReason" -> Json.toJson(model.deregReason)),
        model.deregDate.map("deregDate" -> Json.toJson(_)),
        model.deregLaterDate.map("deregLaterDate" -> Json.toJson(_)),
        model.turnoverBelowThreshold.map("turnoverBelowDeregLimit" -> Json.toJson(_)),
        Some("deregDetails" -> JsObject(
          List(
            "optionTaxProperty" -> JsBoolean(model.optionToTax),
            "intendSellCapitalAssets" -> JsBoolean(model.intendSellCapitalAssets),
            "additionalTaxInvoices" -> JsBoolean(model.additionalTaxInvoices),
            "cashAccountingScheme" -> JsBoolean(model.cashAccountingScheme),
            "OTTStocksAssetsValue" -> JsNumber(model.ottStocksAssetsValue)
          )
        ))
      ).flatten
    )
  }
}
