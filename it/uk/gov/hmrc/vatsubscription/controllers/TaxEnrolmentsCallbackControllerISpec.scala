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
import uk.gov.hmrc.vatsubscription.helpers.{ComponentSpecBase, CustomMatchers}

class TaxEnrolmentsCallbackControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  "/subscription-request/vat-number/callback" when {
    "callback is successful" should {
      "return NO_CONTENT with the status" in {

        val res = post(s"/subscription-request/vat-number/$testVatNumber/callback")(Json.obj("vatNumber" -> testVatNumber))
        res should have(
          httpStatus(NO_CONTENT)
        )
      }
    }

  }
}
