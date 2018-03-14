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

package uk.gov.hmrc.vatsubscription.repositories.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Suite}
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository

import scala.concurrent.{ExecutionContext, Future}

trait MockSubscriptionRequestRepository extends MockitoSugar with BeforeAndAfterEach {
  this: Suite =>
  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSubscriptionRequestRepository)
  }

  val mockSubscriptionRequestRepository: SubscriptionRequestRepository = mock[SubscriptionRequestRepository]

  def mockUpsertVatNumber(vatNumber: String)(response: Future[UpdateWriteResult]): Unit =
    when(mockSubscriptionRequestRepository.upsertVatNumber(ArgumentMatchers.eq(vatNumber)))
      .thenReturn(response)

  def mockUpsertCompanyNumber(vatNumber: String, companyNumber: String)(response: Future[UpdateWriteResult]): Unit =
    when(mockSubscriptionRequestRepository.upsertCompanyNumber(ArgumentMatchers.eq(vatNumber), ArgumentMatchers.eq(companyNumber)))
      .thenReturn(response)

  def mockUpsertEmail(vatNumber: String, email: String)(response: Future[UpdateWriteResult]): Unit =
    when(mockSubscriptionRequestRepository.upsertEmail(ArgumentMatchers.eq(vatNumber), ArgumentMatchers.eq(email)))
      .thenReturn(response)

  def mockUpsertNino(vatNumber: String, nino: String)(response: Future[UpdateWriteResult]): Unit =
    when(mockSubscriptionRequestRepository.upsertNino(ArgumentMatchers.eq(vatNumber), ArgumentMatchers.eq(nino)))
      .thenReturn(response)

  def mockUpsertIdentityVerified(vatNumber: String)(response: Future[UpdateWriteResult]): Unit =
    when(mockSubscriptionRequestRepository.upsertIdentityVerified(ArgumentMatchers.eq(vatNumber)))
      .thenReturn(response)

  def mockDeleteRecord(vatNumber: String)(response: Future[WriteResult]): Unit =
    when(mockSubscriptionRequestRepository.deleteRecord(ArgumentMatchers.eq(vatNumber)))
      .thenReturn(response)

  def mockFindById(vatNumber: String)(response: Future[Option[SubscriptionRequest]]): Unit =
    when(mockSubscriptionRequestRepository.findById(
      ArgumentMatchers.eq(vatNumber),
      ArgumentMatchers.any[ReadPreference]
    )(
      ArgumentMatchers.any[ExecutionContext]
    )) thenReturn response
}
