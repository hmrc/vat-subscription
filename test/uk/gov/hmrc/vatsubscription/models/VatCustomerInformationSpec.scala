/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.assets.TestUtil
import uk.gov.hmrc.vatsubscription.helpers.BankDetailsTestConstants.bankDetailsModelMax
import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.PPOBTestConstants.{email, mobileNumber, phoneNumber, ppobModelMax}

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

  "release 10 reads" when {

    "the newStatusIndicators feature is on" should {

      "parse the json correctly" in {
        VatCustomerInformation.release10Reads(mockAppConfig).reads(customerInformationDESJsonMaxWithFRS).get shouldBe
          customerInformationModelMaxWithFRS
      }
    }

    "the newStatusIndicators feature is off" should {

      val model = customerInformationModelMaxWithFRS.copy(
        mandationStatus = MTDfBVoluntary,
        pendingChanges = Some(PendingChanges(
          Some(ppobModelMax),
          Some(bankDetailsModelMax),
          Some(MCReturnPeriod(None, None, None)),
          Some(MTDfBVoluntary)
        ))
      )

      "parse the json correctly" in {
        mockAppConfig.features.newStatusIndicators(false)
        VatCustomerInformation.release10Reads(mockAppConfig).reads(customerInformationDESJsonMaxWithFRS).get shouldBe
          model
      }
    }
  }

  "release 8 reads" when {

    "all optional fields are populated for release 8" should {

      "parse the json correctly" in {
        VatCustomerInformation.release8Reads(mockAppConfig).reads(customerInformationDESJsonMaxR8).get shouldBe
          customerInformationModelMaxR8
      }
    }

    "no optional fields are populated for release 8" should {

      "parse the json correctly" in {
        VatCustomerInformation.release8Reads(mockAppConfig).reads(customerInformationDESJsonMinR8).get  shouldBe
          customerInformationModelMinR8
      }
    }

    "current and in-flight return periods are not valid" should {

      "exclude these return periods from response" in {
        VatCustomerInformation.release8Reads(mockAppConfig)
          .reads(customerInformationDESJsonInvalidReturnPeriod).get.returnPeriod shouldBe None
        VatCustomerInformation.release8Reads(mockAppConfig)
          .reads(customerInformationDESJsonInvalidReturnPeriod).get.pendingChanges shouldBe None
      }
    }
  }

  "Writes" should {

    "write the json correctly when all optional fields are populated for release 10" in {
      Json.toJson(customerInformationModelMaxWithFRS)(VatCustomerInformation.writes(true)) shouldBe
        customerInformationOutputJsonMaxWithFRS
    }

    "write the json correctly when all optional fields are populated for release 8 (no overseas indicator in Json)" in {
      Json.toJson(customerInformationModelMaxR8)(VatCustomerInformation.writes(false)) shouldBe
        customerInformationOutputJsonMaxR8
    }

    "write the json correctly when no optional fields are populated for release 10" in {
      Json.toJson(customerInformationModelMin)(VatCustomerInformation.writes(true)) shouldBe
        customerInformationOutputJsonMin
    }

    "write the json correctly when no optional fields are populated for release 8 (no overseas indicator in Json)" in {
      Json.toJson(customerInformationModelMinR8)(VatCustomerInformation.writes(false)) shouldBe
        customerInformationOutputJsonMinR8
    }
  }

  "Manage Account writes" should {

    "write the json correctly when all optional fields are populated for release 10" in {
      Json.toJson(manageAccountModelMax)(VatCustomerInformation.manageAccountWrites(true)) shouldBe
        manageAccountSummaryOutputJsonMax
    }

    "write the json correctly when all optional fields are populated for release 8 (no overseas indicator in Json)" in {
      Json.toJson(manageAccountModelMax)(VatCustomerInformation.manageAccountWrites(false)) shouldBe
        manageAccountSummaryOutputJsonMaxR8
    }

    "write the json correctly when no optional fields are populated for release 10" in {
      Json.toJson(customerInformationModelMin)(VatCustomerInformation.manageAccountWrites(true)) shouldBe
        manageAccountSummaryOutputJsonMin
    }

    "write the json correctly when no optional fields are populated for release 8 (no overseas indicator in Json)" in {
      Json.toJson(customerInformationModelMinR8)(VatCustomerInformation.manageAccountWrites(false)) shouldBe
        manageAccountSummaryOutputJsonMinR8
    }
  }
}
