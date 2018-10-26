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

package uk.gov.hmrc.vatsubscription.models

import play.api.libs.json._
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.models.get.PPOBGet
import uk.gov.hmrc.vatsubscription.utils.JsonReadUtil

case class VatCustomerInformation(mandationStatus: MandationStatus,
                                  customerDetails: CustomerDetails,
                                  flatRateScheme: Option[FlatRateScheme],
                                  ppob: Option[PPOBGet],
                                  bankDetails: Option[BankDetails],
                                  returnPeriod: Option[ReturnPeriod],
                                  deregistration: Option[Deregistration],
                                  changeIndicators: Option[ChangeIndicators],
                                  pendingChanges: Option[PendingChanges],
                                  partyType: Option[String] = None)

object VatCustomerInformation extends JsonReadUtil {

  val approvedInformationKey = "approvedInformation"
  val pendingChangesKey = "inFlightInformation"
  val changes = "inFlightChanges"
  val changeIndicators = "changeIndicators"

  val customerDetailsKey = "customerDetails"
  val individualKey = "individual"
  val firstNameKey = "firstName"
  val lastNameKey = "lastName"
  val organisationNameKey = "organisationName"
  val tradingNameKey = "tradingName"
  val mandationStatusKey = "mandationStatus"
  val welshIndicatorKey = "welshIndicator"
  val isPartialMigrationKey = "isPartialMigration"
  val flatRateSchemeKey = "flatRateScheme"
  val ppobKey = "PPOB"
  val bankDetailsKey = "bankDetails"
  val returnPeriodKey = "returnPeriod"
  val vatRegistrationDateKey = "effectiveRegistrationDate"
  val deregistrationKey = "deregistration"
  val partyTypeKey = "partyType"

  private val path = __ \ approvedInformationKey
  private val customerDetailsPath = path \ customerDetailsKey
  private val flatRateSchemePath = path \ flatRateSchemeKey
  private val ppobPath = path \ ppobKey
  private val bankDetailsPath = path \ bankDetailsKey
  private val returnPeriodPath = path \ returnPeriodKey
  private val deregistrationPath = path \ deregistrationKey

  private val changeIndicatorsPath = __ \ pendingChangesKey \ changeIndicators
  private val pendingChangesPath = __ \ pendingChangesKey \ changes

  val currentReads: Reads[VatCustomerInformation] = for {
    firstName <- (customerDetailsPath \ individualKey \ firstNameKey).readOpt[String]
    lastName <- (customerDetailsPath \ individualKey \ lastNameKey).readOpt[String]
    organisationName <- (customerDetailsPath \ organisationNameKey).readOpt[String]
    tradingName <- (customerDetailsPath \ tradingNameKey).readOpt[String]
    vatRegistrationDate <- (customerDetailsPath \ vatRegistrationDateKey).readOpt[String]
    mandationStatus <- (customerDetailsPath \ mandationStatusKey).read[MandationStatus]
    welshIndicator <- (customerDetailsPath \ welshIndicatorKey).readOpt[Boolean]
    isPartialMigration <- (customerDetailsPath \ isPartialMigrationKey).readOpt[Boolean]
    flatRateScheme <- flatRateSchemePath.readOpt[FlatRateScheme]
    ppob <- ppobPath.readOpt[PPOBGet]
    bankDetails <- bankDetailsPath.readOpt[BankDetails]
    returnPeriod <- returnPeriodPath.readOpt[ReturnPeriod](ReturnPeriod.currentDesReads)
    deregistration <- deregistrationPath.readOpt[Deregistration]
    changeIndicators <- changeIndicatorsPath.readOpt[ChangeIndicators]
    pendingChanges <- pendingChangesPath.readOpt[PendingChanges](PendingChanges.currentReads)
  } yield VatCustomerInformation(
    mandationStatus,
    CustomerDetails(
      firstName = firstName,
      lastName = lastName,
      organisationName = organisationName,
      tradingName = tradingName,
      vatRegistrationDate,
      flatRateScheme.isDefined,
      welshIndicator,
      isPartialMigration.contains(true)
    ),
    flatRateScheme,
    ppob,
    bankDetails,
    returnPeriod,
    deregistration,
    changeIndicators,
    pendingChanges
  )

  val newReads: Reads[VatCustomerInformation] = for {
    firstName <- (customerDetailsPath \ individualKey \ firstNameKey).readOpt[String]
    lastName <- (customerDetailsPath \ individualKey \ lastNameKey).readOpt[String]
    organisationName <- (customerDetailsPath \ organisationNameKey).readOpt[String]
    tradingName <- (customerDetailsPath \ tradingNameKey).readOpt[String]
    vatRegistrationDate <- (customerDetailsPath \ vatRegistrationDateKey).readOpt[String]
    mandationStatus <- (customerDetailsPath \ mandationStatusKey).read[MandationStatus]
    welshIndicator <- (customerDetailsPath \ welshIndicatorKey).readOpt[Boolean]
    isPartialMigration <- (customerDetailsPath \ isPartialMigrationKey).readOpt[Boolean]
    flatRateScheme <- flatRateSchemePath.readOpt[FlatRateScheme]
    ppob <- ppobPath.readOpt[PPOBGet]
    bankDetails <- bankDetailsPath.readOpt[BankDetails]
    returnPeriod <- returnPeriodPath.readOpt[ReturnPeriod](ReturnPeriod.currentDesReads)
    deregistration <- deregistrationPath.readOpt[Deregistration]
    changeIndicators <- changeIndicatorsPath.readOpt[ChangeIndicators]
    pendingChanges <- pendingChangesPath.readOpt[PendingChanges](PendingChanges.newReads)
    partyType <- (customerDetailsPath \ partyTypeKey).readOpt[String]
  } yield VatCustomerInformation(
    mandationStatus,
    CustomerDetails(
      firstName = firstName,
      lastName = lastName,
      organisationName = organisationName,
      tradingName = tradingName,
      vatRegistrationDate,
      flatRateScheme.isDefined,
      welshIndicator,
      isPartialMigration.contains(true)
    ),
    flatRateScheme,
    ppob,
    bankDetails,
    returnPeriod,
    deregistration,
    changeIndicators,
    pendingChanges,
    partyType
  )

  implicit val writes: Writes[VatCustomerInformation] = Json.writes[VatCustomerInformation]

}
