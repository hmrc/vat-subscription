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

package helpers

import BaseTestConstants._
import play.api.libs.json.{JsObject, JsValue, Json}
import models.CustomerDetails

object CustomerDetailsTestConstants {

  val customerDetailsModelMin: CustomerDetails = CustomerDetails(
    None,
    None,
    None,
    None,
    None,
    welshIndicator = None,
    isPartialMigration = false,
    customerMigratedToETMPDate = None,
    overseasIndicator = false
  )

  val customerDetailsModelMax: CustomerDetails = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName),
    Some(effectiveDate),
    welshIndicator = Some(false),
    isPartialMigration = false,
    customerMigratedToETMPDate = Some("2019-01-01"),
    overseasIndicator = false
  )

  val customerDetailsModelMaxWithTrueOverseas: CustomerDetails = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName),
    Some(effectiveDate),
    welshIndicator = Some(false),
    isPartialMigration = false,
    customerMigratedToETMPDate = Some("2019-01-01"),
    overseasIndicator = true
  )

  val customerDetailsModelMaxWithFRS: CustomerDetails = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName),
    Some(effectiveDate),
    hasFlatRateScheme = true,
    welshIndicator = Some(true),
    isPartialMigration = false,
    customerMigratedToETMPDate = Some("2019-01-01"),
    overseasIndicator = false
  )

  val customerDetailsModelNoWelshIndicator: CustomerDetails = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName),
    Some(effectiveDate),
    hasFlatRateScheme = true,
    welshIndicator = None,
    isPartialMigration = false,
    customerMigratedToETMPDate = None,
    overseasIndicator = false
  )

  val customerDetailsJsonMaxWithFRS: JsValue = Json.obj(
    "firstName" -> firstName,
    "lastName" -> lastName,
    "organisationName" -> orgName,
    "tradingName" -> tradingName,
    "vatRegistrationDate" -> effectiveDate,
    "hasFlatRateScheme" -> true,
    "welshIndicator" -> true,
    "isPartialMigration" -> false,
    "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
    "overseasIndicator" -> false
  )

  val customerDetailsJsonNoWelshIndicator: JsValue = Json.obj(
    "firstName" -> firstName,
    "lastName" -> lastName,
    "organisationName" -> orgName,
    "tradingName" -> tradingName,
    "vatRegistrationDate" -> effectiveDate,
    "hasFlatRateScheme" -> true,
    "isPartialMigration" -> false,
    "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
    "overseasIndicator" -> false
  )

  val customerDetailsJsonMax: JsValue = Json.obj(
    "firstName" -> firstName,
    "lastName" -> lastName,
    "organisationName" -> orgName,
    "tradingName" -> tradingName,
    "vatRegistrationDate" -> effectiveDate,
    "hasFlatRateScheme" -> false,
    "welshIndicator" -> false,
    "isPartialMigration" -> false,
    "customerMigratedToETMPDate" -> customerMigratedToETMPDate,
    "overseasIndicator" -> false
  )

  val customerDetailsJsonMin: JsObject = Json.obj(
    "hasFlatRateScheme" -> false,
    "isPartialMigration" -> false,
    "overseasIndicator" -> false
  )
}