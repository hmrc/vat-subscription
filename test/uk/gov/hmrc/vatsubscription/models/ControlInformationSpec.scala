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
import uk.gov.hmrc.vatsubscription.models.ControlListInformation.{EntityConflict, InvalidFormat, StaggerConflict}


class ControlInformationSpec extends UnitSpec {

  // space added to make it easier to spot stagger 0, stagger 1 - 3 and business entities
  val valid = "1111111111111 1 111 110 01111111 1111".replaceAll(" ","")
  val businessEntityConflict = valid.substring(0, 20) + "00000000" + valid.substring(28, 32)
  val staggerConflict = valid.substring(0, 17) + "000" + valid.substring(20, 32)

  "ControlListInformation.tryParse" should {
    "parse into a ControlListInformation object" when {
      "the string is valid" in {
        println(ControlListInformation.tryParse(valid))
        ControlListInformation.tryParse(valid).isRight shouldBe true
      }
    }

    "fail and return InvalidFormat" when {
      "the string is not exactly 32 characters long" in {
        ControlListInformation.tryParse(valid.drop(1)) shouldBe Left(InvalidFormat)
      }
      "the string does not represent a binary number" in {
        ControlListInformation.tryParse(valid.replaceFirst("1", "2")) shouldBe Left(InvalidFormat)
      }
    }
    "fail and return EntityConflict" when {
      "multiple business entity is defined" in {
        ControlListInformation.tryParse(businessEntityConflict) shouldBe Left(EntityConflict)
      }
    }
    "fail and return StaggerConflict" when {
      "multiple stagger is defined" in {
        ControlListInformation.tryParse(staggerConflict) shouldBe Left(StaggerConflict)
      }
    }
  }

}
