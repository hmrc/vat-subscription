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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration

import play.api.libs.json._

sealed trait BelowThreshold {
  val value: String
  val desValue: String
}
case object BelowPast12Months extends BelowThreshold {
  override val value: String = "belowPast12Months"
  override val desValue: String = "3"
}
case object BelowNext12Months extends BelowThreshold {
  override val value: String = "belowNext12Months"
  override val desValue: String = "2"
}

object BelowThreshold {

  implicit val frontendReads: Reads[BelowThreshold] = new Reads[BelowThreshold] {
    override def reads(json: JsValue): JsResult[BelowThreshold] = json match {
      case JsString(BelowPast12Months.value) => JsSuccess(BelowPast12Months)
      case JsString(BelowNext12Months.value) => JsSuccess(BelowNext12Months)
      case x => JsError(s"Invalid Below Threshold Value of '$x'")
    }
  }

  implicit val desWrites: Writes[BelowThreshold] = Writes { value => JsString(value.desValue) }

}
