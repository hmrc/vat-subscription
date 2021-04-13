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

import play.api.libs.json._
import config.AppConfig
import models.MandationStatus.{desReader, desReaderOld}
import models.ReturnPeriod.filterReturnPeriod
import models.get.{PPOBAddressGet, PPOBGet}
import utils.{JsonObjectSugar, JsonReadUtil}

case class VatCustomerInformation(mandationStatus: MandationStatus,
                                  customerDetails: CustomerDetails,
                                  flatRateScheme: Option[FlatRateScheme],
                                  ppob: PPOBGet,
                                  bankDetails: Option[BankDetails],
                                  returnPeriod: Option[ReturnPeriod],
                                  deregistration: Option[Deregistration],
                                  changeIndicators: Option[ChangeIndicators],
                                  pendingChanges: Option[PendingChanges],
                                  partyType: Option[PartyType] = None,
                                  primaryMainCode: Option[String],
                                  missingTrader: Boolean = false,
                                  commsPreference: Option[CommsPreference]
                                 ) {

  val pendingPPOBAddress: Option[PPOBAddressGet] = pendingChanges.flatMap(_.ppob.map(_.address))
  val pendingBankDetails: Option[BankDetails] = pendingChanges.flatMap(_.bankDetails)
  val pendingContactEmail: Option[String] = pendingChanges.flatMap(_.ppob.flatMap(_.contactDetails.flatMap(_.emailAddress)))
  val pendingLandLine: Option[String] = pendingChanges.flatMap(_.ppob.flatMap(_.contactDetails.flatMap(_.phoneNumber)))
  val pendingMobile: Option[String] = pendingChanges.flatMap(_.ppob.flatMap(_.contactDetails.flatMap(_.mobileNumber)))
}

object VatCustomerInformation extends JsonReadUtil with JsonObjectSugar {

  val approvedInformationKey = "approvedInformation"
  val pendingChangesKey = "inFlightInformation"
  val changes = "inFlightChanges"
  val changeIndicators = "changeIndicators"

  val customerDetailsKey = "customerDetails"
  val individualKey = "individual"
  val titleKey = "title"
  val firstNameKey = "firstName"
  val middleNameKey = "middleName"
  val lastNameKey = "lastName"
  val organisationNameKey = "organisationName"
  val tradingNameKey = "tradingName"
  val mandationStatusKey = "mandationStatus"
  val welshIndicatorKey = "welshIndicator"
  val isPartialMigrationKey = "isPartialMigration"
  val flatRateSchemeKey = "flatRateScheme"
  val overseasIndicatorKey = "overseasIndicator"
  val isInsolventKey = "isInsolvent"
  val insolvencyTypeKey = "insolvencyType"
  val insolvencyDateKey = "insolvencyDate"
  val continueToTradeKey = "continueToTrade"
  val nameIsReadOnlyKey = "nameIsReadOnly"
  val ppobKey = "PPOB"
  val bankDetailsKey = "bankDetails"
  val returnPeriodKey = "returnPeriod"
  val effectiveRegistrationDateKey = "effectiveRegistrationDate"
  val customerMigratedToETMPDateKey = "customerMigratedToETMPDate"
  val hybridToFullMigrationDateKey = "hybridToFullMigrationDate"
  val deregistrationKey = "deregistration"
  val partyTypeKey = "partyType"
  val rlsTypeKey = "RLS"
  val commsPreferenceKey = "commsPreference"

  val businessActivitiesKey = "businessActivities"
  val primaryMainCodeKey = "primaryMainCode"

  private val path = __ \ approvedInformationKey
  private val customerDetailsPath = path \ customerDetailsKey
  private val flatRateSchemePath = path \ flatRateSchemeKey
  private val overseasIndicatorPath = path \ customerDetailsKey \ overseasIndicatorKey
  private val isInsolventPath = path \ customerDetailsKey \ isInsolventKey
  private val insolvencyTypePath = path \ customerDetailsKey \ insolvencyTypeKey
  private val insolvencyDatePath = path \ customerDetailsKey \ insolvencyDateKey
  private val continueToTradePath = path \ customerDetailsKey \ continueToTradeKey
  private val nameIsReadOnlyPath = path \ customerDetailsKey \ nameIsReadOnlyKey
  private val ppobPath = path \ ppobKey
  private val bankDetailsPath = path \ bankDetailsKey
  private val returnPeriodPath = path \ returnPeriodKey
  private val deregistrationPath = path \ deregistrationKey
  private val primaryMainCodePath = path \ businessActivitiesKey \ primaryMainCodeKey
  private val rlsTypePath = ppobPath \ rlsTypeKey
  private val approvedCommsPreferencePath = ppobPath \ commsPreferenceKey

