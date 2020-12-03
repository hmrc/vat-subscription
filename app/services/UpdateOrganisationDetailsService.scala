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

import connectors.UpdateVatSubscriptionConnector
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import javax.inject.{Inject, Singleton}
import models.{ContactDetails, TradingName, User}
import models.updateVatSubscription.request._
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateOrganisationDetailsService @Inject()(updateVatSubscriptionConnector: UpdateVatSubscriptionConnector) {

  def updateTradingName(updatedTradingName: TradingName, welshIndicator: Boolean)
                       (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {
    val subscriptionModel = constructTradingNameUpdateModel(updatedTradingName, welshIndicator)
    Logger.debug(s"[UpdateReturnPeriodService][updateReturnPeriod]: updating return period for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(user, subscriptionModel, hc)
  }

  def constructTradingNameUpdateModel(updatedTradingName: TradingName, welshIndicator: Boolean)
                                      (implicit user: User[_]): UpdateVatSubscription = {

    val agentContactDetails: Option[ContactDetails] =
      if(updatedTradingName.transactorOrCapacitorEmail.isDefined)
        Some(ContactDetails(None, None, None, updatedTradingName.transactorOrCapacitorEmail, None))
      else
        None

    val agentOrCapacitor: Option[AgentOrCapacitor] = user.arn.map(AgentOrCapacitor(_, agentContactDetails))

    UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator),
      requestedChanges = ChangeOrganisationDetailsRequest,
      organisationDetails = Some(UpdatedOrganisationDetails(None, None, Some(removedNameIsEmpty(updatedTradingName.tradingName)))),
      updatedPPOB = None,
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      declaration = Declaration(agentOrCapacitor, Signing()),
      commsPreference = None
    )
  }

  private def removedNameIsEmpty(newTradingName: Option[String]): String = newTradingName.fold("")(name => name)

}
