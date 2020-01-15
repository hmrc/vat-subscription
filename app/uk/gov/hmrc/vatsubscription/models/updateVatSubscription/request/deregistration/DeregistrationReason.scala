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

sealed trait DeregistrationReason {
  val value: String
  val desValue: String
}
case object CeasedTrading extends DeregistrationReason {
  override val value: String = "ceased"
  override val desValue: String = "0003"
}
case object ReducedTurnover extends DeregistrationReason {
  override val value: String = "belowThreshold"
  override val desValue: String = "0010"
}
case object ZeroRated extends DeregistrationReason {
  override val value: String = "zeroRated"
  override val desValue: String = "0006"
}

object DeregistrationReason {

  implicit val frontendReads: Reads[DeregistrationReason] = new Reads[DeregistrationReason] {
    override def reads(json: JsValue): JsResult[DeregistrationReason] = json match {
      case JsString(CeasedTrading.value) => JsSuccess(CeasedTrading)
      case JsString(ReducedTurnover.value) => JsSuccess(ReducedTurnover)
      case JsString(ZeroRated.value) => JsSuccess(ZeroRated)
      case x => JsError(s"Invalid deregistration reason supplied of '$x'")
    }
  }

  implicit val desWrites: Writes[DeregistrationReason] = Writes { value => JsString(value.desValue) }

}
