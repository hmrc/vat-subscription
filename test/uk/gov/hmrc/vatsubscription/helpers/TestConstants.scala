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

package uk.gov.hmrc.vatsubscription.helpers

import java.util.UUID

import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.vatsubscription.config.Constants.{AgentEnrolmentKey, AgentReferenceNumberKey, VATEnrolmentKey, VATReferenceKey}

object TestConstants {
  val testVatNumber: String = UUID.randomUUID().toString
  val testNino: String = UUID.randomUUID().toString
  val testCompanyNumber: String = UUID.randomUUID().toString
  val testEmail: String = UUID.randomUUID().toString
  val testAgentReferenceNumber: String = UUID.randomUUID().toString
  val testSafeId: String = UUID.randomUUID().toString
  val testToken =  UUID.randomUUID().toString
  val testJourneyLink = s"/mdtp/journey/journeyId/${UUID.randomUUID().toString}"

  val testAgentEnrolment: Enrolment = Enrolment(AgentEnrolmentKey).withIdentifier(AgentReferenceNumberKey, testAgentReferenceNumber)
  val testPrincipalEnrolment: Enrolment = Enrolment(VATEnrolmentKey).withIdentifier(VATReferenceKey, testVatNumber)

  val testErrorMsg = "this is an error"

}
