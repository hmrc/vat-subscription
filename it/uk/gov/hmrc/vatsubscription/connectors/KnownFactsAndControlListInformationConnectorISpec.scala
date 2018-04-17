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
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.KnownFactsAndControlListInformationStub
import uk.gov.hmrc.vatsubscription.httpparsers.{ControlListInformationVatNumberNotFound, KnownFactsInvalidVatNumber}
import uk.gov.hmrc.vatsubscription.models.KnownFactsAndControlListInformation

class KnownFactsAndControlListInformationConnectorISpec extends ComponentSpecBase with EitherValues {

  private lazy val KnownFactsAndControlListInformationConnector: KnownFactsAndControlListInformationConnector =
    app.injector.instanceOf[KnownFactsAndControlListInformationConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "getKnownFactsAndControlListInformation" when {
    "DES returns a successful response" should {
      "return the known facts and control list information" in {
        KnownFactsAndControlListInformationStub.stubSuccessGetKnownFactsAndControlListInformation(testVatNumber)

        val res = await(KnownFactsAndControlListInformationConnector.getKnownFactsAndControlListInformation(testVatNumber))

        res.right.value shouldBe KnownFactsAndControlListInformation(Some(testPostCode),Some(testDateOfRegistration), testControlListInformation)
      }
    }
  }

  "getKnownFactsAndControlListInformation" when {
    "DES returns a BAD_REQUEST" should {
      "return a KnownFactsInvalidVatNumber" in {
        KnownFactsAndControlListInformationStub.stubFailureKnownFactsInvalidVatNumber(testVatNumber)

        val res = await(KnownFactsAndControlListInformationConnector.getKnownFactsAndControlListInformation(testVatNumber))

        res.left.value shouldBe KnownFactsInvalidVatNumber
      }
    }
  }

  "getKnownFactsAndControlListInformation" when {
    "DES returns a NOT_FOUND" should {
      "return a ControlListInformationVatNumberNotFound" in {
        KnownFactsAndControlListInformationStub.stubFailureControlListVatNumberNotFound(testVatNumber)

        val res = await(KnownFactsAndControlListInformationConnector.getKnownFactsAndControlListInformation(testVatNumber))

        res.left.value shouldBe ControlListInformationVatNumberNotFound
      }
    }
  }
}
