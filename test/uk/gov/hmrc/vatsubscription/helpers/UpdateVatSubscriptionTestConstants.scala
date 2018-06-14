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

package uk.gov.hmrc.vatsubscription.helpers

import uk.gov.hmrc.vatsubscription.models.MAReturnPeriod
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request._
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.{ErrorModel, SuccessModel}

object UpdateVatSubscriptionTestConstants {

  val messageType: String = "SubscriptionUpdate"
  val controlInformation: ControlInformation = ControlInformation()
  val agentOrCapacitor: AgentOrCapacitor = AgentOrCapacitor("XAIT0000000000")
  val changeReturnPeriod: RequestedChanges = RequestedChanges(addressDetails = false, returnPeriod = true, repaymentBankDetails = false)
  val updatedReturnPeriod: UpdatedReturnPeriod = UpdatedReturnPeriod(MAReturnPeriod)
  val nonAgentDeclaration: Declaration = Declaration(None, Signing())

  val updateSuccessResponse: SuccessModel = SuccessModel("XAVV0000000123456")
  val updateErrorResponse: ErrorModel = ErrorModel("TEST","ERROR")
}
