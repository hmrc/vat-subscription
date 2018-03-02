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
import uk.gov.hmrc.vatsubscription.helpers.{ComponentSpecBase, CustomMatchers}
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.EmailVerificationStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.RegistrationStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.SignUpStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.TaxEnrolmentsStub._
import scala.concurrent.ExecutionContext.Implicits.global

class SignUpSubmissionControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {
  override def beforeEach: Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  private val repo = app.injector.instanceOf[SubscriptionRequestRepository]

  private val testSubscriptionRequest = SubscriptionRequest(
    vatNumber = testVatNumber,
    companyNumber = Some(testCompanyNumber),
    email = Some(testEmail)
  )

  "/subscription-request/vat-number/:vatNumber/submit" when {
    "all downstream services behave as expected" should {
      "return NO_CONTENT" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetEmailVerified(testEmail)
        stubRegisterCompany(testVatNumber, testCompanyNumber)(testSafeId)
        stubSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(OK)
        stubRegisterEnrolment(testVatNumber, testSafeId)(NO_CONTENT)

        await(repo.insert(testSubscriptionRequest))
        val res = await(post(s"/subscription-request/vat-number/$testVatNumber/submit")(Json.obj()))

        res should have(
          httpStatus(NO_CONTENT),
          emptyBody
        )
      }
    }
  }
}
