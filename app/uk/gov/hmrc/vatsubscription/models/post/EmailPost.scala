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

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.vatsubscription.models.ContactDetails

case class EmailPost(address: PPOBAddressPost,
                     contactDetails: ContactDetails,
                     websiteAddress: Option[String])

object EmailPost {

  private val addressPath = __ \ "PPOBAddress"
  private val contactDetailsPath = __ \ "PPOBCommDetails"
  private val websiteAddressPath = __ \ "websiteAddress"

  implicit val reads: Reads[EmailPost] = Json.reads[EmailPost]

  implicit val writes: Writes[EmailPost] = (
    addressPath.write[PPOBAddressPost] and
      contactDetailsPath.write[ContactDetails] and
      websiteAddressPath.writeNullable[String]
    )(unlift(EmailPost.unapply))

}
