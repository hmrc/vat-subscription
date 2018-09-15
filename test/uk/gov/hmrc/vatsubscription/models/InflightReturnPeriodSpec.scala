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

package uk.gov.hmrc.vatsubscription.models

import assets.TestUtil
import play.api.libs.json.Json

class InflightReturnPeriodSpec extends TestUtil {

  "InFlightReturnPeriod Reads" when {

    "latestApi1363Version feature switch is on" should {

      val json = Json.obj(
        "returnPeriod" -> "MA"
      )

      val model: InflightReturnPeriod = MAInflightReturnPeriod

      "output a correctly formatted UpdatedReturnPeriod json" in {
        mockAppConfig.features.latestApi1363Version(true)
        InflightReturnPeriod.inflightReturnPeriodReader(mockAppConfig).reads(json).get shouldEqual model
      }
    }

    "latestApi1363Version feature switch is off" should {


      val json = Json.obj(
        "stdReturnPeriod" -> "MA"
      )

      val model: InflightReturnPeriod = MAInflightReturnPeriod

      "output a correctly formatted UpdatedReturnPeriod json" in {
        mockAppConfig.features.latestApi1363Version(false)
        InflightReturnPeriod.inflightReturnPeriodReader(mockAppConfig).reads(json).get shouldEqual model
      }
    }
  }
}
