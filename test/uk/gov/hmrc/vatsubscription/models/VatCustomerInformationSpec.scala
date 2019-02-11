/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.models

import assets.TestUtil
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.BankDetailsTestConstants.bankDetailsModelMax
import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.PPOBTestConstants.{email, ppobModelMax}


class VatCustomerInformationSpec extends TestUtil {

  ".pendingBankDetails method" when {

    "there are pending bank detail changes" should {

      "return the pending changes" in {
        customerInformationModelMax.pendingBankDetails shouldBe Some(bankDetailsModelMax)
      }
    }

    "there are NO pending bank detail changes" should {

      "return None" in {
        customerInformationModelMin.pendingBankDetails shouldBe None
      }
    }
  }

  ".pendingPPOBAddress method" when {

    "there is a pending PPOB Address change" should {

      "return the pending changes" in {
        customerInformationModelMax.pendingPPOBAddress shouldBe Some(ppobModelMax.address)
      }
    }

    "there is NO pending PPOB Address change" should {

      "return None" in {
        customerInformationModelMin.pendingPPOBAddress shouldBe None
      }
    }
  }


  ".pendingEmailAddress method" when {

    "there is a pending Email Address change" should {

      "return the pending changes" in {
        customerInformationModelMax.pendingContactEmail shouldBe Some(email)
      }
    }

    "there is NO pending Email Address change" should {

      "return None" in {
        customerInformationModelMin.pendingContactEmail shouldBe None
      }
    }
  }


  "currentReads" should {

    "parse the json correctly when all optional fields are populated" in {
      VatCustomerInformation.currentReads.reads(customerInformationDESJsonMaxV3_2_1).get shouldBe customerInformationModelMax
    }

    "parse the json correctly when no optional fields are returned" in {
      VatCustomerInformation.currentReads.reads(customerInformationDESJsonMin).get shouldBe customerInformationModelMin
    }
  }

  "newReads" should {
    "parse the json correctly when all optional fields are populated" in {
      VatCustomerInformation.newReads.reads(customerInformationDESJsonMaxV3_3).get shouldBe customerInformationModelMax
    }

    "parse the json correctly when no optional fields are returned" in {
      VatCustomerInformation.newReads.reads(customerInformationDESJsonMin).get shouldBe customerInformationModelMin
    }
  }

  it should {
    "parse the json correctly when all optional fields are populated for release 7" in {
      VatCustomerInformation.newReads.reads(customerInformationDESJsonMaxR7).get shouldBe customerInformationModelMaxR7
    }

    "parse the json correctly when no optional fields are returned for release 7" in {
      VatCustomerInformation.newReads.reads(customerInformationDESJsonMinR7).get shouldBe customerInformationModelMin
    }
  }

  "release 8 reads" when {

    "all optional fields are populated for release 8" should {

      "parse the json correctly" in {
        VatCustomerInformation.release8Reads.reads(customerInformationDESJsonMaxR8).get shouldBe customerInformationModelMaxR8
      }
    }

    "no optional fields are populated for release 8" should {

      "parse the json correctly" in {
        VatCustomerInformation.release8Reads.reads(customerInformationDESJsonMinR8).get shouldBe customerInformationModelMin
      }
    }

    "current and in-flight return periods are not valid" should {

      "exclude these return periods from response" in {
        VatCustomerInformation.release8Reads.reads(customerInformationDESJsonInvalidReturnPeriod).get.returnPeriod shouldBe None
        VatCustomerInformation.release8Reads.reads(customerInformationDESJsonInvalidReturnPeriod).get.pendingChanges.get.returnPeriod shouldBe None
      }
    }
  }

  "Manage Account writes" should {
    "write the json correctly when all optional fields are populated" in {
      Json.toJson(customerInformationModelMax)(VatCustomerInformation.manageAccountWrites) shouldBe manageAccountSummaryOutputJsonMax
    }

    "write the json correctly when no optional fields are populated" in {
      Json.toJson(customerInformationModelMin)(VatCustomerInformation.manageAccountWrites) shouldBe manageAccountSummaryOutputJsonMin
    }
  }
}
