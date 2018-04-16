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

import cats.data._
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.{AgentClientRelationshipsConnector, MandationStatusConnector}
import uk.gov.hmrc.vatsubscription.httpparsers.GetMandationStatusHttpParser.VatNumberNotFound
import uk.gov.hmrc.vatsubscription.models._
import uk.gov.hmrc.vatsubscription.models.monitoring.AgentClientRelationshipAuditing.AgentClientRelationshipAuditModel
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.services.monitoring.AuditService
import uk.gov.hmrc.vatsubscription.utils.EnrolmentUtils._
import StoreVatNumberService._
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StoreVatNumberService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository,
                                      agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                      mandationStatusConnector: MandationStatusConnector,
                                      auditService: AuditService
                                     )(implicit ec: ExecutionContext) {

  def storeVatNumber(vatNumber: String,
                     enrolments: Enrolments
                    )(implicit hc: HeaderCarrier, request: Request[_]): Future[Either[StoreVatNumberFailure, StoreVatNumberSuccess.type]] = {
      for {
        _ <- checkUserAuthority(vatNumber, enrolments)
        _ <- checkExistingVatSubscription(vatNumber)
        _ <- insertVatNumber(vatNumber)
      } yield StoreVatNumberSuccess
    }.value

  private def checkUserAuthority(vatNumber: String,
                                 enrolments: Enrolments
                                )(implicit request: Request[_], hc: HeaderCarrier): EitherT[Future, StoreVatNumberFailure, Any] = {
    EitherT((enrolments.vatNumber, enrolments.agentReferenceNumber) match {
      case (Some(vatNumberFromEnrolment), _) =>
        if (vatNumber == vatNumberFromEnrolment) Future.successful(Right(UserHasMatchingEnrolment))
        else Future.successful(Left(DoesNotMatchEnrolment))
      case (_, Some(agentReferenceNumber)) =>
        agentClientRelationshipsConnector.checkAgentClientRelationship(agentReferenceNumber, vatNumber) map {
          case Right(HaveRelationshipResponse) =>
            auditService.audit(AgentClientRelationshipAuditModel(vatNumber, agentReferenceNumber, haveRelationship = true))
            Right(HaveRelationshipResponse)
          case Right(NoRelationshipResponse) =>
            auditService.audit(AgentClientRelationshipAuditModel(vatNumber, agentReferenceNumber, haveRelationship = false))
            Left(RelationshipNotFound)
          case _ =>
            Left(AgentServicesConnectionFailure)
        }
      case _ =>
        Future.successful(Left(InsufficientEnrolments))
    })

  }

  private def checkExistingVatSubscription(vatNumber: String
                                          )(implicit hc: HeaderCarrier): EitherT[Future, StoreVatNumberFailure, NotSubscribed.type] =
    EitherT(mandationStatusConnector.getMandationStatus(vatNumber) map {
      case Right(NonMTDfB | NonDigital) | Left(VatNumberNotFound) => Right(NotSubscribed)
      case Right(MTDfBMandated | MTDfBVoluntary) => Left(AlreadySubscribed)
      case _ => Left(VatSubscriptionConnectionFailure)
    })

  private def insertVatNumber(vatNumber: String
                             )(implicit hc: HeaderCarrier): EitherT[Future, StoreVatNumberFailure, StoreVatNumberSuccess.type] =
    EitherT(subscriptionRequestRepository.upsertVatNumber(vatNumber) map {
      _ => Right(StoreVatNumberSuccess)
    } recover {
      case _ => Left(VatNumberDatabaseFailure)
    })
}

object StoreVatNumberService {
  case object StoreVatNumberSuccess

  case object NotSubscribed

  case object UserHasMatchingEnrolment

  sealed trait StoreVatNumberFailure

  case object AlreadySubscribed extends StoreVatNumberFailure

  case object DoesNotMatchEnrolment extends StoreVatNumberFailure

  case object InsufficientEnrolments extends StoreVatNumberFailure

  case object RelationshipNotFound extends StoreVatNumberFailure

  case object AgentServicesConnectionFailure extends StoreVatNumberFailure

  case object VatSubscriptionConnectionFailure extends StoreVatNumberFailure

  case object VatNumberDatabaseFailure extends StoreVatNumberFailure
}


