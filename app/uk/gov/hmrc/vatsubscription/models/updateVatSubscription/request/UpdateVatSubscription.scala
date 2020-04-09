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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.models.MandationStatus
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration.DeregistrationInfo
import uk.gov.hmrc.vatsubscription.models.MandationStatus.{desReader, desReaderOld, desWriter}

case class UpdateVatSubscription(messageType: String = "SubscriptionUpdate",
                                 controlInformation: ControlInformation,
                                 requestedChanges: RequestedChanges,
                                 updatedPPOB: Option[UpdatedPPOB],
                                 updatedReturnPeriod: Option[UpdatedReturnPeriod],
                                 updateDeregistrationInfo: Option[DeregistrationInfo],
                                 declaration: Declaration)

object UpdateVatSubscription {

  val DESApi1365WritesR7: Writes[UpdateVatSubscription] = writes(RequestedChanges.DESApi1365WritesR7)
  val DESApi1365WritesR11: Writes[UpdateVatSubscription] = writes(RequestedChanges.DESApi1365WritesR11)

  def writes(requestedChangesWrites: Writes[RequestedChanges]): Writes[UpdateVatSubscription] = (
    (__ \ "messageType").write[String] and
    (__ \ "controlInformation").write[ControlInformation] and
    (__ \ "requestedChange").write[RequestedChanges](requestedChangesWrites) and
    (__ \ "contactDetails").writeNullable[UpdatedPPOB] and
    (__ \ "returnPeriods").writeNullable[UpdatedReturnPeriod] and
    (__ \ "deregistrationInfo").writeNullable[DeregistrationInfo] and
    (__ \ "declaration").write[Declaration]
  ) (unlift(UpdateVatSubscription.unapply))
}

case class ControlInformation(welshIndicator: Boolean,
                              source: String = "100",
                              mandationStatus: Option[MandationStatus] = None)

object ControlInformation {
  private val welshIndicatorPath = JsPath \ "welshIndicator"
  private val sourcePath = JsPath \ "source"
  private val mandationStatusPath = JsPath \ "mandationStatus"

  implicit val reads: AppConfig => Reads[ControlInformation] = conf => for {
    welshIndicator <- welshIndicatorPath.read[Boolean]
    source <- sourcePath.read[String]
    mandationStatus <- mandationStatusPath.readNullable[MandationStatus](
      if (conf.features.newStatusIndicators()) desReader else desReaderOld
    )
  } yield ControlInformation(welshIndicator, source, mandationStatus)

  implicit val writes: Writes[ControlInformation] = (
    welshIndicatorPath.write[Boolean] and
    sourcePath.write[String] and
    mandationStatusPath.writeNullable[MandationStatus](desWriter)
  ) (unlift(ControlInformation.unapply))
}
