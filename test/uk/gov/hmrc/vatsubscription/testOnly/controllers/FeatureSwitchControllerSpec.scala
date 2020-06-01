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

package uk.gov.hmrc.vatsubscription.testOnly.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.vatsubscription.config.featureSwitch._
import uk.gov.hmrc.vatsubscription.testonly.controllers.FeatureSwitchController
import uk.gov.hmrc.vatsubscription.assets.TestUtil

import scala.concurrent.Future

class FeatureSwitchControllerSpec extends TestUtil {

  private lazy val target = new FeatureSwitchController(mockAppConfig, controllerComponents)

  "Calling the .get action" should {

    lazy val result: Future[Result] = target.get()(fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return JSON" in {
      contentType(result) shouldBe Some("application/json")
    }

    "return feature switch configuration" in {
      await(jsonBodyOf(result)) shouldEqual Json.toJson(
        FeatureSwitchModel(
          Api1365Version = Api1365Latest,
          Api1363Version = Api1363Latest,
          enableAnnualAccounting = true,
          newStatusIndicators = true
        ))
    }
  }

  "Calling the .update action" should {

    val body = Json.toJson(FeatureSwitchModel(
      Api1365Version = Api1365Latest,
      Api1363Version = Api1363Latest,
      enableAnnualAccounting = true,
      newStatusIndicators = true
    ))

    val result = call(target.update(), FakeRequest(POST, "")
      .withHeaders((CONTENT_TYPE, "application/json"))
      .withJsonBody(body))

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return new feature switch configuration" in {
      await(jsonBodyOf(result)) shouldEqual body
    }
  }
}
