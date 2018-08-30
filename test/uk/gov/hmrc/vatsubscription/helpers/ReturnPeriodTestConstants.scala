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

package uk.gov.hmrc.vatsubscription.helpers

import play.api.libs.json.{JsValue, Json}

object ReturnPeriodTestConstants {

  val maReturnPeriod = "MA"
  val mbReturnPeriod = "MB"
  val mcReturnPeriod = "MC"
  val mmReturnPeriod = "MM"

  val returnPeriodMAJson: JsValue = Json.obj("stdReturnPeriod" -> maReturnPeriod)
  val returnPeriodMBJson: JsValue = Json.obj("stdReturnPeriod" -> mbReturnPeriod)
  val returnPeriodMCJson: JsValue = Json.obj("stdReturnPeriod" -> mcReturnPeriod)
  val returnPeriodMMJson: JsValue = Json.obj("stdReturnPeriod" -> mmReturnPeriod)

  val inflightReturnPeriodMAJson: JsValue = Json.obj("returnPeriod" -> maReturnPeriod)
  val inflightReturnPeriodMBJson: JsValue = Json.obj("returnPeriod" -> mbReturnPeriod)
  val inflightReturnPeriodMCJson: JsValue = Json.obj("returnPeriod" -> mcReturnPeriod)
  val inflightReturnPeriodMMJson: JsValue = Json.obj("returnPeriod" -> mmReturnPeriod)

}
