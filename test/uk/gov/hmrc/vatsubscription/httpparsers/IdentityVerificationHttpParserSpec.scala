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

package uk.gov.hmrc.vatsubscription.httpparsers

import org.scalatest.EitherValues
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.TestConstants.testToken
import uk.gov.hmrc.vatsubscription.httpparsers.IdentityVerificationHttpParser.GetIdentityVerificationOutcomeHttpReads.read
import uk.gov.hmrc.vatsubscription.httpparsers.IdentityVerificationHttpParser._

class IdentityVerificationHttpParserSpec extends UnitSpec with EitherValues {

  "GetIdentityVerificationOutcome#read" when {
    "the response status is OK" should {
      "return IdentityVerified" when {
        "the result code is 'Success'" in {
          val httpResponse = HttpResponse(
            responseStatus = OK,
            responseJson = Some(Json.obj("result" -> SuccessCode,
                                         "token" -> testToken))
          )

          read("", "", httpResponse).right.value shouldBe IdentityVerified
        }
      }

      "return IdentityNotVerified" when {
        "the result code is not 'Success'" in {
          val notVerifiedResultCode = "Incomplete"

          val resultCode = "Incomplete"
          val httpResponse = HttpResponse(
            responseStatus = OK,
            responseJson = Some(Json.obj("result" -> notVerifiedResultCode,
                                         "token" -> testToken))
          )

          read("", "", httpResponse).right.value shouldBe IdentityNotVerified(resultCode)
        }
      }
    }

    "the response status is not OK" should {
      "return IdentityVerificationOutcomeErrorResponse" in {
        val httpResponse = HttpResponse(
          responseStatus = BAD_REQUEST
        )

        read("", "", httpResponse).left.value shouldBe IdentityVerificationOutcomeErrorResponse(BAD_REQUEST, httpResponse.body)
      }
    }
  }
}
