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

package models.updateVatSubscription.request

import play.api.libs.json.Json
import models.MAReturnPeriod
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class UpdatedReturnPeriodSpec extends AnyWordSpecLike with Matchers {

  "UpdatedReturnPeriod Writes" should {

    val model: UpdatedReturnPeriod = UpdatedReturnPeriod(
      MAReturnPeriod(None, None, None))

    "output a correctly formatted UpdatedReturnPeriod json" in {
      val result = Json.obj(
        "changeReturnPeriod" -> true,
        "returnPeriod" -> "MA"
      )

      UpdatedReturnPeriod.writes.writes(model) shouldBe result
    }
  }
}
