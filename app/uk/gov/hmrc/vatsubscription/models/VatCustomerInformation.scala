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
import uk.gov.hmrc.vatsubscription.utils.JsonReadUtil

case class VatCustomerInformation(mandationStatus: MandationStatus,
                                  customerDetails: CustomerDetails,
                                  flatRateScheme: Option[FlatRateScheme],
                                  ppob: Option[PPOB],
                                  bankDetails:Option[BankDetails],
                                  returnPeriod: Option[ReturnPeriod])


object VatCustomerInformation extends JsonReadUtil {

  val approvedInformationKey = "approvedInformation"
  val customerDetailsKey = "customerDetails"
  val individualKey = "individual"
  val firstNameKey = "firstName"
  val lastNameKey = "lastName"
  val organisationNameKey = "organisationName"
  val tradingNameKey = "tradingName"
  val mandationStatusKey = "mandationStatus"
  val flatRateSchemeKey = "flatRateScheme"
  val ppobKey = "PPOB"
  val bankDetailsKey = "bankDetails"
  val returnPeriodKey = "returnPeriod"

  private val path = __ \ approvedInformationKey
  private val customerDetailsPath = path \ customerDetailsKey
  private val flatRateSchemePath = path \ flatRateSchemeKey
  private val ppobPath = path \ ppobKey
  private val bankDetailsPath = path \ bankDetailsKey
  private val returnPeriodPath = path \ returnPeriodKey

  implicit val desReader: Reads[VatCustomerInformation] = for {
    firstName <- (customerDetailsPath \ individualKey \ firstNameKey).readOpt[String]
    lastName <- (customerDetailsPath \ individualKey \ lastNameKey).readOpt[String]
    organisationName <- (customerDetailsPath \ organisationNameKey).readOpt[String]
    tradingName <- (customerDetailsPath \ tradingNameKey).readOpt[String]
    mandationStatus <- (customerDetailsPath \ mandationStatusKey).read[MandationStatus]
    flatRateScheme <- flatRateSchemePath.readOpt[FlatRateScheme]
    ppob <- ppobPath.readOpt[PPOB]
    bankDetails <- bankDetailsPath.readOpt[BankDetails]
    returnPeriod <- returnPeriodPath.readOpt[ReturnPeriod]
  } yield VatCustomerInformation(
    mandationStatus,
    CustomerDetails(firstName = firstName, lastName = lastName, organisationName = organisationName, tradingName = tradingName, flatRateScheme.isDefined),
    flatRateScheme,
    ppob,
    bankDetails,
    returnPeriod
  )

  implicit val writes = Json.writes[VatCustomerInformation]

}
