/*
 * Copyright 2024 HM Revenue & Customs
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

package models.updateVatSubscription.request.deregistration

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, Writes, _}

case class TurnoverBelowThreshold(belowThreshold: BelowThreshold,
                                  nextTwelveMonthsTurnover: BigDecimal,
                                  whyTurnoverBelow: WhyTurnoverBelow)

object TurnoverBelowThreshold {

  implicit val frontendReads: Reads[TurnoverBelowThreshold] = (
    (__ \ "belowThreshold").read[BelowThreshold] and
      (__ \ "nextTwelveMonthsTurnover").read[BigDecimal] and
      (__ \ "whyTurnoverBelow").readNullable[WhyTurnoverBelow].map {
        case Some(reasons) => reasons
        case None => TurnoverAlreadyBelow
      }
    )(TurnoverBelowThreshold.apply _)

  implicit val desWrites: Writes[TurnoverBelowThreshold] = (
    (__ \ "aboveBelowThreshold").write[BelowThreshold] and
      (__ \ "taxableSuppliesValue").write[BigDecimal] and
      (__ \ "reason").write[WhyTurnoverBelow]
    )(unlift(TurnoverBelowThreshold.unapply))

}

