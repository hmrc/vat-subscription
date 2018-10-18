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
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration.DeregistrationInfo

case class UpdateVatSubscription(messageType: String = "SubscriptionUpdate",
                                 controlInformation: ControlInformation,
                                 requestedChanges: RequestedChanges,
                                 updatedPPOB: Option[UpdatedPPOB],
                                 updatedReturnPeriod: Option[UpdatedReturnPeriod],
                                 updateDeregistrationInfo: Option[DeregistrationInfo],
                                 declaration: Declaration)

object UpdateVatSubscription {

  val currentDESApi1365Writes: Writes[UpdateVatSubscription] = writes(RequestedChanges.currentDESApi1365Writes)

  val latestDESApi1365Writes: Writes[UpdateVatSubscription] = writes(RequestedChanges.latestDESApi1365Writes)

  def writes(requestedChangesWrites: Writes[RequestedChanges]): Writes[UpdateVatSubscription] = (
    (__ \ "messageType").write[String] and
      (__ \ "controlInformation").write[ControlInformation] and
      (__ \ "requestedChange").write[RequestedChanges](requestedChangesWrites) and
      (__ \ "contactDetails").writeNullable[UpdatedPPOB] and
      (__ \ "returnPeriods").writeNullable[UpdatedReturnPeriod] and
      (__ \ "deregistrationInfo").writeNullable[DeregistrationInfo] and
      (__ \ "declaration").write[Declaration]
    )(unlift(UpdateVatSubscription.unapply))

}

case class ControlInformation(welshIndicator: Boolean,
                              source: String = "100",
                              mandationStatus: String = "1")

object ControlInformation {
  implicit val writes: Writes[ControlInformation] = Json.writes[ControlInformation]
}