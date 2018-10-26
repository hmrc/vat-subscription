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

package uk.gov.hmrc.vatsubscription.models

import assets.TestUtil
import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants._


class VatCustomerInformationSpec extends TestUtil {

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
}
