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

package models.updateVatSubscription.request

import play.api.libs.json.Json
import config.featureSwitch.{Api1365Latest, Api1365PreRelease}
import helpers.TestUtil

class RequestedChangesSpec extends TestUtil {

  "RequestedChanges Writes" when {

    "the API1365 version is Latest" should {

      val model: RequestedChanges = RequestedChanges(
        ppobDetails = true,
        returnPeriod = true,
        deregInfo = true
      )

      "output a correctly formatted RequestedChanges json for the latest DES API1365 writes" in {
        val result = Json.obj(
          "PPOBDetails" -> true,
          "returnPeriod" -> true,
          "deregInfo" -> true,
          "repaymentBankDetails" -> false,
          "businessActivities" -> false,
          "flatRateScheme" -> false,
          "organisationDetails" -> false,
          "correspDetails" -> false,
          "mandationStatus" -> false,
          "commsPreference" -> false
        )

        mockAppConfig.features.api1365Version(Api1365Latest)
        RequestedChanges.DESApi1365Writes(mockAppConfig).writes(model) shouldBe result
      }
    }

    "the API1365 version is Pre-release" should {

      val model: RequestedChanges = RequestedChanges(
        correspDetails = true,
        mandationStatus = true,
        deregInfo = true
      )

      "output correctly formatted RequestedChanges json which omits the commsPreference field" in {
        val result = Json.obj(
          "PPOBDetails" -> false,
          "returnPeriod" -> false,
          "deregInfo" -> true,
          "repaymentBankDetails" -> false,
          "businessActivities" -> false,
          "flatRateScheme" -> false,
          "organisationDetails" -> false,
          "correspDetails" -> true,
          "mandationStatus" -> true
        )

        mockAppConfig.features.api1365Version(Api1365PreRelease)
        RequestedChanges.DESApi1365Writes(mockAppConfig).writes(model) shouldBe result
      }
    }
  }
}
