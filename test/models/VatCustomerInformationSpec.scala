/*
 * Copyright 2024 HM Revenue & Customs
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

package models

import play.api.libs.json.Json
import helpers.BankDetailsTestConstants.bankDetailsModelMax
import helpers.CustomerInformationTestConstants._
import helpers.PPOBTestConstants.{email, mobileNumber, phoneNumber, ppobModelMax}
import helpers.TestUtil

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

  ".pendingLandLine method" when {

    "there is a pending LandLine change" should {

      "return the pending changes" in {
        customerInformationModelMax.pendingLandLine shouldBe Some(phoneNumber)
      }
    }

    "there is NO pending LandLine change" should {

      "return None" in {
        customerInformationModelMin.pendingLandLine shouldBe None
      }
    }
  }

  ".pendingMobile method" when {

    "there is a pending Mobile change" should {

      "return the pending changes" in {
        customerInformationModelMax.pendingMobile shouldBe Some(mobileNumber)
      }
    }

    "there is NO pending Mobile change" should {

      "return None" in {
        customerInformationModelMin.pendingMobile shouldBe None
      }
    }
  }

  "the latest release reads" should {

    "parse the json correctly when all fields are present" in {
      VatCustomerInformation.reads(mockAppConfig).reads(customerInformationDESJsonMaxWithFRS).get shouldBe
        customerInformationModelMaxWithFRS
    }

    "parse the json correctly when there is the minimum number of fields present" in {
      VatCustomerInformation.reads(mockAppConfig).reads(customerInformationDESJsonMin).get shouldBe
        customerInformationModelMin
    }
  }

  "Writes" should {

    "write the json correctly when all optional fields are populated for the latest release" in {
      Json.toJson(customerInformationModelMaxWithFRS)(VatCustomerInformation.writes) shouldBe
        customerInformationOutputJsonMaxWithFRS
    }

    "write the json correctly when no optional fields are populated for the latest release" in {
      Json.toJson(customerInformationModelMin)(VatCustomerInformation.writes) shouldBe
        customerInformationOutputJsonMin
    }
  }
}
