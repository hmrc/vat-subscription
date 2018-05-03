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
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.{EmailStub, TaxEnrolmentsStub}
import uk.gov.hmrc.vatsubscription.httpparsers.SendEmailHttpParser.{EmailQueued, SendEmailFailure}
import uk.gov.hmrc.vatsubscription.httpparsers.{FailedTaxEnrolment, SuccessfulTaxEnrolment}

class EmailConnectorISpec extends ComponentSpecBase {

  lazy val connector: EmailConnector = app.injector.instanceOf[EmailConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "registerEnrolment" when {
    "Tax Enrolments returns a successful response" should {
      "return a SuccessfulTaxEnrolment" in {
        EmailStub.stubSendEmail(testEmail, testEmailTemplate)(NO_CONTENT)

        val res = connector.sendEmail(testEmail, testEmailTemplate)

        await(res) shouldBe Right(EmailQueued)
      }
    }

    "Tax Enrolments returns a unsuceessful response" should {
      "return a FailedTaxEnrolment" in {
        EmailStub.stubSendEmail(testEmail, testEmailTemplate)(BAD_REQUEST)

        val res = connector.sendEmail(testEmail, testEmailTemplate)

        await(res) shouldBe Left(SendEmailFailure(BAD_REQUEST, ""))
      }
    }
  }

}