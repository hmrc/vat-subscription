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
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._

class SignUpRequestSpec extends UnitSpec {

  "SignUpRequest" should {
    "convert the request into the correct DES format" in {
      val requestJson = Json.toJson(SignUpRequest(testSafeId, testVatNumber, testEmail, emailVerified = true))
      val expectedJson = Json.parse(
        s"""
           |{
           |  "signUpRequest": {
           |    "identification": [
           |      {
           |        "idType": "SAFEID",
           |        "idNumber": "$testSafeId"
           |      },
           |     {
           |        "idType": "VRN",
           |        "idNumber": "$testVatNumber"
           |      }
           |    ],
           |    "additionalInformation": [
           |      {
           |        "typeOfField": "EMAIL",
           |        "fieldContents": "$testEmail",
           |        "infoVerified": true
           |      }
           |    ]
           |  }
           |}
        """.stripMargin
      )
      requestJson shouldBe expectedJson
    }
  }

}
