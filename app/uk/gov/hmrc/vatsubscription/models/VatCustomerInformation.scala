/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.vatsubscription.models.get.{PPOBAddressGet, PPOBGet}
import uk.gov.hmrc.vatsubscription.models.ReturnPeriod.filterReturnPeriod
import uk.gov.hmrc.vatsubscription.utils.{JsonObjectSugar, JsonReadUtil}

case class VatCustomerInformation(mandationStatus: MandationStatus,
                                  customerDetails: CustomerDetails,
                                  flatRateScheme: Option[FlatRateScheme],
                                  ppob: PPOBGet,
                                  bankDetails: Option[BankDetails],
                                  returnPeriod: Option[ReturnPeriod],
                                  deregistration: Option[Deregistration],
                                  changeIndicators: Option[ChangeIndicators],
                                  pendingChanges: Option[PendingChanges],
                                  partyType: Option[PartyType] = None) {

  val pendingPPOBAddress: Option[PPOBAddressGet] = pendingChanges.flatMap(_.ppob.map(_.address))
  val pendingBankDetails: Option[BankDetails] = pendingChanges.flatMap(_.bankDetails)
  val pendingContactEmail: Option[String] = pendingChanges.flatMap(_.ppob.flatMap(_.contactDetails.flatMap(_.emailAddress)))

}

object VatCustomerInformation extends JsonReadUtil with JsonObjectSugar {

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
  val overseasIndicatorKey = "overseasIndicator"
  val ppobKey = "PPOB"
  val bankDetailsKey = "bankDetails"
  val returnPeriodKey = "returnPeriod"
  val vatRegistrationDateKey = "effectiveRegistrationDate"
  val customerMigratedToETMPDateKey = "customerMigratedToETMPDate"
  val deregistrationKey = "deregistration"
  val partyTypeKey = "partyType"

  private val path = __ \ approvedInformationKey
  private val customerDetailsPath = path \ customerDetailsKey
  private val flatRateSchemePath = path \ flatRateSchemeKey
  private val overseasIndicatorPath = path \ customerDetailsKey \ overseasIndicatorKey
  private val ppobPath = path \ ppobKey
  private val bankDetailsPath = path \ bankDetailsKey
  private val returnPeriodPath = path \ returnPeriodKey
  private val deregistrationPath = path \ deregistrationKey

  private val changeIndicatorsPath = __ \ pendingChangesKey \ changeIndicators
  private val pendingChangesPath = __ \ pendingChangesKey \ changes

  val release8Reads: Reads[VatCustomerInformation] = for {
    firstName <- (customerDetailsPath \ individualKey \ firstNameKey).readOpt[String]
    lastName <- (customerDetailsPath \ individualKey \ lastNameKey).readOpt[String]
    organisationName <- (customerDetailsPath \ organisationNameKey).readOpt[String]
    tradingName <- (customerDetailsPath \ tradingNameKey).readOpt[String]
    vatRegistrationDate <- (customerDetailsPath \ vatRegistrationDateKey).readOpt[String]
    customerMigratedToETMPDate <- (customerDetailsPath \ customerMigratedToETMPDateKey).readOpt[String]
    mandationStatus <- (customerDetailsPath \ mandationStatusKey).read[MandationStatus]
    welshIndicator <- (customerDetailsPath \ welshIndicatorKey).readOpt[Boolean]
    isPartialMigration <- (customerDetailsPath \ isPartialMigrationKey).readOpt[Boolean]
    flatRateScheme <- flatRateSchemePath.readOpt[FlatRateScheme]
    overseasIndicator <- overseasIndicatorPath.read[Boolean]
    ppob <- ppobPath.read[PPOBGet]
    bankDetails <- bankDetailsPath.readOpt[BankDetails]
    returnPeriod <- returnPeriodPath.readOpt[ReturnPeriod](ReturnPeriod.currentDesReads)
    deregistration <- deregistrationPath.readOpt[Deregistration]
    changeIndicators <- changeIndicatorsPath.readOpt[ChangeIndicators]
    pendingChanges <- pendingChangesPath.readOpt[PendingChanges](PendingChanges.newReads)
    partyType <- (customerDetailsPath \ partyTypeKey).readOpt[PartyType](PartyType.r8reads)
  } yield VatCustomerInformation(
    mandationStatus,
    CustomerDetails(
      firstName = firstName,
      lastName = lastName,
      organisationName = organisationName,
      tradingName = tradingName,
      vatRegistrationDate,
      customerMigratedToETMPDate,
      flatRateScheme.isDefined,
      welshIndicator,
      isPartialMigration.contains(true),
      overseasIndicator
    ),
    flatRateScheme,
    ppob,
    bankDetails,
    filterReturnPeriod(returnPeriod),
    deregistration,
    changeIndicators,
    pendingChanges,
    partyType
  )

  implicit val writes: Writes[VatCustomerInformation] = Writes {
    model =>
      jsonObjNoNulls(
        "mandationStatus" -> model.mandationStatus.value,
        "customerDetails" -> model.customerDetails,
        "flatRateScheme" -> model.flatRateScheme,
        "ppob" -> model.ppob,
        "bankDetails" -> model.bankDetails,
        "returnPeriod" -> model.returnPeriod,
        "deregistration" -> model.deregistration,
        "changeIndicators" -> model.changeIndicators,
        "pendingChanges" -> model.pendingChanges,
        "partyType" -> model.partyType
      )
  }

  val manageAccountWrites: Writes[VatCustomerInformation] = Writes {
    model =>
      jsonObjNoNulls(
        "mandationStatus" -> model.mandationStatus.value,
        "overseasIndicator" -> model.customerDetails.overseasIndicator,
        "ppobAddress" -> model.pendingPPOBAddress.fold(model.ppob.address)(x => x),
        "contactEmail" -> model.pendingContactEmail.fold(model.ppob.contactDetails.flatMap(_.emailAddress))(x => Some(x)),
        "repaymentBankDetails" -> model.pendingBankDetails.fold(model.bankDetails)(x => Some(x)),
        "businessName" -> model.customerDetails.organisationName,
        "partyType" -> model.partyType
      )
  }
}
