/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, Writes, __}
import config.AppConfig
import models.MandationStatus.desReader
import models.get.PPOBGet

case class PendingChanges(ppob: Option[PPOBGet],
                          bankDetails: Option[BankDetails],
                          returnPeriod: Option[ReturnPeriod],
                          mandationStatus: Option[MandationStatus],
                          commsPreference: Option[CommsPreference],
                          tradingName: Option[String],
                          organisationName: Option[String])

object PendingChanges {

  private val ppobPath = __ \ "PPOBDetails"
  private val bankDetailsPath =  __ \ "bankDetails"
  private val returnPeriodPath = __ \ "returnPeriod"
  private val mandationStatusDesPath = __ \ "mandationStatus" \ "mandationStatus"
  private val commsPreferenceDesPath = __ \ "commsPreference" \ "commsPreference"
  private val tradingNamePath = __ \ "organisationDetails" \ "tradingName"
  private val organisationNamePath = __ \ "organisationDetails" \ "organisationName"

  val reads: AppConfig => Reads[PendingChanges] = conf => for {
    ppob <- ppobPath.readNullable[PPOBGet]
    bankDetails <- bankDetailsPath.readNullable[BankDetails]
    returnPeriod <- returnPeriodPath.readNullable[ReturnPeriod](ReturnPeriod.inFlightReads)
    mandationStatus <- mandationStatusDesPath.readNullable[MandationStatus](desReader).orElse(Reads.pure(None))
    commsPref <- commsPreferenceDesPath.readNullable[CommsPreference].orElse(Reads.pure(None))
    tradingName <- tradingNamePath.readNullable[String].orElse(Reads.pure(None))
    organisationName <- organisationNamePath.readNullable[String].orElse(Reads.pure(None))
  } yield PendingChanges(
    ppob,
    bankDetails,
    returnPeriod,
    mandationStatus,
    commsPref,
    tradingName,
    organisationName
  )

  private val mandationStatusWritesPath = __ \ "mandationStatus"
  private val commsPreferenceWritesPath = __ \ "commsPreference"
  private val tradingNameWritesPath = __ \ "tradingName"
  private val organisationNameWritesPath = __ \ "organisationName"

  implicit val writes: Writes[PendingChanges] = (
    ppobPath.writeNullable[PPOBGet] and
    bankDetailsPath.writeNullable[BankDetails] and
    returnPeriodPath.writeNullable[ReturnPeriod] and
    mandationStatusWritesPath.writeNullable[MandationStatus](MandationStatus.writer) and
    commsPreferenceWritesPath.writeNullable[CommsPreference](CommsPreference.writes) and
    tradingNameWritesPath.writeNullable[String] and
    organisationNameWritesPath.writeNullable[String]
  )(unlift(PendingChanges.unapply))
}
