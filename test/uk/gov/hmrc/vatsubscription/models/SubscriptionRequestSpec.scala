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

import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest._

class SubscriptionRequestSpec extends UnitSpec {
  "mongoFormat" should {
    val testJson = Json.obj(
      internalIdKey -> testInternalId,
      vatNumberKey -> testVatNumber,
      companyNumberKey -> testCompanyNumber
    )

    val testModel = SubscriptionRequest(
      internalId = testInternalId,
      vatNumber = Some(testVatNumber),
      companyNumber = Some(testCompanyNumber)
    )

    "convert a SubscriptionRequest into a correctly formatted json model" in {
      SubscriptionRequest.mongoFormat.writes(testModel) shouldBe testJson
    }

    "convert a correctly formatted json model into a SubscriptionRequest" in {
      SubscriptionRequest.mongoFormat.reads(testJson) shouldBe JsSuccess(testModel)
    }
  }
}
