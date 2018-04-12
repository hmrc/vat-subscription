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

import play.api.libs.json.JsString
import uk.gov.hmrc.play.test.UnitSpec


class MandationStatusSpec extends UnitSpec {

  "desReader" should {

    "parse '1' into  MTDfBMandated" in {
      val data = MandationStatus.desReader.reads(JsString("1")).get
      data shouldBe MTDfBMandated
    }
    "parse '2' into  MTDfBVoluntary" in {
      val data = MandationStatus.desReader.reads(JsString("2")).get
      data shouldBe MTDfBVoluntary
    }

    "parse '3' into  NonMTDfB" in {
      val data = MandationStatus.desReader.reads(JsString("3")).get
      data shouldBe NonMTDfB
    }
    "parse '4' into  NonDigital" in {
      val data = MandationStatus.desReader.reads(JsString("4")).get
      data shouldBe NonDigital
    }
  }

  "writer" should {
    "write the status correctly" in {
      MandationStatus.writer.writes(MTDfBMandated) shouldBe JsString(MTDfBMandated.Name)
      MandationStatus.writer.writes(MTDfBVoluntary) shouldBe JsString(MTDfBVoluntary.Name)
      MandationStatus.writer.writes(NonMTDfB) shouldBe JsString(NonMTDfB.Name)
      MandationStatus.writer.writes(NonDigital) shouldBe JsString(NonDigital.Name)
    }
  }

}
