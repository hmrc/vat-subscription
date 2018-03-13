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

import cats.data.EitherT
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.IdentityVerificationConnector
import uk.gov.hmrc.vatsubscription.httpparsers
import uk.gov.hmrc.vatsubscription.httpparsers.IdentityVerified
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository
import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}

class IdentityVerificationOrchestrationService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository,
                                                         identityVerificationConnector: IdentityVerificationConnector
                                                        )(implicit ec: ExecutionContext) {

  import uk.gov.hmrc.vatsubscription.services.IdentityVerificationOrchestrationService._

  def updateIdentityVerificationState(vatNumber: String,
                                      journeyLink: String,
                                      confidenceLevel: ConfidenceLevel
                                     )(implicit hc: HeaderCarrier): Future[IdentityVerificationOrchestrationResponse] = {
    for {
      _ <- checkIdentityVerification(journeyLink, confidenceLevel)
      _ <- upsertIdentityVerified(vatNumber)
    } yield IdentityVerified
  }.value


  private def checkIdentityVerification(journeyLink: String,
                                        confidenceLevel: ConfidenceLevel
                                       )(implicit hc: HeaderCarrier): EitherT[Future, IdentityVerificationOrchestrationFailure, IdentityVerified.type] =
    EitherT(if (confidenceLevel < ConfidenceLevel.L200) {
      identityVerificationConnector.getIdentityVerificationOutcome(journeyLink) map {
        case Right(IdentityVerified) => Right(IdentityVerified)
        case Right(httpparsers.IdentityNotVerified(_)) => Left(IdentityNotVerified)
        case _ => Left(IdentityVerificationConnectionFailure)
      }
    } else {
      Future.successful(Right(IdentityVerified))
    })

  private def upsertIdentityVerified(vatNumber: String): EitherT[Future, IdentityVerificationOrchestrationFailure, IdentityVerified.type] =
    EitherT(subscriptionRequestRepository.upsertIdentityVerified(vatNumber)
      .map(_ => Right(IdentityVerified))
      .recover { case _ => Left(IdentityVerificationDatabaseFailure) })
}

object IdentityVerificationOrchestrationService {

  type IdentityVerificationOrchestrationResponse = Either[IdentityVerificationOrchestrationFailure, IdentityVerified.type]

  sealed trait IdentityVerificationOrchestrationFailure

  case object IdentityNotVerified extends IdentityVerificationOrchestrationFailure

  case object IdentityVerificationConnectionFailure extends IdentityVerificationOrchestrationFailure

  case object IdentityVerificationDatabaseFailure extends IdentityVerificationOrchestrationFailure

}
