/*
 * Copyright 2020 HM Revenue & Customs
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

import assets.TestUtil
import play.api.libs.json.{JsError, Json}
import helpers.BankDetailsTestConstants.bankDetailsModelMax
import helpers.BaseTestConstants.tradingName
import helpers.CustomerInformationTestConstants._
import helpers.PPOBTestConstants.ppobModelMax

class PendingChangesSpec extends TestUtil {

  "PendingChanges" when {

    "all optional fields are populated" when {

      "the newStatusIndicators feature is on" should {

        val model = PendingChanges(
          Some(ppobModelMax),
          Some(bankDetailsModelMax),
          Some(MAReturnPeriod(None, None, None)),
          Some(MTDfB),
          Some(DigitalPreference),
          Some(tradingName)
        )

        "parse the json correctly" in {
          PendingChanges.reads(mockAppConfig).reads(inFlightChanges).get shouldEqual model
        }
      }

      "the newStatusIndicators feature is off" should {

        val model = PendingChanges(
          Some(ppobModelMax),
          Some(bankDetailsModelMax),
          Some(MAReturnPeriod(None, None, None)),
          Some(MTDfBVoluntary),
          Some(DigitalPreference),
          Some(tradingName)
        )

        "parse the json correctly" in {
          mockAppConfig.features.newStatusIndicators(false)
          PendingChanges.reads(mockAppConfig).reads(inFlightChanges).get shouldEqual model
        }
      }
    }

    "no optional fields are populated" should {

      "parse the json correctly" in {
        PendingChanges.reads(mockAppConfig).reads(Json.obj()).get shouldEqual PendingChanges(None, None, None, None, None, None)
      }
    }

    "return period is populated but not valid" should {

      "return a JsError" in {
        PendingChanges.reads(mockAppConfig).reads(inFlightChangesInvalidReturnPeriod) shouldBe a[JsError]
      }
    }
  }
}