  private val changeIndicatorsPath = __ \ pendingChangesKey \ changeIndicators
  private val pendingChangesPath = __ \ pendingChangesKey \ changes

  val reads: AppConfig => Reads[VatCustomerInformation] = conf => for {
    title <- (customerDetailsPath \ individualKey \ titleKey).readOpt[String]
    firstName <- (customerDetailsPath \ individualKey \ firstNameKey).readOpt[String]
    middleName <- (customerDetailsPath \ individualKey \ middleNameKey).readOpt[String]
    lastName <- (customerDetailsPath \ individualKey \ lastNameKey).readOpt[String]
    organisationName <- (customerDetailsPath \ organisationNameKey).readOpt[String]
    tradingName <- (customerDetailsPath \ tradingNameKey).readOpt[String]
    effectiveRegistrationDate <- (customerDetailsPath \ effectiveRegistrationDateKey).readOpt[String]
    customerMigratedToETMPDate <- (customerDetailsPath \ customerMigratedToETMPDateKey).readOpt[String]
    hybridToFullMigrationDate <- (customerDetailsPath \ hybridToFullMigrationDateKey).readOpt[String]
    mandationStatus <- (customerDetailsPath \ mandationStatusKey).read[MandationStatus](
      if (conf.features.newStatusIndicators()) desReader else desReaderOld
    )
    welshIndicator <- (customerDetailsPath \ welshIndicatorKey).readOpt[Boolean]
    isPartialMigration <- (customerDetailsPath \ isPartialMigrationKey).readOpt[Boolean]
    flatRateScheme <- flatRateSchemePath.readOpt[FlatRateScheme]
    overseasIndicator <- overseasIndicatorPath.read[Boolean]
    isInsolvent <- isInsolventPath.readOpt[Boolean]
    insolvencyType <- insolvencyTypePath.readOpt[String]
    insolvencyDate <- insolvencyDatePath.readOpt[String]
    continueToTrade <- continueToTradePath.readOpt[Boolean]
    nameIsReadOnly <- nameIsReadOnlyPath.readNullable[Boolean]
    ppob <- ppobPath.read[PPOBGet]
    bankDetails <- bankDetailsPath.readOpt[BankDetails]
    returnPeriod <- returnPeriodPath.readOpt[ReturnPeriod](ReturnPeriod.currentDesReads)
    deregistration <- deregistrationPath.readOpt[Deregistration]
    changeIndicators <- changeIndicatorsPath.readOpt[ChangeIndicators]
    pendingChanges <- pendingChangesPath.readOpt[PendingChanges](PendingChanges.reads(conf))
    partyType <- (customerDetailsPath \ partyTypeKey).readOpt[PartyType](PartyType.reads)
    primaryMainCode <- primaryMainCodePath.readNullable[String].orElse(Reads.pure(None))
    rlsType <- rlsTypePath.readOpt[String]
    commsPreference <- approvedCommsPreferencePath.readOpt[CommsPreference]
  } yield VatCustomerInformation(
    mandationStatus,
    CustomerDetails(
      title = title,
      firstName = firstName,
      middleName = middleName,
      lastName = lastName,
      organisationName = organisationName,
      tradingName = tradingName,
      effectiveRegistrationDate,
      customerMigratedToETMPDate,
      hybridToFullMigrationDate,
      flatRateScheme.isDefined,
      welshIndicator,
      isPartialMigration.contains(true),
      isInsolvent,
      insolvencyType,
      insolvencyDate,
      continueToTrade,
      overseasIndicator,
      nameIsReadOnly
    ),
    flatRateScheme,
    ppob,
    bankDetails,
    filterReturnPeriod(returnPeriod, conf),
    deregistration,
    changeIndicators,
    pendingChanges,
    partyType,
    primaryMainCode,
    rlsType.nonEmpty,
    commsPreference
  )

  implicit val commsPreferenceWrites: Writes[CommsPreference] = CommsPreference.writes

  implicit val writes: Writes[VatCustomerInformation] = Writes {
    model =>
      jsonObjNoNulls(
        "mandationStatus" -> model.mandationStatus.value,
        "customerDetails" -> Json.toJson(model.customerDetails)(CustomerDetails.cdWriter),
        "flatRateScheme" -> model.flatRateScheme,
        "ppob" -> model.ppob,
        "bankDetails" -> model.bankDetails,
        "returnPeriod" -> model.returnPeriod,
        "deregistration" -> model.deregistration,
        "changeIndicators" -> model.changeIndicators,
        "pendingChanges" -> model.pendingChanges,
        "partyType" -> model.partyType,
        "primaryMainCode" -> model.primaryMainCode,
        "missingTrader" -> model.missingTrader,
        "commsPreference" -> model.commsPreference
      )
  }

}