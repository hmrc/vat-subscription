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
import play.api.libs.json.{JsError, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.MatchUserHttpParser.MatchUserHttpReads

class MatchUserHttpParserSpec extends UnitSpec with EitherValues {
  "MatchUserHttpReads" when {
    "read" should {
      val testUrl = "/"
      val testMethod = "get"

      "return user details if authenticator response with OK and valid JSON" in {
        val testUserDetailsMatch = UserMatchSuccessResponseModel(nino = testNino)

        val httpResponse = HttpResponse(responseStatus = OK, responseJson = Some(Json.toJson(testUserDetailsMatch)))

        MatchUserHttpReads.read(testMethod, testUrl, httpResponse).right.value shouldBe Some(testNino)
      }

      "return none if authenticator returns an UNAUTHORIZED without an unexpected error" in {
        val httpResponse = HttpResponse(
          responseStatus = UNAUTHORIZED,
          responseJson = Some(Json.toJson(UserMatchFailureResponseModel(testErrorMsg)))
        )

        MatchUserHttpReads.read(testMethod, testUrl, httpResponse).right.value shouldBe empty
      }

      "return an error if authenticator returns an UNAUTHORIZED response that cannot be parsed" in {
        val json = Json.obj()

        val httpResponse = HttpResponse(
          responseStatus = UNAUTHORIZED,
          responseJson = Some(json)
        )

        val expectedJsError = json.validate[UserMatchFailureResponseModel].asInstanceOf[JsError]

        MatchUserHttpReads.read(testMethod, testUrl, httpResponse).left.value shouldBe UserMatchFailureResponseModel(expectedJsError)
      }

      "return an error if authenticator returns an UNAUTHORIZED response that contains an unexpected error" in {
        val json = Json.obj()

        val httpResponse = HttpResponse(
          responseStatus = UNAUTHORIZED,
          responseJson = Some(Json.toJson(UserMatchUnexpectedError))
        )

        MatchUserHttpReads.read(testMethod, testUrl, httpResponse).left.value shouldBe UserMatchUnexpectedError
      }

      "return error if authenticator returns another status" in {
        val httpResponse = HttpResponse(responseStatus = BAD_REQUEST)

        MatchUserHttpReads.read(testMethod, testUrl, httpResponse).left.value shouldBe UserMatchFailureResponseModel(httpResponse)
      }
    }
  }
}
