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

package models

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}
import utils.JsonObjectSugar

case class TaxPeriod(periodStart: LocalDate,
                     periodEnd: LocalDate)

object TaxPeriod extends JsonObjectSugar {

  implicit val reads: Reads[TaxPeriod] = (
    ((JsPath \ "periodStartDate").read[LocalDate] or (JsPath \ "periodStartDateOfFirstStandardPeriod").read[LocalDate]) and
    ((JsPath \ "periodEndDate").read[LocalDate] or (JsPath \ "periodEndDateOfFirstStandardPeriod").read[LocalDate])
  )(TaxPeriod.apply _)

  implicit val writes: Writes[TaxPeriod] = Writes (
    taxPeriod => jsonObjNoNulls(
      "periodStart" -> taxPeriod.periodStart,
      "periodEnd" -> taxPeriod.periodEnd
    )
  )
}
