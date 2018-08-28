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
import play.api.libs.json.{Reads, Writes, __}
import uk.gov.hmrc.vatsubscription.models.get.PPOBGet

case class PendingChanges(ppob: Option[PPOBGet],
                          bankDetails: Option[BankDetails])

object PendingChanges {

  private val ppobPath = __ \ "PPOBDetails"
  private val bankDetailsPath =  __ \ "bankDetails"

  implicit val reads: Reads[PendingChanges] = (
    ppobPath.readNullable[PPOBGet] and
      bankDetailsPath.readNullable[BankDetails]
    )(PendingChanges.apply _)

  implicit val writes: Writes[PendingChanges] = (
    ppobPath.writeNullable[PPOBGet] and
      bankDetailsPath.writeNullable[BankDetails]
    )(unlift(PendingChanges.unapply))
}
