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

import assets.TestUtil
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.vatsubscription.config.Constants._
import uk.gov.hmrc.vatsubscription.models.User


object BaseTestConstants extends TestUtil {

  val testVatNumber: String = UUID.randomUUID().toString
  val testArn: String = UUID.randomUUID().toString
  val testMtdVatEnrolment: Enrolment = Enrolment(MtdVatEnrolmentKey).withIdentifier(MtdVatReferenceKey, testVatNumber)
  val testAgentServicesEnrolment: Enrolment = Enrolment(AgentServicesEnrolment).withIdentifier(AgentServicesReference, testArn)

  val testCredentials: Credentials = Credentials("GG123456789", "GG")

  val testUser: User[_] = User(testVatNumber, None, testCredentials.providerId)(fakeRequest)
  val testAgentUser: User[_] = User(testVatNumber, Some(testArn), testCredentials.providerId)(fakeRequest)

  val testErrorMsg = "this is an error"

  val title = "0001"
  val orgName = "Ancient Antiques Ltd"
  val tradingName = "Dusty Relics"
  val firstName = "Fred"
  val lastName = "Flintstone"
  val middleName = "M"
  val effectiveDate = "1967-08-13"

}
