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

import uk.gov.hmrc.vatsubscription.repositories.EmailRequestRepository

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class StoreTemporarySubscriptionDataService @Inject()(emailRequestRepository: EmailRequestRepository
                                                     )(implicit ec: ExecutionContext)
{

  import StoreTemporarySubscriptionDataService._

  def storeTemporarySubscriptionData(vatNumber: String, email: String): Future[Either[StoreTemporarySubscriptionDataFailure,
    StoreTemporarySubscriptionDataSuccess.type]] = {
    emailRequestRepository.upsertEmail(vatNumber, email) map {
      _ => Right(StoreTemporarySubscriptionDataSuccess)
    } recover {
     case _ =>
        Left(StoreTemporarySubscriptionDataDatabaseFailure)
    }
  }
}


object StoreTemporarySubscriptionDataService {

  case object StoreTemporarySubscriptionDataSuccess

  sealed trait StoreTemporarySubscriptionDataFailure

  case object StoreTemporarySubscriptionDataDatabaseFailure extends StoreTemporarySubscriptionDataFailure
}