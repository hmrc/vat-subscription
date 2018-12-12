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

package uk.gov.hmrc.vatsubscription.models.post

import play.api.libs.json._
import uk.gov.hmrc.vatsubscription.models.ContactDetails

case class PPOBPost(address: PPOBAddressPost,
                    contactDetails: Option[ContactDetails],
                    websiteAddress: Option[String],
                    transactorOrCapacitorEmail: Option[String])

object PPOBPost {

  implicit val reads: Reads[PPOBPost] = Json.reads[PPOBPost]

  implicit val writes: Writes[PPOBPost] = Writes {
    model => Json.obj(
      "PPOBAddress" -> model.address,
      "PPOBCommDetails" -> model.contactDetails,
      "websiteAddress" -> model.websiteAddress
    )
  }
}
