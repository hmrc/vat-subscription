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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class UpdateVatSubscription(messageType: String = "SubscriptionUpdate",
                                 controlInformation: ControlInformation = ControlInformation(),
                                 requestedChanges: RequestedChanges,
                                 updatedPPOB: Option[UpdatedPPOB],
                                 updatedReturnPeriod: Option[UpdatedReturnPeriod],
                                 updateDeregInfo: Option[DeregInfo],
                                 declaration: Declaration)

object UpdateVatSubscription {

  implicit val writes: Writes[UpdateVatSubscription] = (
    (JsPath \ "messageType").write[String] and
    (JsPath \ "controlInformation").write[ControlInformation] and
    (JsPath \ "requestedChange").write[RequestedChanges] and
    (JsPath \ "contactDetails").writeNullable[UpdatedPPOB] and
    (JsPath \ "returnPeriods").writeNullable[UpdatedReturnPeriod] and
    (JsPath \ "deregistrationInfo").writeNullable[DeregInfo] and
    (JsPath \ "declaration").write[Declaration]
  )(unlift(UpdateVatSubscription.unapply))
}

case class ControlInformation(source: String = "100",
                              mandationStatus: String = "1")

object ControlInformation {
  implicit val writes: Writes[ControlInformation] = Json.writes[ControlInformation]
}