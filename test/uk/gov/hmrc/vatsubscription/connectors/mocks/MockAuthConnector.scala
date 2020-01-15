/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.connectors.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Suite}
import uk.gov.hmrc.auth.core.authorise.{EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants._

import scala.concurrent.{ExecutionContext, Future}

trait MockAuthConnector extends BeforeAndAfterEach with MockitoSugar {
  self: Suite =>

  val mockAuthConnector: AuthConnector = mock[AuthConnector]

  def mockAuthorise[T](predicate: Predicate = EmptyPredicate,
                       retrievals: Retrieval[T] = EmptyRetrieval
                      )(response: Future[T]): Unit = {
    when(
      mockAuthConnector.authorise(
        ArgumentMatchers.eq(predicate),
        ArgumentMatchers.eq(retrievals)
      )(
        ArgumentMatchers.any[HeaderCarrier],
        ArgumentMatchers.any[ExecutionContext])
    ) thenReturn response
  }

  val retrievals: Retrieval[Enrolments ~ Credentials] = Retrievals.allEnrolments and Retrievals.credentials

  def mockAuthRetrieveAgentServicesEnrolled(predicate: Predicate = EmptyPredicate): Unit =
    mockAuthorise(predicate, retrievals)(
      Future.successful(
        new ~(Enrolments(Set(testAgentServicesEnrolment)), testCredentials)
      )
    )

  def mockAuthRetrieveMtdVatEnrolled(predicate: Predicate = EmptyPredicate): Unit =
    mockAuthorise(predicate = predicate, retrievals = retrievals)(
      Future.successful(
        new ~(Enrolments(Set(testMtdVatEnrolment)), testCredentials)
      )
    )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuthConnector)
  }
}
