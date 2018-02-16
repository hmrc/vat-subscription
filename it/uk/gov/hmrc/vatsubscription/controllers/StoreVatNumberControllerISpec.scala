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

import java.util.UUID

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._

class StoreVatNumberControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {
  val testVrn: String = UUID.randomUUID().toString

  "PUT /subscription-request/vat-number" should {
    "return no content when the vat number has been stored successfully" in {
      stubAuth(OK, successfulAuthResponse)

      val res = put("/subscription-request/vat-number")(Json.obj("vatNumber" -> testVrn))

      res should have(
        httpStatus(NO_CONTENT),
        emptyBody
      )
    }

    "return BAD_REQUEST when the json is invalid" in {
      stubAuth(OK, successfulAuthResponse)

      val res = put("/subscription-request/vat-number")(Json.obj())

      res should have(
        httpStatus(BAD_REQUEST)
      )
    }
  }
}
