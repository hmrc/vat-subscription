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

package uk.gov.hmrc.vatsubscription.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, Writes, __}
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.models.MandationStatus.{desReader, desReaderOld}
import uk.gov.hmrc.vatsubscription.models.ReturnPeriod.filterReturnPeriod
import uk.gov.hmrc.vatsubscription.models.get.PPOBGet

case class PendingChanges(ppob: Option[PPOBGet],
                          bankDetails: Option[BankDetails],
                          returnPeriod: Option[ReturnPeriod],
                          mandationStatus: Option[MandationStatus],
                          commsPreference: Option[CommsPreference])

object PendingChanges {

  private val ppobPath = __ \ "PPOBDetails"
  private val bankDetailsPath =  __ \ "bankDetails"
  private val returnPeriodPath = __ \ "returnPeriod"
  private val mandationStatusDesPath = __ \ "mandationStatus" \ "mandationStatus"
  private val commsPreferenceDesPath = __ \ "commsPreference" \ "commsPreference"

  val reads: AppConfig => Reads[PendingChanges] = conf => for {
    ppob <- ppobPath.readNullable[PPOBGet]
    bankDetails <- bankDetailsPath.readNullable[BankDetails]
    returnPeriod <- returnPeriodPath.readNullable[ReturnPeriod](ReturnPeriod.inFlightReads)
    mandationStatus <- mandationStatusDesPath.readNullable[MandationStatus](
      if (conf.features.newStatusIndicators()) desReader else desReaderOld
    ).orElse(Reads.pure(None))
    commsPref <- commsPreferenceDesPath.readNullable[CommsPreference].orElse(Reads.pure(None))
  } yield PendingChanges(
    ppob,
    bankDetails,
    filterReturnPeriod(returnPeriod, conf),
    mandationStatus,
    commsPref
  )

  private val mandationStatusWritesPath = __ \ "mandationStatus"
  private val commsPreferenceWritesPath = __ \ "commsPreference"

  implicit val writes: Writes[PendingChanges] = (
    ppobPath.writeNullable[PPOBGet] and
    bankDetailsPath.writeNullable[BankDetails] and
    returnPeriodPath.writeNullable[ReturnPeriod] and
    mandationStatusWritesPath.writeNullable[MandationStatus](MandationStatus.writer) and
    commsPreferenceWritesPath.writeNullable[CommsPreference]
  )(unlift(PendingChanges.unapply))
}
