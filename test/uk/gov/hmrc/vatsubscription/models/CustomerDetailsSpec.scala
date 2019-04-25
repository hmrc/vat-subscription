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

import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.CustomerDetailsTestConstants._

class CustomerDetailsSpec extends UnitSpec {

  "CustomerDetails Reads" should {
    "parse the json correctly when all optional fields are populated" in {
      CustomerDetails.cdReader.reads(customerDetailsJsonMax).get shouldBe customerDetailsModelMax
    }

    "parse the json correctly when no fields are supplied" in {
      CustomerDetails.cdReader.reads(customerDetailsJsonMin).get shouldBe customerDetailsModelMin
    }
  }

  "CustomerDetails Writes" should {
    "output a fully populated CustomerDetails object with all fields populated" in {
      CustomerDetails.cdWriter.writes(customerDetailsModelMax) shouldBe customerDetailsJsonMax
    }

    "an empty json object when an empty CustomerDetails object is marshalled" in {
      CustomerDetails.cdWriter.writes(customerDetailsModelMin) shouldBe customerDetailsJsonMin
    }
  }
}