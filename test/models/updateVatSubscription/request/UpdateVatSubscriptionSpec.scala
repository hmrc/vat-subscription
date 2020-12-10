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

package models.updateVatSubscription.request

import assets.TestUtil
import play.api.libs.json.{JsValue, Json}
import helpers.UpdateVatSubscriptionTestConstants._
import helpers.{DeclarationTestConstants, DeregistrationInfoTestConstants}
import models.{CommsPreference, MTDfBExempt, MTDfBMandated, PaperPreference}

class UpdateVatSubscriptionSpec extends TestUtil {

  val updateVatSubscriptionLatestDESApi1365JsonMax: JsValue = Json.obj(
    "messageType" -> messageType,
    "controlInformation" -> ControlInformation(welshIndicator = false, mandationStatus = Some(MTDfBMandated)),
    "requestedChange" -> Json.toJson(changeAll)(RequestedChanges.DESApi1365Writes(mockAppConfig)),
    "organisationDetails" -> Json.toJson(updatedTradingName),
    "contactDetails" -> Json.toJson(updatedPPOB),
    "returnPeriods" -> Json.toJson(updatedReturnPeriod),
    "deregistrationInfo" -> DeregistrationInfoTestConstants.deregInfoCeasedTradingDESJson,
    "declaration" -> DeclarationTestConstants.declarationDESJsonAgent,
    "commsPreference" -> Json.toJson(PaperPreference)(CommsPreference.updatePreferenceWrites)
  )

  val updateVatSubscriptionLatestDESApi1365JsonMin: JsValue = Json.obj(
    "messageType" -> messageType,
    "controlInformation" -> ControlInformation(welshIndicator = false),
    "requestedChange" -> Json.toJson(ChangeReturnPeriod)(RequestedChanges.DESApi1365Writes(mockAppConfig)),
    "returnPeriods" -> Json.toJson(updatedReturnPeriod),
    "declaration" -> DeclarationTestConstants.declarationDESJsonlNonAgent
  )

  "UpdateVatSubscription" when {

    "serializing to JSON" should {

      "for the latest DES API1365 writes" should {

        "Output the correct JSON for UpdateVatSubscriptionModelMax" in {
          UpdateVatSubscription.DESApi1365Writes(mockAppConfig).writes(updateVatSubscriptionModelMax) shouldBe
            updateVatSubscriptionLatestDESApi1365JsonMax
        }

        "Output the correct JSON for UpdateVatSubscriptionModelMin" in {
          UpdateVatSubscription.DESApi1365Writes(mockAppConfig).writes(updateVatSubscriptionModelMin) shouldBe
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
