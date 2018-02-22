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
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository
import scala.concurrent.ExecutionContext.Implicits.global

class StoreCompanyNumberControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val repo: SubscriptionRequestRepository = app.injector.instanceOf[SubscriptionRequestRepository]

  override def beforeEach: Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  "PUT /subscription-request/company-number" should {
    "if vat number exists return no content when the company number has been stored successfully" in {
      stubAuth(OK, successfulAuthResponse)

      repo.insertVatNumber(testVatNumber)

      val res = put(s"/subscription-request/vat-number/$testVatNumber/company-number")(Json.obj("companyNumber" -> testCompanyNumber))

      res should have(
        httpStatus(NO_CONTENT),
        emptyBody
      )
    }

    "if the vat number does not already exist then return NOT_FOUND" in {
      stubAuth(OK, successfulAuthResponse)

      val res = put(s"/subscription-request/vat-number/$testVatNumber/company-number")(Json.obj("companyNumber" -> testCompanyNumber))

      res should have(
        httpStatus(NOT_FOUND)
      )
    }

    "return BAD_REQUEST when the json is invalid" in {
      stubAuth(OK, successfulAuthResponse)

      val res = put(s"/subscription-request/vat-number/$testVatNumber/company-number")(Json.obj())

      res should have(
        httpStatus(BAD_REQUEST)
      )
    }
  }

}
