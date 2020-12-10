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

package models.updateVatSubscription.request

import play.api.libs.functional.syntax._
import play.api.libs.json._
import config.AppConfig
import models.MandationStatus.{desReader, desReaderOld, desWriter}
import models.updateVatSubscription.request.deregistration.DeregistrationInfo
import models.{CommsPreference, MandationStatus}

case class UpdateVatSubscription(messageType: String = "SubscriptionUpdate",
                                 controlInformation: ControlInformation,
                                 requestedChanges: RequestedChanges,
                                 organisationDetails: Option[UpdatedOrganisationDetails],
                                 updatedPPOB: Option[UpdatedPPOB],
                                 updatedReturnPeriod: Option[UpdatedReturnPeriod],
                                 updateDeregistrationInfo: Option[DeregistrationInfo],
                                 declaration: Declaration,
                                 commsPreference: Option[CommsPreference])

object UpdateVatSubscription {

  val DESApi1365Writes: AppConfig => Writes[UpdateVatSubscription] = appConfig => writes(RequestedChanges.DESApi1365Writes(appConfig))

  def writes(requestedChangesWrites: Writes[RequestedChanges]): Writes[UpdateVatSubscription] = (
    (__ \ "messageType").write[String] and
    (__ \ "controlInformation").write[ControlInformation] and
    (__ \ "requestedChange").write[RequestedChanges](requestedChangesWrites) and
    (__ \ "organisationDetails").writeNullable[UpdatedOrganisationDetails] and
    (__ \ "contactDetails").writeNullable[UpdatedPPOB] and
    (__ \ "returnPeriods").writeNullable[UpdatedReturnPeriod] and
    (__ \ "deregistrationInfo").writeNullable[DeregistrationInfo] and
    (__ \ "declaration").write[Declaration] and
    (__ \ "commsPreference").writeNullable[CommsPreference](CommsPreference.updatePreferenceWrites)
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
