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

case class VatCustomerInformation(mandationStatus: MandationStatus, customerDetails: CustomerDetails)


object VatCustomerInformation {

  val approvedInformationKey = "approvedInformation"
  val customerDetailsKey = "customerDetails"
  val individualKey = "individual"
  val firstNameKey = "firstName"
  val lastNameKey = "lastName"
  val organisationNameKey = "organisationName"
  val tradingNameKey = "tradingName"
  val mandationStatusKey = "mandationStatus"
  private val path = JsPath \ approvedInformationKey \ customerDetailsKey

  implicit class JsonReadUtil(jsPath: JsPath) {
    def readOpt[T](implicit reads: Reads[T]): Reads[Option[T]] = jsPath.readNullable[T].orElse(Reads.pure(None))
  }

  implicit val desReader: Reads[VatCustomerInformation] = for {
    firstName <- (path \ individualKey \ firstNameKey).readOpt[String]
    lastName <- (path \ individualKey \ lastNameKey).readOpt[String]
    organisationName <- (path \ organisationNameKey).readOpt[String]
    tradingName <- (path \ tradingNameKey).readOpt[String]
    mandationStatus <- (path \ mandationStatusKey).read[MandationStatus]
  } yield VatCustomerInformation(
    mandationStatus,
    CustomerDetails(firstName = firstName, lastName = lastName, organisationName = organisationName, tradingName = tradingName)
  )

}
