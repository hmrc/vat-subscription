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
import utils.JsonObjectSugar

case class CustomerDetails(title: Option[String],
                           firstName: Option[String],
                           middleName: Option[String],
                           lastName: Option[String],
                           organisationName: Option[String],
                           tradingName: Option[String],
                           vatRegistrationDate: Option[String],
                           customerMigratedToETMPDate: Option[String],
                           hybridToFullMigrationDate: Option[String],
                           hasFlatRateScheme: Boolean = false,
                           welshIndicator: Option[Boolean],
                           isPartialMigration: Boolean,
                           isInsolvent: Option[Boolean],
                           insolvencyType: Option[String],
                           insolvencyDate: Option[String],
                           continueToTrade: Option[Boolean],
                           overseasIndicator: Boolean,
                           nameIsReadOnly: Option[Boolean])

object CustomerDetails extends JsonObjectSugar {

  private val titlePath = JsPath \ "title"
  private val firstNamePath = JsPath \ "firstName"
  private val middleNamePath = JsPath \ "middleName"
  private val lastNamePath = JsPath \ "lastName"
  private val organisationNamePath = JsPath \ "organisationName"
  private val tradingNamePath = JsPath \ "tradingName"
  private val vatRegistrationDatePath = JsPath \ "vatRegistrationDate"
  private val customerMigratedToETMPDatePath = JsPath \ "customerMigratedToETMPDate"
  private val hybridToFullMigrationDate = JsPath \ "hybridToFullMigrationDate"
  private val hasFlatRateSchemePath = JsPath \ "hasFlatRateScheme"
  private val welshIndicatorPath = JsPath \ "welshIndicator"
  private val isPartialMigrationPath = JsPath \ "isPartialMigration"
  private val isInsolventPath = JsPath \ "isInsolvent"
  private val insolvencyTypePath = JsPath \ "insolvencyType"
  private val insolvencyDatePath = JsPath \ "insolvencyDate"
  private val continueToTradePath = JsPath \ "continueToTrade"
  private val overseasIndicatorPath = JsPath \ "overseasIndicator"
  private val nameIsReadOnlyPath = JsPath \ "nameIsReadOnly"

  implicit val cdReader: Reads[CustomerDetails] = for {
    title <- titlePath.readNullable[String]
    firstName <- firstNamePath.readNullable[String]
    middleName <- middleNamePath.readNullable[String]
    lastName <- lastNamePath.readNullable[String]
    organisationName <- organisationNamePath.readNullable[String]
    tradingName <- tradingNamePath.readNullable[String]
    vatRegistrationDate <- vatRegistrationDatePath.readNullable[String]
    customerMigratedToETMPDate <- customerMigratedToETMPDatePath.readNullable[String]
    hybridToFullMigrationDate <- hybridToFullMigrationDate.readNullable[String]
    hasFlatRateScheme <- hasFlatRateSchemePath.read[Boolean]
    welshIndicator <- welshIndicatorPath.readNullable[Boolean]
    isPartialMigration <- isPartialMigrationPath.readNullable[Boolean]
    isInsolvent <- isInsolventPath.readNullable[Boolean]
    insolvencyType <- insolvencyTypePath.readNullable[String]
    insolvencyDate <- insolvencyDatePath.readNullable[String]
    continueToTrade <- continueToTradePath.readNullable[Boolean]
    overseasIndicator <- overseasIndicatorPath.read[Boolean]
    nameIsReadOnly <- nameIsReadOnlyPath.readNullable[Boolean]
  } yield CustomerDetails(
    title,
    firstName,
    middleName,
    lastName,
    organisationName,
    tradingName,
    vatRegistrationDate,
    customerMigratedToETMPDate,
    hybridToFullMigrationDate,
    hasFlatRateScheme,
    welshIndicator,
    isPartialMigration.contains(true),
    isInsolvent,
    insolvencyType,
    insolvencyDate,
    continueToTrade,
    overseasIndicator,
    nameIsReadOnly
  )

  implicit val cdWriter: Writes[CustomerDetails] = Writes { model =>
    jsonObjNoNulls(
        "title" -> model.title,
        "firstName" -> model.firstName,
        "middleName" -> model.middleName,
        "lastName" -> model.lastName,
        "organisationName" -> model.organisationName,
        "tradingName" -> model.tradingName,
        "vatRegistrationDate" -> model.vatRegistrationDate,
        "customerMigratedToETMPDate" -> model.customerMigratedToETMPDate,
        "hybridToFullMigrationDate" -> model.hybridToFullMigrationDate,
        "hasFlatRateScheme" -> model.hasFlatRateScheme,
        "welshIndicator" -> model.welshIndicator,
        "isPartialMigration" -> model.isPartialMigration,
        "isInsolvent" -> model.isInsolvent,
        "insolvencyType" -> model.insolvencyType,
        "insolvencyDate" -> model.insolvencyDate,
        "continueToTrade" -> model.continueToTrade,
        "overseasIndicator" -> model.overseasIndicator,
        "nameIsReadOnly" -> model.nameIsReadOnly
      )
    }
}

