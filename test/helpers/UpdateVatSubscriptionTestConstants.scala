/*
 * Copyright 2021 HM Revenue & Customs
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

package helpers

import play.api.libs.json.{JsValue, Json}
import models.{MAReturnPeriod, MTDfBMandated, MandationStatus, NonMTDfB, PaperPreference}
import models.updateVatSubscription.request._
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}

object UpdateVatSubscriptionTestConstants {

  val messageType: String = "SubscriptionUpdate"

  val changeAll: RequestedChanges = RequestedChanges(ppobDetails = true, returnPeriod = true, deregInfo = true)

  val updatedReturnPeriod: UpdatedReturnPeriod = UpdatedReturnPeriod(MAReturnPeriod(Some("agent@emailaddress"), None, None))
  val updatedTradingName: UpdatedOrganisationDetails = UpdatedOrganisationDetails(None, None, Some("He-man's accounting firm"))
  val updatedPPOB: UpdatedPPOB = UpdatedPPOB(PPOBTestConstants.ppobModelMaxPost)
  val updatedMandationStatus: MandationStatus = NonMTDfB

  val updateSuccessResponse: SuccessModel = SuccessModel("XAVV0000000123456")
  val updateErrorResponse: ErrorModel = ErrorModel("TEST","ERROR")
  val updateConflictResponse: ErrorModel = ErrorModel("CONFLICT","ERROR")
  val updateErrorModel: ErrorModel = ErrorModel("404","Not found")

  val updateVatSubscriptionModelMax: UpdateVatSubscription = UpdateVatSubscription(
    controlInformation = ControlInformation(welshIndicator = false, mandationStatus = Some(MTDfBMandated)),
    requestedChanges = changeAll,
    organisationDetails = Some(updatedTradingName),
    updatedPPOB = Some(updatedPPOB),
    updatedReturnPeriod = Some(updatedReturnPeriod),
    updateDeregistrationInfo = Some(DeregistrationInfoTestConstants.deregInfoCeasedTradingModel),
    declaration = DeclarationTestConstants.declarationModelAgent,
    commsPreference = Some(PaperPreference)
  )

  val updateVatSubscriptionModelMin: UpdateVatSubscription = UpdateVatSubscription(
    controlInformation = ControlInformation(welshIndicator = false),
    requestedChanges = ChangeReturnPeriod,
    organisationDetails = None,
    updatedPPOB = None,
    updatedReturnPeriod = Some(updatedReturnPeriod),
    updateDeregistrationInfo = None,
    declaration = Declaration(None, Signing()),
    commsPreference = None
  )

  val controlInformationJsonMax: JsValue = Json.obj(
    "welshIndicator" -> true,
    "source" -> "100",
    "mandationStatus" -> "1"
  )

  val controlInformationJsonMin: JsValue = Json.obj(
    "welshIndicator" -> true,
    "source" -> "100"
  )
}
