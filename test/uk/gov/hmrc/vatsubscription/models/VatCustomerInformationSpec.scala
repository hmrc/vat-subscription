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

import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.CustomerInformationTestConstants._


class VatCustomerInformationSpec extends UnitSpec {

  "desReader" should {
    "parse the json correctly when all optional fields are populated" in {
      VatCustomerInformation.desReader.reads(customerInformationDESJsonMax).get shouldBe customerInformationModelMax
    }

    "parse the json correctly when no optional fields are returned" in {
      VatCustomerInformation.desReader.reads(customerInformationDESJsonMin).get shouldBe customerInformationModelMin
    }
  }
}
