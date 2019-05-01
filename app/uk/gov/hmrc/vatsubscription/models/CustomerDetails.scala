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
import uk.gov.hmrc.vatsubscription.utils.JsonObjectSugar

case class CustomerDetails(firstName: Option[String],
                           lastName: Option[String],
                           organisationName: Option[String],
                           tradingName: Option[String],
                           vatRegistrationDate: Option[String],
                           customerMigratedToETMPDate: Option[String],
                           hasFlatRateScheme: Boolean = false,
                           welshIndicator: Option[Boolean],
                           isPartialMigration: Boolean,
                           overseasIndicator: Boolean)

object CustomerDetails extends JsonObjectSugar {

  private val firstNamePath = JsPath \ "firstName"
  private val lastNamePath = JsPath \ "lastName"
  private val organisationNamePath = JsPath \ "organisationName"
  private val tradingNamePath = JsPath \ "tradingName"
  private val vatRegistrationDatePath = JsPath \ "vatRegistrationDate"
  private val customerMigratedToETMPDatePath = JsPath \ "customerMigratedToETMPDate"
  private val hasFlatRateSchemePath = JsPath \ "hasFlatRateScheme"
  private val welshIndicatorPath = JsPath \ "welshIndicator"
  private val isPartialMigrationPath = JsPath \ "isPartialMigration"
  private val overseasIndicatorPath = JsPath \ "overseasIndicator"

  implicit val cdReaderR8: Reads[CustomerDetails] = for {
    firstName <- firstNamePath.readNullable[String]
    lastName <- lastNamePath.readNullable[String]
    organisationName <- organisationNamePath.readNullable[String]
    tradingName <- tradingNamePath.readNullable[String]
    vatRegistrationDate <- vatRegistrationDatePath.readNullable[String]
    customerMigratedToETMPDate <- customerMigratedToETMPDatePath.readNullable[String]
    hasFlatRateScheme <- hasFlatRateSchemePath.read[Boolean]
    welshIndicator <- welshIndicatorPath.readNullable[Boolean]
    isPartialMigration <- isPartialMigrationPath.readNullable[Boolean]
  } yield CustomerDetails(
    firstName,
    lastName,
    organisationName,
    tradingName,
    vatRegistrationDate,
    customerMigratedToETMPDate,
    hasFlatRateScheme,
    welshIndicator,
    isPartialMigration.contains(true),
    overseasIndicator = false
  )

  implicit val cdReaderR10: Reads[CustomerDetails] = for {
    firstName <- firstNamePath.readNullable[String]
    lastName <- lastNamePath.readNullable[String]
    organisationName <- organisationNamePath.readNullable[String]
    tradingName <- tradingNamePath.readNullable[String]
    vatRegistrationDate <- vatRegistrationDatePath.readNullable[String]
    customerMigratedToETMPDate <- customerMigratedToETMPDatePath.readNullable[String]
    hasFlatRateScheme <- hasFlatRateSchemePath.read[Boolean]
    welshIndicator <- welshIndicatorPath.readNullable[Boolean]
    isPartialMigration <- isPartialMigrationPath.readNullable[Boolean]
    overseasIndicator <- overseasIndicatorPath.read[Boolean]
  } yield CustomerDetails(
    firstName,
    lastName,
    organisationName,
    tradingName,
    vatRegistrationDate,
    customerMigratedToETMPDate,
    hasFlatRateScheme,
    welshIndicator,
    isPartialMigration.contains(true),
    overseasIndicator
  )

  implicit val cdWriter: Boolean => Writes[CustomerDetails] = isRelease10 =>
    Writes { model =>
      jsonObjNoNulls(
        "firstName" -> model.firstName,
        "lastName" -> model.lastName,
        "organisationName" -> model.organisationName,
        "tradingName" -> model.tradingName,
        "vatRegistrationDate" -> model.vatRegistrationDate,
        "customerMigratedToETMPDate" -> model.customerMigratedToETMPDate,
        "hasFlatRateScheme" -> model.hasFlatRateScheme,
        "welshIndicator" -> model.welshIndicator,
        "isPartialMigration" -> model.isPartialMigration
      ) ++ (if(isRelease10) {
        Json.obj("overseasIndicator" -> model.overseasIndicator)
      } else Json.obj())
    }
}

