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

import helpers.MandationStatusTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class MandationStatusPostSpec extends AnyWordSpecLike with Matchers {

  "MandationStatusPost reads" should {

    "read a mandation status with an agent email address" in {
      mandationStatusPostAgentJson.as[MandationStatusPost] shouldBe mandationStatusPostAgent
    }

    "read a mandation status without an agent email address" in {
      mandationStatusPostJson.as[MandationStatusPost] shouldBe mandationStatusPost
    }
  }

}
