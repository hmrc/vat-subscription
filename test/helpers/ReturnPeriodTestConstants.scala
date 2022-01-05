/*
 * Copyright 2022 HM Revenue & Customs
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

import models._
import play.api.libs.json.{JsValue, Json}

object ReturnPeriodTestConstants {

  val maReturnPeriod = "MA"
  val mbReturnPeriod = "MB"
  val mcReturnPeriod = "MC"
  val mmReturnPeriod = "MM"
  val yaReturnPeriod = "YA"
  val ybReturnPeriod = "YB"
  val ycReturnPeriod = "YC"
  val ydReturnPeriod = "YD"
  val yeReturnPeriod = "YE"
  val yfReturnPeriod = "YF"
  val ygReturnPeriod = "YG"
  val yhReturnPeriod = "YH"
  val yiReturnPeriod = "YI"
  val yjReturnPeriod = "YJ"
  val ykReturnPeriod = "YK"
  val ylReturnPeriod = "YL"

  val returnPeriodMAJson: JsValue = Json.obj("stdReturnPeriod" -> maReturnPeriod)
  val returnPeriodMBJson: JsValue = Json.obj("stdReturnPeriod" -> mbReturnPeriod)
  val returnPeriodMCJson: JsValue = Json.obj("stdReturnPeriod" -> mcReturnPeriod)
  val returnPeriodMMJson: JsValue = Json.obj("stdReturnPeriod" -> mmReturnPeriod)
  val returnPeriodYAJson: JsValue = Json.obj("stdReturnPeriod" -> yaReturnPeriod)
  val returnPeriodYBJson: JsValue = Json.obj("stdReturnPeriod" -> ybReturnPeriod)
  val returnPeriodYCJson: JsValue = Json.obj("stdReturnPeriod" -> ycReturnPeriod)
  val returnPeriodYDJson: JsValue = Json.obj("stdReturnPeriod" -> ydReturnPeriod)
  val returnPeriodYEJson: JsValue = Json.obj("stdReturnPeriod" -> yeReturnPeriod)
  val returnPeriodYFJson: JsValue = Json.obj("stdReturnPeriod" -> yfReturnPeriod)
  val returnPeriodYGJson: JsValue = Json.obj("stdReturnPeriod" -> ygReturnPeriod)
  val returnPeriodYHJson: JsValue = Json.obj("stdReturnPeriod" -> yhReturnPeriod)
  val returnPeriodYIJson: JsValue = Json.obj("stdReturnPeriod" -> yiReturnPeriod)
  val returnPeriodYJJson: JsValue = Json.obj("stdReturnPeriod" -> yjReturnPeriod)
  val returnPeriodYKJson: JsValue = Json.obj("stdReturnPeriod" -> ykReturnPeriod)
  val returnPeriodYLJson: JsValue = Json.obj("stdReturnPeriod" -> ylReturnPeriod)
  val invalidReturnPeriod: JsValue = Json.obj("stdReturnPeriod" -> "invalid")

  val inflightReturnPeriodMCRelease6Json: JsValue = Json.obj("stdReturnPeriod" -> mcReturnPeriod)

  val inflightReturnPeriodMAJson: JsValue = Json.obj("returnPeriod" -> maReturnPeriod)
  val inflightReturnPeriodMBJson: JsValue = Json.obj("returnPeriod" -> mbReturnPeriod)
  val inflightReturnPeriodMCJson: JsValue = Json.obj("returnPeriod" -> mcReturnPeriod)
  val inflightReturnPeriodMMJson: JsValue = Json.obj("returnPeriod" -> mmReturnPeriod)
  val inflightReturnPeriodYAJson: JsValue = Json.obj("returnPeriod" -> yaReturnPeriod)
  val inflightReturnPeriodYBJson: JsValue = Json.obj("returnPeriod" -> ybReturnPeriod)
  val inflightReturnPeriodYCJson: JsValue = Json.obj("returnPeriod" -> ycReturnPeriod)
  val inflightReturnPeriodYDJson: JsValue = Json.obj("returnPeriod" -> ydReturnPeriod)
  val inflightReturnPeriodYEJson: JsValue = Json.obj("returnPeriod" -> yeReturnPeriod)
  val inflightReturnPeriodYFJson: JsValue = Json.obj("returnPeriod" -> yfReturnPeriod)
  val inflightReturnPeriodYGJson: JsValue = Json.obj("returnPeriod" -> ygReturnPeriod)
  val inflightReturnPeriodYHJson: JsValue = Json.obj("returnPeriod" -> yhReturnPeriod)
  val inflightReturnPeriodYIJson: JsValue = Json.obj("returnPeriod" -> yiReturnPeriod)
  val inflightReturnPeriodYJJson: JsValue = Json.obj("returnPeriod" -> yjReturnPeriod)
  val inflightReturnPeriodYKJson: JsValue = Json.obj("returnPeriod" -> ykReturnPeriod)
  val inflightReturnPeriodYLJson: JsValue = Json.obj("returnPeriod" -> ylReturnPeriod)
  val invalidInflightReturnPeriod: JsValue = Json.obj("returnPeriod" -> "invalid")

  case class PeriodWithJson(period: ReturnPeriod, json: JsValue)

  def periods: Set[PeriodWithJson] = Set(
    PeriodWithJson(MAReturnPeriod(None, None, None), returnPeriodMAJson),
    PeriodWithJson(MBReturnPeriod(None, None, None), returnPeriodMBJson),
    PeriodWithJson(MCReturnPeriod(None, None, None), returnPeriodMCJson),
    PeriodWithJson(MMReturnPeriod(None, None, None), returnPeriodMMJson),
    PeriodWithJson(YAReturnPeriod(None, None, None), returnPeriodYAJson),
    PeriodWithJson(YBReturnPeriod(None, None, None), returnPeriodYBJson),
    PeriodWithJson(YCReturnPeriod(None, None, None), returnPeriodYCJson),
    PeriodWithJson(YDReturnPeriod(None, None, None), returnPeriodYDJson),
    PeriodWithJson(YEReturnPeriod(None, None, None), returnPeriodYEJson),
    PeriodWithJson(YFReturnPeriod(None, None, None), returnPeriodYFJson),
    PeriodWithJson(YGReturnPeriod(None, None, None), returnPeriodYGJson),
    PeriodWithJson(YHReturnPeriod(None, None, None), returnPeriodYHJson),
    PeriodWithJson(YIReturnPeriod(None, None, None), returnPeriodYIJson),
    PeriodWithJson(YJReturnPeriod(None, None, None), returnPeriodYJJson),
    PeriodWithJson(YKReturnPeriod(None, None, None), returnPeriodYKJson),
    PeriodWithJson(YLReturnPeriod(None, None, None), returnPeriodYLJson)
  )

}
