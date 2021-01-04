/*
 * Copyright 2021 HM Revenue & Customs
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

package models.updateVatSubscription.request

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class UpdatedOrganisationDetailsSpec extends UnitSpec {

  "UpdatedOrganisationDetails Writes" should {

    val model: UpdatedOrganisationDetails = UpdatedOrganisationDetails(
      Some("Liverpool magic"),
      Some(IndividualName(Some("Archmage"), Some("Steven"), None, Some("Gerrard"))),
      Some("Stevies Spells"))

    "output a correctly formatted UpdatedOrganisationDetails json" in {
      val result = Json.obj(
        "organisationName" -> "Liverpool magic",
        "individualName" -> Json.obj(
          "title" -> "Archmage",
          "firstName" -> "Steven",
          "lastName" -> "Gerrard"
        ),
        "tradingName" -> "Stevies Spells"
      )

      UpdatedOrganisationDetails.writes.writes(model) shouldBe result
    }
  }
}
