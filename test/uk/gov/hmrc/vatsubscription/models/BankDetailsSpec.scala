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

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class BankDetailsSpec extends UnitSpec {

  private val accName = "**********************"
  private val accNum = "****1234"
  private val accSort = "12****"

  private val allFieldsInput = Json.obj(
    "accountHolderName" -> accName,
    "bankAccountNumber" -> accNum,
    "sortCode" -> accSort
  )

  private val noFields = Json.obj()

  "BankDetails Reads" should {
    "parse the json correctly when all optional fields are populated" in {
      val expected = BankDetails(Some(accName), Some(accNum), Some(accSort))
      BankDetails.bankReader.reads(allFieldsInput).get shouldBe expected
    }

    "parse the json correctly when no fields are supplied" in {
      val expected = BankDetails(None, None, None)
      BankDetails.bankReader.reads(noFields).get shouldBe expected
    }
  }

  "BankDetails Writes" should {

    "output a fully populated BankDetails object with all fields populated" in {
      val bankDetails = BankDetails(Some(accName), Some(accNum), Some(accSort))
      val output = BankDetails.bankWriter.writes(bankDetails)
      output shouldBe allFieldsInput
    }

    "an empty json object when an empty BankDetails object is marshalled" in {
      val bankDetails = BankDetails(None,None,None)
      val output = BankDetails.bankWriter.writes(bankDetails)
      output shouldBe noFields
    }
  }
}
