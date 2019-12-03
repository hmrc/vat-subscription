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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration

import java.time.LocalDate

import play.api.libs.json._

case class DeregistrationInfo(deregReason: DeregistrationReason,
                              deregDate: Option[LocalDate],
                              deregLaterDate: Option[LocalDate],
                              turnoverBelowThreshold: Option[TurnoverBelowThreshold],
                              zeroRatedExmpApplication: Option[ZeroRatedExmpApplication],
                              optionToTax: Boolean,
                              intendSellCapitalAssets: Boolean,
                              additionalTaxInvoices: Boolean,
                              cashAccountingScheme: Boolean,
                              optionToTaxValue: Option[BigDecimal],
                              stocksValue: Option[BigDecimal],
                              capitalAssetsValue: Option[BigDecimal],
                              transactorOrCapacitorEmail: Option[String]) {

  val ottStocksAssetsValue: BigDecimal = List(optionToTaxValue, stocksValue, capitalAssetsValue).flatten.sum

  private val validateCeasedTrading: JsResult[DeregistrationInfo] = (turnoverBelowThreshold, deregDate) match {
    case (Some(_), _) => JsError("unexpected turnoverBelowThreshold object when journey is ceasedTrading")
    case (_, None) => JsError("deregDate is mandatory when journey is ceasedTrading")
    case _ => JsSuccess(this)
  }

  private val validateReducedTurnover: JsResult[DeregistrationInfo] = turnoverBelowThreshold match {
    case None =>
      JsError("turnoverBelowThreshold is mandatory when deregReason is belowThreshold")
    case _ => JsSuccess(this)
  }

  private val validateZeroRated: JsResult[DeregistrationInfo] = zeroRatedExmpApplication match{
    case None => JsError("zeroRatedExmpApplication is mandatory when deregReason is zeroRated")
    case _ => JsSuccess(this)
  }

  val validate: JsResult[DeregistrationInfo] =
    (optionToTax, optionToTaxValue, intendSellCapitalAssets, capitalAssetsValue) match {
      case (true, None, _, _) => JsError("optionToTaxValue is mandatory when optionToTax is true")
      case (_, _, true, None) => JsError("capitalAssetsValue is mandatory when intendSellCapitalAssets is true")
      case (_, _, _, _) => deregReason match {
        case ReducedTurnover => validateReducedTurnover
        case CeasedTrading => validateCeasedTrading
        case ZeroRated => validateZeroRated
      }
    }
}

object DeregistrationInfo {

  implicit val frontendReads: Reads[DeregistrationInfo] = new Reads[DeregistrationInfo] {
    override def reads(json: JsValue): JsResult[DeregistrationInfo] = {
        json.validate[DeregistrationInfo](Json.reads[DeregistrationInfo]) match {
          case JsSuccess(model, _) => model.validate
          case err => err
        }
    }
  }

  implicit val desWrites: Writes[DeregistrationInfo] = Writes { model =>
    JsObject(
      List(
        Some("deregReason" -> Json.toJson(model.deregReason)),
        Some("deregDate" -> Json.toJson(model.deregDate.getOrElse(LocalDate.now()))),
        model.deregLaterDate.map("deregLaterDate" -> Json.toJson(_)),
        model.turnoverBelowThreshold.map("turnoverBelowDeregLimit" -> Json.toJson(_)),
        model.zeroRatedExmpApplication.map("zeroRatedExmpApplication" -> Json.toJson(_)),
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
