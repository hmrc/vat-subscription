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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.UpdateVatSubscriptionTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.PPOBTestConstants._

class UpdateVatSubscriptionSpec extends UnitSpec {

  "UpdateVatSubscription Writes" when {

    "supplied with an UpdatedReturnPeriod" should {

      val model: UpdateVatSubscription = UpdateVatSubscription(
        requestedChanges = changeReturnPeriod,
        updatedPPOB = None,
        updatedReturnPeriod = Some(updatedReturnPeriod),
        updateDeregInfo = None,
        declaration = Declaration(Some(agentOrCapacitor), Signing())
      )

      "output a correctly formatted UpdateVatSubscription json object" in {
        val result =
          Json.obj(
            "messageType" -> messageType,
            "controlInformation" -> controlInformation,
            "requestedChange" -> model.requestedChanges,
            "returnPeriods" -> model.updatedReturnPeriod,
            "declaration" -> model.declaration
          )
        UpdateVatSubscription.writes.writes(model) shouldBe result
      }
    }

    "supplied with an UpdatedPPOB" should {

      val model: UpdateVatSubscription = UpdateVatSubscription(
        requestedChanges = changePPOB,
        updatedPPOB = Some(UpdatedPPOB(ppobModelMaxPost)),
        updatedReturnPeriod = None,
        updateDeregInfo = None,
        declaration = Declaration(Some(agentOrCapacitor), Signing())
      )

      "output a correctly formatted UpdateVatSubscription json object" in {
        val result =
          Json.obj(
            "messageType" -> messageType,
            "controlInformation" -> controlInformation,
            "requestedChange" -> model.requestedChanges,
            "contactDetails" -> model.updatedPPOB,
            "declaration" -> model.declaration
          )
        UpdateVatSubscription.writes.writes(model) shouldBe result
      }
    }

    "supplied with an UpdatedPPOB and an UpdatedReturnPeriod" should {

      val model: UpdateVatSubscription = UpdateVatSubscription(
        requestedChanges = changePPOBandRP,
        updatedPPOB = Some(UpdatedPPOB(ppobModelMaxPost)),
        updatedReturnPeriod = Some(updatedReturnPeriod),
        updateDeregInfo = None,
        declaration = Declaration(Some(agentOrCapacitor), Signing())
      )

      "output a correctly formatted UpdateVatSubscription json object" in {
        val result =
          Json.obj(
            "messageType" -> messageType,
            "controlInformation" -> controlInformation,
            "requestedChange" -> model.requestedChanges,
            "contactDetails" -> model.updatedPPOB,
            "returnPeriods" -> model.updatedReturnPeriod,
            "declaration" -> model.declaration
          )
        UpdateVatSubscription.writes.writes(model) shouldBe result
      }
    }

    "not supplied with any updates" should {

      val model: UpdateVatSubscription = UpdateVatSubscription(
        requestedChanges = changeReturnPeriod,
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregInfo = None,
        declaration = Declaration(Some(agentOrCapacitor), Signing())
      )

      "output a correctly formatted UpdateVatSubscription json object" in {
        val result =
          Json.obj(
            "messageType" -> messageType,
            "controlInformation" -> controlInformation,
            "requestedChange" -> model.requestedChanges,
            "declaration" -> model.declaration
          )
        UpdateVatSubscription.writes.writes(model) shouldBe result
      }
    }
  }

}
