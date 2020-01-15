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

import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.CustomerDetailsTestConstants._

class CustomerDetailsSpec extends UnitSpec {

  "CustomerDetails Reads" should {
    "parse the json correctly when all optional fields are populated for release 10" in {
      CustomerDetails.cdReaderR10.reads(customerDetailsJsonMax).get shouldBe customerDetailsModelMax
    }

    "parse the json correctly when all optional fields are populated for release 8" in {
      CustomerDetails.cdReaderR8.reads(customerDetailsJsonMax).get shouldBe customerDetailsModelMaxR8
    }

    "parse the json correctly when no fields are supplied for release 10" in {
      CustomerDetails.cdReaderR10.reads(customerDetailsJsonMin).get shouldBe customerDetailsModelMin
    }

    "parse the json correctly when no fields are supplied for release 8" in {
      CustomerDetails.cdReaderR8.reads(customerDetailsJsonMin).get shouldBe customerDetailsModelMinR8
    }

    "parse the json and set overseas indicator to false, if true in Json, if in release 8" in {
      CustomerDetails.cdReaderR8.reads(customerDetailsJsonMinWithTrueOverseas).get shouldBe customerDetailsModelMinR8
    }
  }

  "CustomerDetails Writes" should {
    "output a fully populated CustomerDetails object with all fields populated for release 10" in {
      CustomerDetails.cdWriter(true).writes(customerDetailsModelMax) shouldBe customerDetailsJsonMax
    }

    "output a fully populated CustomerDetails object with all fields populated for release 8 (no overseas indicator in written Json)" in {
      CustomerDetails.cdWriter(false).writes(customerDetailsModelMax) shouldBe customerDetailsJsonMaxR8
    }

    "an empty json object when an empty CustomerDetails object is marshalled for release 10" in {
      CustomerDetails.cdWriter(true).writes(customerDetailsModelMin) shouldBe customerDetailsJsonMin
    }

    "an empty json object when an empty CustomerDetails object is marshalled for release 8 (no overseas indicator in written Json)" in {
      CustomerDetails.cdWriter(false).writes(customerDetailsModelMin) shouldBe customerDetailsJsonMinR8
    }
  }
}
