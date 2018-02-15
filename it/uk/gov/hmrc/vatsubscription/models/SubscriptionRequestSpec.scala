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

import java.util.UUID

import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.play.test.UnitSpec

class SubscriptionRequestSpec extends UnitSpec {
  "mongoFormat" should {
    val testCredentialId = UUID.randomUUID().toString
    val testVrn = UUID.randomUUID().toString

    "convert a SubscriptionRequest into a correctly formatted json model" in {
      val model = SubscriptionRequest(testCredentialId, Some(testVrn))
      val expectedModel = Json.parse(s"""{"_id":"$testCredentialId","vrn":"$testVrn"}""")

      SubscriptionRequest.mongoFormat.writes(model) shouldBe expectedModel
    }

    "convert a correctly formatted json model into a SubscriptionRequest" in {
      val jsonModel = Json.parse(s"""{"_id":"$testCredentialId","vrn":"$testVrn"}""")
      val expectedModel = SubscriptionRequest(testCredentialId, Some(testVrn))

      SubscriptionRequest.mongoFormat.reads(jsonModel) shouldBe JsSuccess(expectedModel)
    }
  }
}
