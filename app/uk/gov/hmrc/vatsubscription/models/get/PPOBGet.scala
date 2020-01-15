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

package uk.gov.hmrc.vatsubscription.models.get

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.vatsubscription.models.ContactDetails

case class PPOBGet(address: PPOBAddressGet,
                   contactDetails: Option[ContactDetails],
                   websiteAddress: Option[String])

object PPOBGet {

  private val addressPath = __ \ "address"
  private val contactDetailsPath = __ \ "contactDetails"
  private val websiteAddressPath = __ \ "websiteAddress"

  implicit val reads: Reads[PPOBGet] = (
    addressPath.read[PPOBAddressGet] and
      contactDetailsPath.readNullable[ContactDetails] and
      websiteAddressPath.readNullable[String]
    )(PPOBGet.apply _)

  implicit val writes: Writes[PPOBGet] = (
    addressPath.write[PPOBAddressGet] and
    contactDetailsPath.writeNullable[ContactDetails] and
    websiteAddressPath.writeNullable[String]
  )(unlift(PPOBGet.unapply))

}