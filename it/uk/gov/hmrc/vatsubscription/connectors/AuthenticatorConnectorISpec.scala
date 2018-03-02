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

import java.time.LocalDate
import java.util.UUID

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.helpers.ComponentSpecBase
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthenticatorStub
import uk.gov.hmrc.vatsubscription.httpparsers.UserMatchUnexpectedError
import uk.gov.hmrc.vatsubscription.models.UserDetailsModel

class AuthenticatorConnectorISpec extends ComponentSpecBase {

  lazy val connector: AuthenticatorConnector = app.injector.instanceOf[AuthenticatorConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  val testUserDetails = UserDetailsModel(
    firstName = UUID.randomUUID().toString,
    lastName = UUID.randomUUID().toString,
    dateOfBirth = LocalDate.now(),
    nino = testNino
  )

  "matchUser" when {
    "Tax Enrolments returns a matched response" should {
      "return the nino returned" in {
        AuthenticatorStub.stubMatchUser(testUserDetails)(matched = true)

        val res = connector.matchUser(testUserDetails)

        await(res) shouldBe Right(Some(testNino))
      }
    }

    "Tax Enrolments returns a not matched response" should {
      "return the nino returned" in {
        AuthenticatorStub.stubMatchUser(testUserDetails)(matched = false)

        val res = connector.matchUser(testUserDetails)

        await(res) shouldBe Right(None)
      }
    }

    "Tax Enrolments returns a unsuceessful response" should {
      "return a UserMatchFailureResponseModel" in {
        AuthenticatorStub.stubMatchUserFailure(testUserDetails)

        val res = connector.matchUser(testUserDetails)

        await(res) shouldBe Left(UserMatchUnexpectedError)
      }
    }
  }

}