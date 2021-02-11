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

package services

import connectors.UpdateVatSubscriptionConnector
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import javax.inject.{Inject, Singleton}
import models.updateVatSubscription.request._
import models._
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateOrganisationDetailsService @Inject()(updateVatSubscriptionConnector: UpdateVatSubscriptionConnector) {

  def updateTradingName(updatedTradingName: TradingName, customerDetails: CustomerDetails)
                       (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {
    val subscriptionModel = constructTradingNameUpdateModel(updatedTradingName,
                                                            customerDetails)
    Logger.debug(s"[UpdateOrganisationDetailsService][updateTradingName]: updating trading name for user " +
      s"with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(user, subscriptionModel, hc)
  }

  def constructTradingNameUpdateModel(updatedTradingName: TradingName,
                                      customerDetails: CustomerDetails)
                                      (implicit user: User[_]): UpdateVatSubscription = {

    val agentContactDetails: Option[ContactDetails] =
      if(updatedTradingName.transactorOrCapacitorEmail.isDefined)
        Some(ContactDetails(None, None, None, updatedTradingName.transactorOrCapacitorEmail, None))
      else
        None

    val agentOrCapacitor: Option[AgentOrCapacitor] = user.arn.map(AgentOrCapacitor(_, agentContactDetails))

    UpdateVatSubscription(
      controlInformation = ControlInformation(customerDetails.welshIndicator.contains(true)),
      requestedChanges = ChangeOrganisationDetailsRequest,
      organisationDetails = Some(UpdatedOrganisationDetails(None, None, Some(tradingName(updatedTradingName, customerDetails)))),
      updatedPPOB = None,
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      declaration = Declaration(agentOrCapacitor, Signing()),
      commsPreference = None
    )
}

  private[services] def tradingName(newTradingName: TradingName, details: CustomerDetails): String = {
    newTradingName.tradingName.fold {
      details.organisationName match {
        case Some(businessName) => businessName
        case _ =>
          (details.firstName.fold("")(name => name + " ") +
          details.middleName.fold("")(name => name + " ") +
          details.lastName.getOrElse("")).trim
      }
    } (name => name)
  }

  def updateBusinessName(updatedBusinessName: BusinessName, approvedDetails: CustomerDetails)
                        (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {
    val subscriptionModel = constructBusinessNameUpdateModel(updatedBusinessName, approvedDetails)
    Logger.debug(s"[UpdateOrganisationDetailsService][updateBusinessName]: updating business name for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(user, subscriptionModel, hc)
  }

  def constructBusinessNameUpdateModel(updatedBusinessName: BusinessName, approvedDetails: CustomerDetails)
                                      (implicit user: User[_]): UpdateVatSubscription = {

    val agentContactDetails: Option[ContactDetails] =
      if(updatedBusinessName.transactorOrCapacitorEmail.isDefined)
        Some(ContactDetails(None, None, None, updatedBusinessName.transactorOrCapacitorEmail, None))
      else
        None

    val agentOrCapacitor: Option[AgentOrCapacitor] = user.arn.map(AgentOrCapacitor(_, agentContactDetails))

    val welshIndicator = approvedDetails.welshIndicator.contains(true)
    val updatedOrgDetails = UpdatedOrganisationDetails(
      Some(updatedBusinessName.businessName),
      Some(IndividualName(
        approvedDetails.title, approvedDetails.firstName, approvedDetails.middleName, approvedDetails.lastName
      )),
      approvedDetails.tradingName
    )

    UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator),
      requestedChanges = ChangeOrganisationDetailsRequest,
      organisationDetails = Some(updatedOrgDetails),
      updatedPPOB = None,
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      declaration = Declaration(agentOrCapacitor, Signing()),
      commsPreference = None
    )
  }
}
