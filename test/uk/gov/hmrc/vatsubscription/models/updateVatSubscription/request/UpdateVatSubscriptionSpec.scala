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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request

import uk.gov.hmrc.vatsubscription.assets.TestUtil
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants._
import uk.gov.hmrc.vatsubscription.models.{MTDfBExempt, MTDfBMandated}

class UpdateVatSubscriptionSpec extends TestUtil {

  "UpdateVatSubscription" when {

    "serializing to JSON" should {

      "for the latest DES API1365 writes" should {

        "Output the correct JSON for UpdateVatSubscriptionModelMax" in {
          UpdateVatSubscription.DESApi1365Writes.writes(updateVatSubscriptionModelMax) shouldBe
            updateVatSubscriptionLatestDESApi1365JsonMax
        }

        "Output the correct JSON for UpdateVatSubscriptionModelMin" in {
          UpdateVatSubscription.DESApi1365Writes.writes(updateVatSubscriptionModelMin) shouldBe
            updateVatSubscriptionLatestDESApi1365JsonMin
        }
      }
    }
  }

  "Control Information" when {

    "serializing to JSON" should {

      "Output the correct JSON for the full model" in {
        Json.toJson(ControlInformation(welshIndicator = true, mandationStatus = Some(MTDfBExempt))) shouldBe
          controlInformationJsonMax
      }

      "Output the correct JSON for the model without the optional fields" in {
        Json.toJson(ControlInformation(welshIndicator = true)) shouldBe controlInformationJsonMin
      }
    }

    "reading from JSON" should {

      "Output the correct model with all optional values from the provided JSON" when {

        "the newStatusIndicators feature is on" in {
          ControlInformation.reads(mockAppConfig).reads(controlInformationJsonMax).get shouldBe
            ControlInformation(
              welshIndicator = true,
              mandationStatus = Some(MTDfBExempt)
            )
        }

        "the newStatusIndicators feature is off" in {
          mockAppConfig.features.newStatusIndicators(false)
          ControlInformation.reads(mockAppConfig).reads(controlInformationJsonMax).get shouldBe
            ControlInformation(
              welshIndicator = true,
              mandationStatus = Some(MTDfBMandated)
            )
        }
      }

      "Output the correct model with no optional values from the provided JSON" in {
        ControlInformation.reads(mockAppConfig).reads(controlInformationJsonMin).get shouldBe
          ControlInformation(welshIndicator = true)
      }
    }
  }
}
