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

import javax.inject.Inject

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.IdentityVerificationConnector
import uk.gov.hmrc.vatsubscription.httpparsers.IdentityVerified
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository

import scala.concurrent.{ExecutionContext, Future}

class IdentityVerificationOrchestrationService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository,
                                                         identityVerificationConnector: IdentityVerificationConnector
                                                        )(implicit ec: ExecutionContext) {

  import uk.gov.hmrc.vatsubscription.services.IdentityVerificationOrchestrationService.IdentityVerificationOrchestrationResponse

  def checkIdentityVerification(vatNumber: String, journeyId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[IdentityVerificationOrchestrationResponse] =
    ???

}

object IdentityVerificationOrchestrationService {

  type IdentityVerificationOrchestrationResponse = Either[IdentityVerificationOrchestrationFailure, IdentityVerified.type]

  sealed trait IdentityVerificationOrchestrationFailure

  case object IdentityNotVerified extends IdentityVerificationOrchestrationFailure

  case object IdentityVerificationConnectionFailure extends IdentityVerificationOrchestrationFailure

  case object IdentityVerificationDatabaseFailure extends IdentityVerificationOrchestrationFailure

}