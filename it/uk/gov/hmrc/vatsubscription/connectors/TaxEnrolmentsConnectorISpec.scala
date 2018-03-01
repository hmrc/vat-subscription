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

import uk.gov.hmrc.vatsubscription.helpers.ComponentSpecBase
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.TaxEnrolmentsStub
import uk.gov.hmrc.vatsubscription.httpparsers.{FailedTaxEnrolment, SuccessfulTaxEnrolment}
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

class TaxEnrolmentsConnectorISpec extends ComponentSpecBase {

  lazy val connector: TaxEnrolmentsConnector = app.injector.instanceOf[TaxEnrolmentsConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "registerEnrolment" when {
    "Tax Enrolments returns a successful response" should {
      "return a SuccessfulTaxEnrolment" in {
        TaxEnrolmentsStub.stubRegisterEnrolment(testVatNumber, testSafeId)(NO_CONTENT)

        val res = connector.registerEnrolment(testVatNumber, testSafeId)

        await(res) shouldBe Right(SuccessfulTaxEnrolment)
      }
    }

    "Tax Enrolments returns a unsuceessful response" should {
      "return a FailedTaxEnrolment" in {
        TaxEnrolmentsStub.stubRegisterEnrolment(testVatNumber, testSafeId)(BAD_REQUEST)

        val res = connector.registerEnrolment(testVatNumber, testSafeId)

        await(res) shouldBe Left(FailedTaxEnrolment(BAD_REQUEST))
      }
    }
  }

}