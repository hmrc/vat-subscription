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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class MandationStatusSpec extends AnyWordSpecLike with Matchers {

  "MandationStatus" when {

    "writer" when {

      "a MTDfB Mandated value is passed through" should {

        "return the correct value" in {
          MandationStatus.writer.writes(MTDfBMandated).validate[String].get shouldBe "MTDfB Mandated"
        }
      }

      "a MTDfB Voluntary value is passed through" should {

        "return the correct value" in {
          MandationStatus.writer.writes(MTDfBVoluntary).validate[String].get shouldBe "MTDfB Voluntary"
        }
      }

      "a Non MTDfB value is passed through" should {

        "return the correct value" in {
          MandationStatus.writer.writes(NonMTDfB).validate[String].get shouldBe "Non MTDfB"
        }
      }

      "a Non Digital value is passed through" should {

        "return the correct value" in {
          MandationStatus.writer.writes(NonDigital).validate[String].get shouldBe "Non Digital"
        }
      }

      "a MTDfBExempt value is passed through" should {

        "return the correct value" in {
          MandationStatus.writer.writes(MTDfBExempt).validate[String].get shouldBe "MTDfB Exempt"
        }
      }

      "a MTDfB value is passed through" should {

        "return the correct value" in {
          MandationStatus.writer.writes(MTDfB).validate[String].get shouldBe "MTDfB"
        }
      }
    }

    "desWriter" when {

      "a MTDfB Mandated value is passed through" should {

        "return the correct value" in {
          MandationStatus.desWriter.writes(MTDfBMandated).validate[String].get shouldBe "1"
        }
      }

      "a MTDfB Voluntary value is passed through" should {

        "return the correct value" in {
          MandationStatus.desWriter.writes(MTDfBVoluntary).validate[String].get shouldBe "2"
        }
      }

      "a Non MTDfB value is passed through" should {

        "return the correct value" in {
          MandationStatus.desWriter.writes(NonMTDfB).validate[String].get shouldBe "3"
        }
      }

      "a Non Digital value is passed through" should {

        "return the correct value" in {
          MandationStatus.desWriter.writes(NonDigital).validate[String].get shouldBe "4"
        }
      }

      "a MTDfBExempt value is passed through" should {

        "return the correct value" in {
          MandationStatus.desWriter.writes(MTDfBExempt).validate[String].get shouldBe "1"
        }
      }

      "a MTDfB value is passed through" should {

        "return the correct value" in {
          MandationStatus.desWriter.writes(MTDfB).validate[String].get shouldBe "2"
        }
      }
    }
  }
}
