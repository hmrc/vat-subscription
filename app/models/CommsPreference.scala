/*
 * Copyright 2023 HM Revenue & Customs
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

sealed trait CommsPreference {
  val desValue: String
  val mdtpValue: String
}

case object PaperPreference extends CommsPreference {
  override val desValue: String = "P01"
  override val mdtpValue: String = "PAPER"
}

case object DigitalPreference extends CommsPreference {
  override val desValue: String = "ZEL"
  override val mdtpValue: String = "DIGITAL"
}

object CommsPreference {

  implicit val reads: Reads[CommsPreference] = JsPath.read[String].collect(JsonValidationError("Invalid contact preference")) {
    case PaperPreference.desValue | PaperPreference.mdtpValue => PaperPreference
    case DigitalPreference.desValue | DigitalPreference.mdtpValue => DigitalPreference
  }

  val writes: Writes[CommsPreference] = Writes[CommsPreference] { model =>
    JsString(model.mdtpValue)
  }

  val updatePreferenceWrites: Writes[CommsPreference] = Writes[CommsPreference] { model =>
    JsString(model.desValue)
  }
}
