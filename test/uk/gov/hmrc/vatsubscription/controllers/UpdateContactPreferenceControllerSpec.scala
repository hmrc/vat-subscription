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

package uk.gov.hmrc.vatsubscription.controllers

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsJson, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import uk.gov.hmrc.vatsubscription.assets.TestUtil
import uk.gov.hmrc.vatsubscription.controllers.actions.mocks.MockVatAuthorised
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants.testVatNumber
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.SuccessModel

import scala.concurrent.Future

class UpdateContactPreferenceControllerSpec extends TestUtil
with MockVatAuthorised {
  object TestCommsPreference
  extends UpdateContactPreferenceController(mockVatAuthorised ,controllerComponents)

  val paperPref: FakeRequest[AnyContentAsJson] = FakeRequest().withJsonBody(Json.obj("commsPreference" -> "PAPER"))

  "the updateContactPreferences() method" when {

    "the user is not authorised" should {

      "return FORBIDDEN" in {

        mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
        val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(paperPref))
        status(res) shouldBe FORBIDDEN
      }
    }

    "the user is authorised" should {

      "return OK" in {

        mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
        val res: Result = await(TestCommsPreference.updateContactPreference(testVatNumber)(paperPref))

        status(res) shouldBe OK
      }
    }
  }
}
