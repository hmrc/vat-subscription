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

package helpers

import play.api.libs.json.{JsValue, Json}
import models.Deregistration

object DeregistrationTestConstants {

  val reason = Some("allergicToVAT")
  val cancellationDate = Some("2018-09-01")
  val lastReturnDate = Some("2018-08-31")

  val deregModel = Deregistration(reason, cancellationDate, lastReturnDate)

  val deregJson: JsValue = Json.obj(
    "deregistrationReason" -> reason,
    "effectDateOfCancellation" -> cancellationDate,
    "lastReturnDueDate" -> lastReturnDate
  )

}
