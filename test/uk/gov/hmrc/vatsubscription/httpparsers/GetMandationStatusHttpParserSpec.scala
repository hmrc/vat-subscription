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
import uk.gov.hmrc.vatsubscription.httpparsers.GetMandationStatusHttpParser.GetMandationStatusHttpFailure
import uk.gov.hmrc.vatsubscription.httpparsers.GetMandationStatusHttpParser.GetMandationStatusHttpReads.read
import uk.gov.hmrc.vatsubscription.models.{MTDfBMandated, MTDfBVoluntary, NonDigital, NonMTDfB}

class GetMandationStatusHttpParserSpec extends UnitSpec with EitherValues {
  val testMethod = "GET"
  val testUrl = "/"

  "GetMandationStatusHttpReads#read" when {
    "the http status is OK" when {
      s"the json is $MTDfBMandated.toString" should {
        "return MTDfBMandated" in {
            val testResponse = HttpResponse(OK, Some(Json.obj("mandationStatus" -> MTDfBMandated.Name)))

            read(testMethod, testUrl, testResponse).right.value shouldBe MTDfBMandated
        }
      }
      s"the json is $MTDfBVoluntary.toString" should {
        "return MTDfBMandated" in {
          val testResponse = HttpResponse(OK, Some(Json.obj("mandationStatus" -> MTDfBVoluntary.Name)))

          read(testMethod, testUrl, testResponse).right.value shouldBe MTDfBVoluntary
        }
      }
      s"the json is $NonMTDfB.toString" should {
        "return MTDfBMandated" in {
          val testResponse = HttpResponse(OK, Some(Json.obj("mandationStatus" -> NonMTDfB.Name)))

          read(testMethod, testUrl, testResponse).right.value shouldBe NonMTDfB
        }
      }
      s"the json is $NonDigital.toString" should {
        "return MTDfBMandated" in {
          val testResponse = HttpResponse(OK, Some(Json.obj("mandationStatus" -> NonDigital.Name)))

          read(testMethod, testUrl, testResponse).right.value shouldBe NonDigital
        }
      }
      s"the json is invalid" should {
        "return GetMandationStatusHttpFailure" in {
          val testResponse = HttpResponse(OK, Some(Json.obj("mandationStatus" -> "invalid")))

          read(testMethod, testUrl, testResponse).left.value shouldBe GetMandationStatusHttpFailure(testResponse.status, testResponse.body)
        }
      }
    }
    "the http status is NOT_FOUND" should {
      "return VatNumberNotFound" in {
        val testResponse = HttpResponse(NOT_FOUND)

        read(testMethod, testUrl, testResponse).left.value shouldBe GetMandationStatusHttpParser.VatNumberNotFound
      }
    }

    "the http status is anything else" should {
      "return GetMandationStatusHttpFailure" in {
        val testResponse = HttpResponse(BAD_REQUEST)

        read(testMethod, testUrl, testResponse).left.value shouldBe GetMandationStatusHttpFailure(testResponse.status, testResponse.body)
      }
    }
  }

}
