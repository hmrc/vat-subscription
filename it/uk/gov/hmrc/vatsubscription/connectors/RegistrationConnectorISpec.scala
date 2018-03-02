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

package uk.gov.hmrc.vatsubscription.connectors

import org.scalatest.EitherValues
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.helpers.ComponentSpecBase
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.RegistrationStub
import uk.gov.hmrc.vatsubscription.httpparsers.RegisterWithMultipleIdsSuccess

class RegistrationConnectorISpec extends ComponentSpecBase with EitherValues {
  private lazy val registrationConnector: RegistrationConnector = app.injector.instanceOf[RegistrationConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "registerCompany" when {
    "DES returns a successful response" should {
      "return a RegistrationSuccess with the SAFE ID" in {
        RegistrationStub.stubRegisterCompany(testVatNumber, testCompanyNumber)(testSafeId)

        val res = await(registrationConnector.registerCompany(testVatNumber, testCompanyNumber))

        res.right.value shouldBe RegisterWithMultipleIdsSuccess(testSafeId)
      }
    }
  }

  "registerIndividual" when {
    "DES returns a successful response" should {
      "return a RegistrationSuccess with the SAFE ID" in {
        RegistrationStub.stubRegisterIndividual(testVatNumber, testNino)(testSafeId)

        val res = await(registrationConnector.registerIndividual(testVatNumber, testNino))

        res.right.value shouldBe RegisterWithMultipleIdsSuccess(testSafeId)
      }
    }
  }
}
