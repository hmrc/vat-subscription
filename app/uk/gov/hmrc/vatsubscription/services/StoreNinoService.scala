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

class StoreNinoService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository
                                     )(implicit ec: ExecutionContext) {

  def storeNino(vatNumber: String, nino: String): Future[Either[StoreNinoFailure, StoreNinoSuccess.type]] =
    subscriptionRequestRepository.upsertNino(vatNumber, nino) map {
      _ => Right(StoreNinoSuccess)
    } recover {
      case e: NoSuchElementException => Left(NinoDatabaseFailureNoVATNumber)
      case _ => Left(NinoDatabaseFailure)
    }
}

case object StoreNinoSuccess

sealed trait StoreNinoFailure

case object NinoDatabaseFailure extends StoreNinoFailure

case object NinoDatabaseFailureNoVATNumber extends StoreNinoFailure
