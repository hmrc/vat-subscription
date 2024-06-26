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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PartyTypeSpec extends AnyWordSpecLike with Matchers {

  "Party Type apply method" when {

    "given a valid partyType" should {

      "for the latest release" should {
        PartyType.partyTypes.foreach { partyType =>
          s"return the correct party type for ${partyType.value}" in {
            PartyType.apply(PartyType.partyTypes)(partyType.value) shouldBe partyType
          }
        }
      }
    }

    "given an invalid partyType" should {
      "throw IllegalArgumentException(Invalid Charge Type)" in {
        val exception = intercept[IllegalArgumentException] {
          PartyType.apply(PartyType.partyTypes)("Bad Party Type")
        }
        exception.getMessage shouldBe "Invalid Party Type: Bad Party Type"
      }
    }
  }

  "Party Type unapply method" should  {

    "for the latest release" should {
      PartyType.partyTypes.foreach { partyType =>
        s"return the correct value for ${partyType.value}" in {
          PartyType.unapply(partyType) shouldBe partyType.value
        }
      }
    }
  }
}
