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

package uk.gov.hmrc.vatsubscription.controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.IdentityVerificationStub.stubGetIdentityVerifiedOutcome
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository


import scala.concurrent.ExecutionContext.Implicits.global

class StoreIdentityVerificationOutcomeControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val repo: SubscriptionRequestRepository = app.injector.instanceOf[SubscriptionRequestRepository]

  override def beforeEach: Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  "PUT /subscription-request/vat-number/:vatNumber/identity-verification-outcome" when {
      "the identity has been successfully verified" should {
        "return NoContent" in {
          stubAuth(OK, successfulAuthResponse())

          repo.insertVatNumber(testVatNumber)
          stubGetIdentityVerifiedOutcome(testJourneyLink)("Success")

          val res = post(s"/subscription-request/vat-number/$testVatNumber/identity-verification")(Json.obj("journeyLink" -> testJourneyLink))

          res should have(
            httpStatus(NO_CONTENT)
          )
        }
    }

    "the identity has been not been successfully verified" should {
      "return Forbidden" in {
        stubAuth(OK, successfulAuthResponse())

        repo.insertVatNumber(testVatNumber)
        stubGetIdentityVerifiedOutcome(testJourneyLink)("Incomplete")

        val res = post(s"/subscription-request/vat-number/$testVatNumber/identity-verification")(Json.obj("journeyLink" -> testJourneyLink))

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "the vat number does not exist" should {
      "return InternServerError" in {
        stubAuth(OK, successfulAuthResponse())

        stubGetIdentityVerifiedOutcome(testJourneyLink)("Success")

        val res = post(s"/subscription-request/vat-number/$testVatNumber/identity-verification")(Json.obj("journeyLink" -> testJourneyLink))
        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }

    "the json is invalid" should {
      "return BAD_REQUEST" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post(s"/subscription-request/vat-number/$testVatNumber/identity-verification")(Json.obj())

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }
  }

}
