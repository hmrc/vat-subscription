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

import java.util.NoSuchElementException
import javax.inject.{Inject,Singleton}

import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StoreCompanyNumberService @Inject()(subscriptionRequestRepository: SubscriptionRequestRepository
                                         )(implicit ec: ExecutionContext) {
  def storeCompanyNumber(vatNumber: String, companyNumber: String): Future[Either[StoreCompanyNumberFailure, StoreCompanyNumberSuccess.type]] =
    subscriptionRequestRepository.upsertCompanyNumber(vatNumber, companyNumber) map {
      _ => Right(StoreCompanyNumberSuccess)
    } recover {
      case e: NoSuchElementException => Left(CompanyNumberDatabaseFailureNoVATNumber)
      case _ => Left(CompanyNumberDatabaseFailure)
    }
}

object StoreCompanyNumberSuccess

sealed trait StoreCompanyNumberFailure

object CompanyNumberDatabaseFailure extends StoreCompanyNumberFailure

object CompanyNumberDatabaseFailureNoVATNumber extends StoreCompanyNumberFailure

