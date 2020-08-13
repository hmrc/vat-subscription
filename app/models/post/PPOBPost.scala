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

package models.post

import play.api.libs.functional.syntax._
import play.api.libs.json._
import models.ContactDetails

case class PPOBPost(address: PPOBAddressPost,
                    contactDetails: Option[ContactDetails],
                    websiteAddress: Option[String],
                    transactorOrCapacitorEmail: Option[String])

object PPOBPost {

  private val addressPath = __ \ "PPOBAddress"
  private val contactDetailsPath = __ \ "PPOBCommDetails"
  private val websiteAddressPath = __ \ "websiteAddress"

  implicit val reads: Reads[PPOBPost] = Json.reads[PPOBPost]

  implicit val writes: Writes[PPOBPost] = (
    addressPath.write[PPOBAddressPost] and
    contactDetailsPath.writeNullable[ContactDetails] and
    websiteAddressPath.writeNullable[String] and
    OWrites[Any](_ => Json.obj())
  )(unlift(PPOBPost.unapply))
}
