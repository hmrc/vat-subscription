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

package models

import play.api.libs.json.{JsError, Json}
import helpers.BankDetailsTestConstants.bankDetailsModelMax
import helpers.BaseTestConstants.{orgName, tradingName}
import helpers.CustomerInformationTestConstants._
import helpers.PPOBTestConstants.ppobModelMax
import helpers.TestUtil

class PendingChangesSpec extends TestUtil {

  "PendingChanges" when {

    "all optional fields are populated" should {

      val model = PendingChanges(
        Some(ppobModelMax),
        Some(bankDetailsModelMax),
        Some(MCReturnPeriod(None, None, None)),
        Some(MTDfB),
        Some(DigitalPreference),
        Some(tradingName),
        Some(orgName)
      )

      "read from JSON" in {
        PendingChanges.reads(mockAppConfig).reads(inFlightChanges).get shouldBe model
      }

      "write to JSON" in {
        PendingChanges.writes.writes(model) shouldBe pendingChangesOutputMax
      }
    }

    "no optional fields are populated" should {

      val model = PendingChanges(None, None, None, None, None, None, None)

      "read from JSON" in {
        PendingChanges.reads(mockAppConfig).reads(Json.obj()).get shouldBe model
      }

      "write to JSON" in {
        PendingChanges.writes.writes(model) shouldBe Json.obj()
      }
    }

    "return period is populated but not valid" should {

      "return a JsError" in {
        PendingChanges.reads(mockAppConfig).reads(inFlightChangesInvalidReturnPeriod) shouldBe a[JsError]
      }
    }
  }
}
