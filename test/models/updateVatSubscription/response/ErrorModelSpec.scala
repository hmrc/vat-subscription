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

package models.updateVatSubscription.response

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json

class ErrorModelSpec extends AnyWordSpecLike with Matchers {

  "ErrorModel Writes" should {

    val model: ErrorModel = ErrorModel("INVALID_PAYLOAD", "Submission has not passed validation. Invalid Payload.")

    "output a correctly formatted json object" in {
      val result = Json.obj(
        "status" -> "INVALID_PAYLOAD",
        "message" -> "Submission has not passed validation. Invalid Payload."
      )
      ErrorModel.writes.writes(model) shouldBe result
    }
  }
}
