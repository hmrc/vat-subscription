/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants._
import uk.gov.hmrc.vatsubscription.models.MTDfBMandated

class UpdateVatSubscriptionSpec extends UnitSpec {

  "UpdateVatSubscription" when {

    "serializing to JSON" should {

      "for the current DES API1365 writes" should {

        "Output the correct JSON for UpdateVatSubscriptionModelMax" in {
          UpdateVatSubscription.DESApi1365WritesR5.writes(updateVatSubscriptionModelMax) shouldBe updateVatSubscriptionCurrentDESApi1365JsonMax
        }

        "Output the correct JSON for UpdateVatSubscriptionModelMin" in {
          UpdateVatSubscription.DESApi1365WritesR5.writes(updateVatSubscriptionModelMin) shouldBe updateVatSubscriptionCurrentDESApi1365JsonMin
        }
      }

      "for the latest DES API1365 writes" should {

        "Output the correct JSON for UpdateVatSubscriptionModelMax" in {
          UpdateVatSubscription.DESApi1365WritesR6.writes(updateVatSubscriptionModelMax) shouldBe updateVatSubscriptionLatestDESApi1365JsonMax
        }

        "Output the correct JSON for UpdateVatSubscriptionModelMin" in {
          UpdateVatSubscription.DESApi1365WritesR6.writes(updateVatSubscriptionModelMin) shouldBe updateVatSubscriptionLatestDESApi1365JsonMin
        }
      }
    }
  }

  "Control Information" when {

    "serializing to JSON" should {

      "Output the correct JSON for the full model" in {
        Json.toJson(ControlInformation(welshIndicator = true, mandationStatus = Some(MTDfBMandated))) shouldBe controlInformationJsonMax
      }
    }

    "reading from JSON" should {

      "Output the correct model from the provided JSON" in {
        ControlInformation.reads.reads(controlInformationJsonMax).get shouldBe ControlInformation(welshIndicator = true, mandationStatus = Some(MTDfBMandated))
      }

    }


  }
}
