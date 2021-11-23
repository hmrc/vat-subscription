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

package models

import play.api.libs.json._

sealed trait MandationStatus {
  def desValue: String
  def value: String
}

case object MTDfBMandated extends MandationStatus {
  override val desValue: String = "1"
  override val value: String = "MTDfB Mandated"
}

case object MTDfBVoluntary extends MandationStatus {
  override val desValue: String = "2"
  override val value: String = "MTDfB Voluntary"
}

case object NonMTDfB extends MandationStatus {
  override val desValue: String = "3"
  override val value: String = "Non MTDfB"
}

case object NonDigital extends MandationStatus {
  override val desValue: String = "4"
  override val value: String = "Non Digital"
}

case object MTDfBExempt extends MandationStatus {
  override val desValue: String = "1"
  override val value: String = "MTDfB Exempt"
}

case object MTDfB extends MandationStatus {
  override val desValue: String = "2"
  override val value: String = "MTDfB"
}


object MandationStatus {

  val desReader: Reads[MandationStatus] = JsPath.read[String].map {
    case MTDfBExempt.`desValue` => MTDfBExempt
    case MTDfB.`desValue` => MTDfB
    case NonMTDfB.`desValue` => NonMTDfB
    case NonDigital.`desValue` => NonDigital
  }

  val writer: Writes[MandationStatus] = Writes(
    status => JsString(status.value)
  )

  val desWriter: Writes[MandationStatus] = Writes(
    status => JsString(status.desValue)
  )
}
