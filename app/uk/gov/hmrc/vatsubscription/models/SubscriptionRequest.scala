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

import java.time.Instant

import play.api.libs.json.{Json, OFormat}

case class SubscriptionRequest(vatNumber: String,
                               companyNumber: Option[String] = None,
                               nino: Option[String] = None,
                               email: Option[String] = None,
                               identityVerified: Boolean = false)

object SubscriptionRequest {

  val vatNumberKey = "vatNumber"
  val postCodeKey = "postCode"
  val registrationDateKey = "registrationDate"
  val idKey = "_id"
  val companyNumberKey = "companyNumber"
  val ninoKey = "nino"
  val emailKey = "email"
  val identityVerifiedKey = "identityVerified"
  val creationTimestampKey = "creationTimestamp"

  val mongoFormat: OFormat[SubscriptionRequest] = OFormat(
    json =>
      for {
        vatNumber <- (json \ idKey).validate[String]
        companyNumber <- (json \ companyNumberKey).validateOpt[String]
        nino <- (json \ ninoKey).validateOpt[String]
        email <- (json \ emailKey).validateOpt[String]
        identityVerified <- (json \ identityVerifiedKey).validate[Boolean]
      } yield SubscriptionRequest(vatNumber, companyNumber, nino, email, identityVerified),
    subscriptionRequest =>
      Json.obj(
        idKey -> subscriptionRequest.vatNumber,
        companyNumberKey -> subscriptionRequest.companyNumber,
        ninoKey -> subscriptionRequest.nino,
        emailKey -> subscriptionRequest.email,
        identityVerifiedKey -> subscriptionRequest.identityVerified,
        creationTimestampKey -> Json.obj("$date" -> Instant.now.toEpochMilli)
      )
  )

}
