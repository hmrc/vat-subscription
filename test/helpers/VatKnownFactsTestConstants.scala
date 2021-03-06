/*
 * Copyright 2021 HM Revenue & Customs
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

package helpers

import play.api.libs.json.{JsObject, Json}
import helpers.BaseTestConstants._
import helpers.PPOBTestConstants._
import services.VatKnownFactsRetrievalService.VatKnownFacts

object VatKnownFactsTestConstants {

  def vatKnownFacts(isOverseas: Boolean): VatKnownFacts = VatKnownFacts(effectiveDate, if (isOverseas) None else Some(postcode), isOverseas)

  val vatKnownFactsOverseasJson: JsObject = Json.obj(
    "vatRegistrationDate" -> effectiveDate,
    "isOverseas" -> true
  )

  val vatKnownFactsJson: JsObject = Json.obj(
    "vatRegistrationDate" -> effectiveDate,
    "businessPostCode" -> postcode,
    "isOverseas" -> false
  )

}
