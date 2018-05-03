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

import uk.gov.hmrc.vatsubscription.config.Constants._
import uk.gov.hmrc.vatsubscription.utils.controllist.ControlListInformationParser.ControlListInformationIndices._

object IntegrationTestConstants {
  val testVatNumber: String = UUID.randomUUID().toString
  val testCompanyNumber: String = UUID.randomUUID().toString
  val testEmail: String = "test@test.test"
  val testAgentNumber: String = UUID.randomUUID().toString
  val testSafeId: String = UUID.randomUUID().toString
  val testNino: String = UUID.randomUUID().toString
  val testToken: String = UUID.randomUUID().toString
  val testJourneyLink = s"/mdtp/journey/journeyId/${UUID.randomUUID().toString}"
  val testEmailTemplate: String = "template"

  val testPostCode = "ZZ11 1ZZ"
  val testDateOfRegistration = "2017-01-01"

  object ControlList {
    val allFalse: String = "1" * CONTROL_INFORMATION_STRING_LENGTH
    val eligible: String = setupTestDataCore(allFalse)(STAGGER_1 -> '0', COMPANY -> '0')
    val ineligible: String = setupTestDataCore(allFalse)(ANNUAL_STAGGER -> '0', COMPANY -> '0')

    def setupTestData(amendments: (Int, Character)*): String = setupTestDataCore(eligible)(amendments: _*)

    private def setupTestDataCore(startString: String)(amendments: (Int, Character)*): String = {
      require(amendments.forall { case (index, _) => index >= 0 && index < CONTROL_INFORMATION_STRING_LENGTH })
      require(amendments.forall { case (_, newValue) => newValue == '0' || newValue == '1' })

      amendments.foldLeft[String](startString) {
        case (pre: String, (index: Int, value: Character)) =>
          pre.substring(0, index) + value + pre.substring(index + 1, pre.length)
      }
    }
  }

}
