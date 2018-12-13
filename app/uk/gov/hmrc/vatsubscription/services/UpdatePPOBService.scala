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

package uk.gov.hmrc.vatsubscription.services

import javax.inject.{Inject, Singleton}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.UpdateVatSubscriptionConnector
import uk.gov.hmrc.vatsubscription.httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import uk.gov.hmrc.vatsubscription.models.{ContactDetails, User}
import uk.gov.hmrc.vatsubscription.models.post.PPOBPost
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdatePPOBService @Inject()(updateVatSubscriptionConnector: UpdateVatSubscriptionConnector) {

  def updatePPOB(updatedPPOB: PPOBPost, welshIndicator: Boolean)
                (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {

    val subscriptionModel = constructPPOBUpdateModel(updatedPPOB, welshIndicator)
    Logger.debug(s"[UpdateVatSubscriptionService][updateReturnPeriod]: updating PPOB for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(user, subscriptionModel, hc)
  }

  def constructPPOBUpdateModel(updatedPPOB: PPOBPost,
                               welshIndicator: Boolean)
                              (implicit user: User[_]): UpdateVatSubscription = {

    val agentContactDetails: Option[ContactDetails] =
      if(updatedPPOB.transactorOrCapacitorEmail.isDefined)
        Some(ContactDetails(None, None, None, updatedPPOB.transactorOrCapacitorEmail, None))
      else
        None

    val agentOrCapacitor: Option[AgentOrCapacitor] = user.arn.map(AgentOrCapacitor(_, agentContactDetails))

    UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator),
      requestedChanges = ChangePPOB,
      updatedPPOB = Some(UpdatedPPOB(updatedPPOB)),
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      declaration = Declaration(agentOrCapacitor, Signing())
    )
  }
}
