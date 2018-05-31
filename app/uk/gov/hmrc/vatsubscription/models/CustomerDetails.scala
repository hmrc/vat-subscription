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
import uk.gov.hmrc.vatsubscription.utils.JsonReadUtil

case class CustomerDetails(firstName: Option[String], lastName: Option[String], organisationName: Option[String],
                           tradingName: Option[String], hasFlatRateScheme: Boolean = false)

object CustomerDetails extends JsonReadUtil {

  private val firstNamePath = JsPath \ "firstName"
  private val lastNamePath =  JsPath \ "lastName"
  private val organisationNamePath = JsPath \ "organisationName"
  private val tradingNamePath = JsPath \ "tradingName"
  private val hasFlatRateSchemePath = JsPath \ "hasFlatRateScheme"

  implicit val cdReader: Reads[CustomerDetails] = for {
    firstName <- firstNamePath.readOpt[String]
    lastName <- lastNamePath.readOpt[String]
    organisationName <- organisationNamePath.readOpt[String]
    tradingName <- tradingNamePath.readOpt[String]
    hasFlatRateScheme <- hasFlatRateSchemePath.read[Boolean]
  } yield CustomerDetails(firstName, lastName, organisationName, tradingName, hasFlatRateScheme)

  implicit val cdWriter: Writes[CustomerDetails] = (
    firstNamePath.writeNullable[String] and
      lastNamePath.writeNullable[String] and
      organisationNamePath.writeNullable[String] and
      tradingNamePath.writeNullable[String] and
      hasFlatRateSchemePath.write[Boolean]
    )(unlift(CustomerDetails.unapply))

  implicit val format: Format[CustomerDetails] = Format[CustomerDetails](
    cdReader,
    cdWriter
  )

}

