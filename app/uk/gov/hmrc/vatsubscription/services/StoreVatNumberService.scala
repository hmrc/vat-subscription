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
import cats.data._
import cats.implicits._
import play.api.mvc.Request
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.{BadGatewayException, HeaderCarrier, InternalServerException}
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.config.featureswitch.{AlreadySubscribedCheck, MTDEligibilityCheck}
import uk.gov.hmrc.vatsubscription.connectors.{AgentClientRelationshipsConnector, KnownFactsAndControlListInformationConnector, MandationStatusConnector}
import uk.gov.hmrc.vatsubscription.httpparsers.GetMandationStatusHttpParser.VatNumberNotFound
import uk.gov.hmrc.vatsubscription.httpparsers._
import uk.gov.hmrc.vatsubscription.models._
import uk.gov.hmrc.vatsubscription.models.monitoring.AgentClientRelationshipAuditing.AgentClientRelationshipAuditModel
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.services.StoreVatNumberService._
import uk.gov.hmrc.vatsubscription.services.monitoring.AuditService
import uk.gov.hmrc.vatsubscription.utils.EnrolmentUtils._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StoreVatNumberService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository,
                                      agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                      mandationStatusConnector: MandationStatusConnector,
                                      knownFactsAndControlListInformationConnector: KnownFactsAndControlListInformationConnector,
                                      auditService: AuditService,
                                      appConfig: AppConfig
                                     )(implicit ec: ExecutionContext) {

  def storeVatNumber(vatNumber: String,
                     enrolments: Enrolments,
                     businessPostcode: Option[String],
                     vatRegistrationDate: Option[String]
                    )(implicit hc: HeaderCarrier, request: Request[_]): Future[Either[StoreVatNumberFailure, StoreVatNumberSuccess.type]] = {
    for {
      _ <- checkUserAuthority(vatNumber, enrolments, businessPostcode, vatRegistrationDate)
      _ <- checkExistingVatSubscription(vatNumber)
      _ <- checkEligibility(vatNumber, enrolments, businessPostcode, vatRegistrationDate)
      _ <- insertVatNumber(vatNumber)
    } yield StoreVatNumberSuccess
  }.value

  private def checkUserAuthority(vatNumber: String,
                                 enrolments: Enrolments,
                                 businessPostcode: Option[String],
                                 vatRegistrationDate: Option[String]
                                )(implicit request: Request[_], hc: HeaderCarrier): EitherT[Future, StoreVatNumberFailure, Any] = {
    EitherT((enrolments.vatNumber, enrolments.agentReferenceNumber) match {
      case (Some(vatNumberFromEnrolment), _) =>
        if (vatNumber == vatNumberFromEnrolment) Future.successful(Right(UserHasMatchingEnrolment))
        else Future.successful(Left(DoesNotMatchEnrolment))
      case (_, None) if businessPostcode.isDefined && vatRegistrationDate.isDefined =>
        Future.successful(Right(UserHasKnownFacts))
      case (_, Some(agentReferenceNumber)) =>
        checkAgentClientRelationship(vatNumber, agentReferenceNumber)
      case _ =>
        Future.successful(Left(InsufficientEnrolments))
    })
  }

  private def checkAgentClientRelationship(vatNumber: String,
                                           agentReferenceNumber: String
                                          )(implicit hc: HeaderCarrier,
                                            request: Request[_]) = {
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
  }

  // todo move this to service???? depends on how to union the types of failures
  private def checkEligibility(vatNumber: String,
                               enrolments: Enrolments,
                               optBusinessPostcode: Option[String],
                               optVatRegistrationDate: Option[String]
                              )(implicit hc: HeaderCarrier): EitherT[Future, StoreVatNumberFailure, EligibilitySuccess.type] =
    if (appConfig.isEnabled(MTDEligibilityCheck)) {
      EitherT[Future, KnownFactsAndControlListInformationFailure, MtdEligible](
        knownFactsAndControlListInformationConnector.getKnownFactsAndControlListInformation(vatNumber: String)
      ).transform[StoreVatNumberFailure, EligibilitySuccess.type] {
        case Right(MtdEligible(businessPostcode, vatRegistrationDate)) =>
          (optBusinessPostcode, optVatRegistrationDate) match {
            case (Some(enteredPostcode), Some(enteredVatRegistrationDate)) =>
              if((enteredPostcode equalsIgnoreCase businessPostcode) && (enteredVatRegistrationDate == vatRegistrationDate)) {
                Right[StoreVatNumberFailure, EligibilitySuccess.type](EligibilitySuccess)
              } else {
                Left[StoreVatNumberFailure, EligibilitySuccess.type](KnownFactsMismatch)
              }
            case _ =>
              Right[StoreVatNumberFailure, EligibilitySuccess.type](EligibilitySuccess)
          }
        // todo audit the reason?
        case Left(MtdIneligible(ineligibleReasons)) => Left(Ineligible)
        case Left(ControlListInformationVatNumberNotFound) => Left(VatNotFound)
        case Left(KnownFactsInvalidVatNumber) => Left(VatInvalid)
        case Left(err: UnexpectedKnownFactsAndControlListInformationFailure) =>
          throw new BadGatewayException(s"Known facts & control list returned ${err.status} ${err.body}")
      }
    } else {
      (optBusinessPostcode, optVatRegistrationDate) match {
        case (Some(_), Some(_)) =>
          EitherT.liftF(Future.failed(new InternalServerException("The known facts API is disabled and known facts were provided")))
        case _ =>
          EitherT.pure(EligibilitySuccess)
      }
    }

  private def checkExistingVatSubscription(vatNumber: String
                                          )(implicit hc: HeaderCarrier): EitherT[Future, StoreVatNumberFailure, NotSubscribed.type] =
    if (appConfig.isEnabled(AlreadySubscribedCheck)) {
      EitherT(mandationStatusConnector.getMandationStatus(vatNumber) map {
        case Right(NonMTDfB | NonDigital) | Left(VatNumberNotFound) => Right(NotSubscribed)
        case Right(MTDfBMandated | MTDfBVoluntary) => Left(AlreadySubscribed)
        case _ => Left(VatSubscriptionConnectionFailure)
      })
    } else {
      EitherT.pure(NotSubscribed)
    }

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

  case object UserHasKnownFacts

  case object EligibilitySuccess

  sealed trait StoreVatNumberFailure

  case object AlreadySubscribed extends StoreVatNumberFailure

  case object DoesNotMatchEnrolment extends StoreVatNumberFailure

  case object InsufficientEnrolments extends StoreVatNumberFailure

  case object KnownFactsMismatch extends StoreVatNumberFailure

  case object Ineligible extends StoreVatNumberFailure

  case object VatNotFound extends StoreVatNumberFailure

  case object VatInvalid extends StoreVatNumberFailure

  case object RelationshipNotFound extends StoreVatNumberFailure

  case object AgentServicesConnectionFailure extends StoreVatNumberFailure

  case object VatSubscriptionConnectionFailure extends StoreVatNumberFailure

  case object VatNumberDatabaseFailure extends StoreVatNumberFailure

}


