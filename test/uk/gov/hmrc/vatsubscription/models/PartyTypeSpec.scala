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

class PartyTypeSpec extends UnitSpec {

  "Party Type apply method" when {

    "given a valid partyType" should {

      "for release 7" should {
        PartyType.r7PartyTypes.foreach { partyType =>
          s"return the correct party type for ${partyType.value}" in {
            PartyType.apply(PartyType.r7PartyTypes)(partyType.value) shouldBe partyType
          }

        }
      }

      "for release 8" should {
        PartyType.r8PartyTypes.foreach { partyType =>
          s"return the correct party type for ${partyType.value}" in {
            PartyType.apply(PartyType.r8PartyTypes)(partyType.value) shouldBe partyType
          }

        }
      }
    }

    "given an invalid partyType" should {
      "throw IllegalArgumentException(Invalid Charge Type)" in {
        val exception = intercept[IllegalArgumentException] {
          PartyType.apply(PartyType.r7PartyTypes)("Bad Party Type")
        }
        exception.getMessage shouldBe "Invalid Party Type"
      }
    }
  }

  "Party Type unapply method" should  {

    "for release 7" should {
      PartyType.r7PartyTypes.foreach { partyType =>
        s"return the correct value for ${partyType.value}" in {
          PartyType.unapply(partyType) shouldBe partyType.value
        }
      }
    }

    "for release 8" should {
      PartyType.r8PartyTypes.foreach { partyType =>
        s"return the correct value for ${partyType.value}" in {
          PartyType.unapply(partyType) shouldBe partyType.value
        }
      }
    }
  }

  "Party Type .isValidPartyType" when {

    "given an invalid charge type" should {

      "return false" in {
        PartyType.isValidPartyType("Z123456", PartyType.r7PartyTypes) shouldBe false
      }
    }

    "given a valid charge type" should {

      "return true" in {
        PartyType.isValidPartyType("Z1", PartyType.r8PartyTypes) shouldBe true
      }
    }
  }

}
