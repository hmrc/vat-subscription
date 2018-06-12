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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class RequestedChangesSpec extends UnitSpec {

  "RequestedChanges Writes" should {

    val model: RequestedChanges = RequestedChanges(
      addressDetails = true,
      returnPeriod = true,
      repaymentBankDetails = true
    )

    "output a correctly formatted RequestedChanges json" in {
      val result = Json.obj(
        "PPOBDetails" -> true,
        "returnPeriod" -> true,
        "repaymentBankDetails" -> true
      )

      RequestedChanges.writes.writes(model) shouldBe result
    }
  }
}
