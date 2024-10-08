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

import config.AppConfig
import connectors.UpdateVatSubscriptionConnector
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.User
import models.post.{EmailPost, PPOBPost}
import models.updateVatSubscription.request._
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateEmailService @Inject()(updateVatSubscriptionConnector: UpdateVatSubscriptionConnector) extends LoggingUtil {

  def updateEmail(updatedEmail: EmailPost, welshIndicator: Boolean, appConfig: AppConfig)
                 (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {

    val subscriptionModel = constructPPOBUpdateModel(updatedEmail, welshIndicator, appConfig)
    infoLog(s"[UpdateVatSubscriptionService][updateEmail]: updating Email for user with vrn - ${user.vrn}")
    updateVatSubscriptionConnector.updateVatSubscription(subscriptionModel, hc)
  }

  def constructPPOBUpdateModel(updatedEmail: EmailPost,
                               welshIndicator: Boolean,
                               appConfig: AppConfig)
                              (implicit user: User[_]): UpdateVatSubscription = {

    val agentOrCapacitor: Option[AgentOrCapacitor] = user.arn.map(AgentOrCapacitor(_, None))

    val updatedPPOB = PPOBPost(updatedEmail.address, Some(updatedEmail.contactDetails), updatedEmail.websiteAddress, None)

    UpdateVatSubscription(
      controlInformation = ControlInformation(welshIndicator),
      requestedChanges = ChangePPOB,
      organisationDetails = None,
      updatedPPOB = if(appConfig.features.plusSignInPhoneNumbersEnabled.apply()) {
        Some(UpdatedPPOB(updatedPPOB))} else {Some(UpdatedPPOB(updatedPPOB).convertUKCountryCodes)},
      updatedReturnPeriod = None,
      updateDeregistrationInfo = None,
      declaration = Declaration(agentOrCapacitor, Signing()),
      commsPreference = None
    )
  }
}
