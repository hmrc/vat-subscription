/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.play.test.UnitSpec
import helpers.CustomerDetailsTestConstants._

class CustomerDetailsSpec extends UnitSpec {

  "CustomerDetails Reads" should {
    "parse the json correctly when all optional fields are populated for the latest release" in {
      CustomerDetails.cdReader.reads(customerDetailsJsonMax).get shouldBe customerDetailsModelMax
    }

    "parse the json correctly when no fields are supplied for the latest release" in {
      CustomerDetails.cdReader.reads(customerDetailsJsonMin).get shouldBe customerDetailsModelMin
    }
  }

  "CustomerDetails Writes" should {
    "output a fully populated CustomerDetails object with all fields populated for the latest release" in {
      CustomerDetails.cdWriter.writes(customerDetailsModelMax) shouldBe customerDetailsJsonMax
    }

    "an empty json object when an empty CustomerDetails object is marshalled for the latest release" in {
      CustomerDetails.cdWriter.writes(customerDetailsModelMin) shouldBe customerDetailsJsonMin
    }
  }
}
