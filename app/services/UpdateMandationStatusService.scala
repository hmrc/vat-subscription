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

package services

import javax.inject.{Inject, Singleton}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import connectors.UpdateVatSubscriptionConnector
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.post.MandationStatusPost
import models.{ContactDetails, User}
import models.updateVatSubscription.request._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateMandationStatusService @Inject()(updateVatSubscriptionConnector: UpdateVatSubscriptionConnector) {

  def updateMandationStatus(updatedMandationStatus: MandationStatusPost, welshIndicator: Boolean)
                 (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {

    val subscriptionModel = constructMandationStatusUpdateModel(updatedMandationStatus, welshIndicator)
    Logger.debug(s"[UpdateMandationStatusService][updateMandationStatus]: updating Mandation Status for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(user, subscriptionModel, hc)
  }

  def constructMandationStatusUpdateModel(mandationStatusPost: MandationStatusPost,
                                          welshIndicator: Boolean)
                                         (implicit user: User[_]): UpdateVatSubscription = {

    val agentContactDetails: Option[ContactDetails] =
      if(mandationStatusPost.transactorOrCapacitorEmail.isDefined)
        Some(ContactDetails(None, None, None, mandationStatusPost.transactorOrCapacitorEmail, None))
      else
        None

    val agentOrCapacitor: Option[AgentOrCapacitor] = user.arn.map(AgentOrCapacitor(_, agentContactDetails))

    UpdateVatSubscription(
      controlInformation = ControlInformation(mandationStatus = Some(mandationStatusPost.mandationStatus), welshIndicator = welshIndicator),
      requestedChanges = ChangeMandationStatus,
      organisationDetails = None,
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      updatedPPOB = None,
      declaration = Declaration(agentOrCapacitor, Signing()),
      commsPreference = None
    )
  }
}