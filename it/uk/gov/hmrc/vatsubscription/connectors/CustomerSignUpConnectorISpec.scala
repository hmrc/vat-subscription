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

import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.helpers.ComponentSpecBase
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.SignUpStub._
import uk.gov.hmrc.vatsubscription.models.CustomerSignUpResponseSuccess

class CustomerSignUpConnectorISpec extends ComponentSpecBase {

  lazy val connector: CustomerSignUpConnector = app.injector.instanceOf[CustomerSignUpConnector]

  "customerSignUp" should {
    "add the additional headers to des" in {
      implicit val hc = HeaderCarrier()
      stubSignUp(OK)

      val res = connector.signUp(testSafeId, testVatNumber, testEmail, emailVerified = true)

      await(res) shouldBe Right(CustomerSignUpResponseSuccess)
    }
  }

}
