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

package uk.gov.hmrc.vatsubscription.services

import javax.inject.{Inject, Singleton}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.UpdateVatSubscriptionConnector
import uk.gov.hmrc.vatsubscription.httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import uk.gov.hmrc.vatsubscription.models.get.PPOBGet
import uk.gov.hmrc.vatsubscription.models.{ContactDetails, DigitalPreference, User, VatCustomerInformation}
import uk.gov.hmrc.vatsubscription.models.post.{CommsPreferencePost, PPOBAddressPost, PPOBPost}
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateContactPreferenceService @Inject()(updateVatSubscriptionConnector: UpdateVatSubscriptionConnector) {

  def updateContactPreference(updatedContactPreference: CommsPreferencePost,
                              welshIndicator: Boolean)
                             (implicit user: User[_],
                              hc: HeaderCarrier,
                              ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {
    val subscriptionModel = constructContactPreferenceModel(updatedContactPreference, welshIndicator)
    Logger.debug("[UpdateContactPreferenceService][updateContactPreference]: " +
      s"updating contact preference for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(user, subscriptionModel, hc)
  }

  def updatePreferenceAndEmail(newEmail: String,
                               currentDetails: VatCustomerInformation)
                              (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {
    val subscriptionModel = constructContactPreferenceAndEmailModel(newEmail, currentDetails.customerDetails.welshIndicator, currentDetails.ppob)
    Logger.debug("[UpdateContactPreferenceService][updatePreferenceAndEmail]: updating contact preference " +
      s"and email for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(user, subscriptionModel, hc)
  }

  def constructContactPreferenceModel(updatedContactPreference: CommsPreferencePost, welshIndicator: Boolean)
                                     (implicit user: User[_]): UpdateVatSubscription = {
    UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator),
      requestedChanges = ChangeCommsPreference,
      updatedPPOB = None,
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      declaration = Declaration(None, Signing()),
      commsPreference = Some(updatedContactPreference.commsPreference)
    )
  }

  def constructContactPreferenceAndEmailModel(newEmail: String,
                                              welshIndicator: Option[Boolean],
                                              currentContactDetails: PPOBGet): UpdateVatSubscription = {
    val currentAddress: PPOBAddressPost = PPOBAddressPost(
      line1 = currentContactDetails.address.line1,
      line2 = currentContactDetails.address.line2,
      line3 = currentContactDetails.address.line3,
      line4 = currentContactDetails.address.line4,
      line5 = currentContactDetails.address.line5,
      postCode = currentContactDetails.address.postCode,
      countryCode = currentContactDetails.address.countryCode
    )

    val updatedContactDetails: ContactDetails = currentContactDetails.contactDetails.fold(
      ContactDetails(None, None, None, emailAddress = Some(newEmail), emailVerified = Some(true))
    )(
      currentDetails => currentDetails.copy(emailAddress = Some(newEmail), emailVerified = Some(true))
    )

    UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator.contains(true)),
      requestedChanges = ChangeCommsPreferenceAndEmail,
      updatedPPOB = Some(UpdatedPPOB(PPOBPost(
        address = currentAddress,
        contactDetails = Some(updatedContactDetails),
        websiteAddress = currentContactDetails.websiteAddress,
        transactorOrCapacitorEmail = None
      ))),
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      declaration = Declaration(None, Signing()),
      commsPreference = Some(DigitalPreference)
    )
  }
}
