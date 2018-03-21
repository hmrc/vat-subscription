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

//$COVERAGE-OFF$Disabling scoverage

package uk.gov.hmrc.vatsubscription.testonly.services

import javax.inject.{Inject,Singleton}

import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DatabaseAdminService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository
                                    )(implicit ec: ExecutionContext) {
  def deleteRecord(vatNumber: String): Future[Either[DatabaseCallFailure, DatabaseCallSuccess.type]] =
    subscriptionRequestRepository.deleteRecord(vatNumber) map {
      _ => Right(DatabaseCallSuccess)
    } recover {
      case _ => Left(DeleteRecordFailure)
    }
}

object DatabaseCallSuccess

sealed trait DatabaseCallFailure

case object DeleteRecordFailure extends DatabaseCallFailure

// $COVERAGE-ON$
