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

package uk.gov.hmrc.vatsubscription.controllers

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.vatsubscription.services.VatNumberEligibilityService
import uk.gov.hmrc.vatsubscription.helpers.TestConstants.testVatNumber

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatNumberEligibilityControllerSpec extends UnitSpec with MockAuthConnector {

  object TestVatNumberEligibilityController extends VatNumberEligibilityController(mockAuthConnector, new VatNumberEligibilityService)

  "checkVatNumberEligibility" should {
    "return NO_CONTENT when successful" in {
      mockAuthorise()(Future.successful(Unit))
      TestVatNumberEligibilityController
      val res = TestVatNumberEligibilityController.checkVatNumberEligibility(testVatNumber)(FakeRequest())
      status(res) shouldBe NO_CONTENT
    }
  }

}
