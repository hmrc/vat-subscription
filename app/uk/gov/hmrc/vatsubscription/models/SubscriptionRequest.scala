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

import play.api.libs.json.{JsPath, Json, OFormat}

case class SubscriptionRequest(credentialId: String, vrn: Option[String])

object SubscriptionRequest {
  val credentialIdKey = "_id"
  val vrnKey = "vrn"

  val mongoFormat: OFormat[SubscriptionRequest] = OFormat(
    json =>
      for {
        credentialId <- (json \ "_id").validate[String]
        vrn <- (json \ "vrn").validateOpt[String]
      } yield SubscriptionRequest(credentialId, vrn),
    subscriptionRequest =>
      Json.obj(
        credentialIdKey -> subscriptionRequest.credentialId,
        vrnKey -> subscriptionRequest.vrn
      )
  )

}
