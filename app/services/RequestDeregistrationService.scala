/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import connectors.UpdateVatSubscriptionConnector
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.updateVatSubscription.request._
import models.updateVatSubscription.request.deregistration.DeregistrationInfo
import models.{ContactDetails, User}
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RequestDeregistrationService @Inject()(updateVatSubscriptionConnector: UpdateVatSubscriptionConnector) extends LoggingUtil {

  def deregister(deregistration: DeregistrationInfo, welshIndicator: Boolean)
                (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {

    val subscriptionModel = constructDeregistrationModel(deregistration, welshIndicator)
    infoLog(s"[RequestDeregistrationService][deregister] Submitting Deregistration Request for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(subscriptionModel, hc)
  }

  def constructDeregistrationModel(deregistration: DeregistrationInfo, welshIndicator: Boolean)
                                  (implicit user: User[_]): UpdateVatSubscription = {

    val agentContactDetails: Option[ContactDetails] =
      if(deregistration.transactorOrCapacitorEmail.isDefined)
        Some(ContactDetails(None, None, None, deregistration.transactorOrCapacitorEmail, None))
      else
        None

    val agentOrCapacitor: Option[AgentOrCapacitor] = user.arn.map(AgentOrCapacitor(_, agentContactDetails))

    UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator),
      requestedChanges = DeregistrationRequest,
      organisationDetails = None,
      updatedPPOB = None,
      updatedReturnPeriod = None,
      updateDeregistrationInfo = Some(deregistration),
      declaration = Declaration(agentOrCapacitor, Signing()),
      commsPreference = None
    )
  }
}
