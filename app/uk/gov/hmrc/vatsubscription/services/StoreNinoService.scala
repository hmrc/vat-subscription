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
import uk.gov.hmrc.vatsubscription.connectors.AuthenticatorConnector
import uk.gov.hmrc.vatsubscription.models.UserDetailsModel
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository

import scala.concurrent.{ExecutionContext, Future}

class StoreNinoService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository,
                                 authenticatorConnector: AuthenticatorConnector
                                )(implicit ec: ExecutionContext) {

  def storeNino(vatNumber: String, userDetailsModel: UserDetailsModel)(implicit hc: HeaderCarrier): Future[Either[StoreNinoFailure, StoreNinoSuccess.type]] =
    matchUser(userDetailsModel) flatMap {
      case Right(nino) => storeNinoToMongo(vatNumber, nino)
      case Left(failure) => Future.successful(Left(failure))
    }

  private def matchUser(userDetailsModel: UserDetailsModel)(implicit hc: HeaderCarrier): Future[Either[UserMatchingFailure, String]] =
    authenticatorConnector.matchUser(userDetailsModel).map {
      case Right(Some(nino)) => Right(nino)
      case Right(None) => Left(NoMatchFoundFailure)
      case _ => Left(AuthenticatorFailure)
    }

  private def storeNinoToMongo(vatNumber: String, nino: String): Future[Either[MongoFailure, StoreNinoSuccess.type]] =
    subscriptionRequestRepository.upsertNino(vatNumber, nino) map {
      _ => Right(StoreNinoSuccess)
    } recover {
      case e: NoSuchElementException => Left(NinoDatabaseFailureNoVATNumber)
      case _ => Left(NinoDatabaseFailure)
    }

}

case object StoreNinoSuccess

sealed trait StoreNinoFailure

sealed trait UserMatchingFailure extends StoreNinoFailure

sealed trait MongoFailure extends StoreNinoFailure

case object AuthenticatorFailure extends UserMatchingFailure

case object NoMatchFoundFailure extends UserMatchingFailure

case object NinoDatabaseFailure extends MongoFailure

case object NinoDatabaseFailureNoVATNumber extends MongoFailure
