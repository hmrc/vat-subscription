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

package uk.gov.hmrc.vatsubscription.models.post

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.vatsubscription.models.CommsPreference
import uk.gov.hmrc.vatsubscription.models.CommsPreference.updatePreferenceReads

case class CommsPreferencePost(commsPreference: CommsPreference,
                               transactorOrCapacitorEmail: Option[String])
object CommsPreferencePost {
  implicit val reads: Reads[CommsPreferencePost] = (
    __.read[CommsPreference](updatePreferenceReads) and
      (__ \ "transactorOrCapacitorEmail").readNullable[String]
    )(CommsPreferencePost.apply _)
}
