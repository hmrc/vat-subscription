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

package models.post

import play.api.libs.json.Json
import helpers.PPOBTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class EmailPostSpec extends AnyWordSpecLike with Matchers {

  "EmailPost Reads" should {
    "parse the json correctly" in {
      emailPostJson.as[EmailPost] shouldBe emailPostValue}
  }

  "EmailsPost Writes" should {
    "output a populated PPOBAddressGet model" in {
      Json.toJson(emailPostValue) shouldBe emailPostWriteResult
    }
  }

}
