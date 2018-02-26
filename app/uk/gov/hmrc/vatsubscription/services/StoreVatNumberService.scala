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
import uk.gov.hmrc.vatsubscription.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.vatsubscription.models.{HaveRelationshipResponse, NoRelationshipResponse}
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository

import scala.concurrent.{ExecutionContext, Future}

class StoreVatNumberService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository,
                                      agentClientRelationshipsConnector: AgentClientRelationshipsConnector
                                     )(implicit ec: ExecutionContext) {
  def storeVatNumber(vatNumber: String,
                     optAgentReferenceNumber: Option[String]
                    )(implicit hc: HeaderCarrier): Future[Either[StoreVatNumberFailure, StoreVatNumberSuccess.type]] = {
    optAgentReferenceNumber match {
      case Some(agentReferenceNumber) =>
        storeDelegatedVatNumber(vatNumber, agentReferenceNumber)
    }
  }

  private def storeDelegatedVatNumber(vatNumber: String, agentReferenceNumber: String)(implicit hc: HeaderCarrier) =
    agentClientRelationshipsConnector.checkAgentClientRelationship(agentReferenceNumber, vatNumber) flatMap {
      case Right(HaveRelationshipResponse) =>
        insertVatNumber(vatNumber)
      case Right(NoRelationshipResponse) =>
        Future.successful(Left(RelationshipNotFound))
      case _ =>
        Future.successful(Left(AgentServicesConnectionFailure))
    } recover {
      case _ => Left(AgentServicesConnectionFailure)
    }

  private def insertVatNumber(vatNumber: String) =
    subscriptionRequestRepository.insertVatNumber(vatNumber) map {
      _ => Right(StoreVatNumberSuccess)
    } recover {
      case _ => Left(VatNumberDatabaseFailure)
    }
}

object StoreVatNumberSuccess

sealed trait StoreVatNumberFailure

object VatNumberDatabaseFailure extends StoreVatNumberFailure

object RelationshipNotFound extends StoreVatNumberFailure

object AgentServicesConnectionFailure extends StoreVatNumberFailure