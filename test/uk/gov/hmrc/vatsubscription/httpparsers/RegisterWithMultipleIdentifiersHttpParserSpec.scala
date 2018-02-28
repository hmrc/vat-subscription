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

import uk.gov.hmrc.play.test.UnitSpec
import RegisterWithMultipleIdentifiersHttpParser.RegisterWithMultipleIdentifiersHttpReads.read
import org.scalatest.EitherValues
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.vatsubscription.config.Constants.Des._
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._

class RegisterWithMultipleIdentifiersHttpParserSpec extends UnitSpec with EitherValues {
  "RegisterWithMultipleIdentifiersHttpReads#read" when {
    "the response status is OK" when {
      "the JSON body is correctly formatted" should {
        "return a RegistrationSuccess with the returned SAFE ID" in {
          val httpResponse = HttpResponse(
            responseStatus = OK,
            responseJson = Some(Json.obj(IdentificationKey -> Json.arr(
              Json.obj(
                IdTypeKey -> SafeIdKey,
                IdValueKey -> testSafeId
              )
            )))
          )

          read("", "", httpResponse).right.value shouldBe RegistrationSuccess(testSafeId)
        }
      }
      "the JSON body is not correctly formatted" should {
        "return an InvalidJsonResponse" in {
          val httpResponse = HttpResponse(
            responseStatus = OK,
            responseJson = Some(Json.obj())
          )

          read("", "", httpResponse).left.value shouldBe an[InvalidJsonResponse]
        }
      }
    }

    "the response status is not OK" should {
      "return a RegistrationErrorResponse" in {
        val httpResponse = HttpResponse(
          responseStatus = BAD_REQUEST
        )

        read("", "", httpResponse).left.value shouldBe RegistrationErrorResponse(BAD_REQUEST, httpResponse.body)
      }
    }
  }
}
