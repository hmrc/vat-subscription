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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CustomerDetails(firstName: Option[String],
                           lastName: Option[String],
                           organisationName: Option[String],
                           tradingName: Option[String],
                           vatRegistrationDate: Option[String],
                           hasFlatRateScheme: Boolean = false,
                           welshIndicator: Option[Boolean],
                           isPartialMigration: Boolean)

object CustomerDetails {

  private val firstNamePath = JsPath \ "firstName"
  private val lastNamePath =  JsPath \ "lastName"
  private val organisationNamePath = JsPath \ "organisationName"
  private val tradingNamePath = JsPath \ "tradingName"
  private val vatRegistrationDatePath = JsPath \ "vatRegistrationDate"
  private val hasFlatRateSchemePath = JsPath \ "hasFlatRateScheme"
  private val welshIndicatorPath = JsPath \ "welshIndicator"
  private val isPartialMigrationPath = JsPath \ "isPartialMigration"

  implicit val cdReader: Reads[CustomerDetails] = for {
    firstName <- firstNamePath.readNullable[String]
    lastName <- lastNamePath.readNullable[String]
    organisationName <- organisationNamePath.readNullable[String]
    tradingName <- tradingNamePath.readNullable[String]
    vatRegistrationDate <- vatRegistrationDatePath.readNullable[String]
    hasFlatRateScheme <- hasFlatRateSchemePath.read[Boolean]
    welshIndicator <- welshIndicatorPath.readNullable[Boolean]
    isPartialMigration <- isPartialMigrationPath.readNullable[Boolean]
  } yield CustomerDetails(
    firstName,
    lastName,
    organisationName,
    tradingName,
    vatRegistrationDate,
    hasFlatRateScheme,
    welshIndicator,
    isPartialMigration.contains(true)
  )

  implicit val cdWriter: Writes[CustomerDetails] = (
    firstNamePath.writeNullable[String] and
      lastNamePath.writeNullable[String] and
      organisationNamePath.writeNullable[String] and
      tradingNamePath.writeNullable[String] and
      vatRegistrationDatePath.writeNullable[String] and
      hasFlatRateSchemePath.write[Boolean] and
      welshIndicatorPath.writeNullable[Boolean] and
      isPartialMigrationPath.write[Boolean]
    )(unlift(CustomerDetails.unapply))
}

