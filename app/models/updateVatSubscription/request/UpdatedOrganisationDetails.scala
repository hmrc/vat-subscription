/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.{Json, Writes}

case class UpdatedOrganisationDetails(organisationName: Option[String],
                                      individualName: Option[IndividualName],
                                      tradingName: Option[String])

object UpdatedOrganisationDetails {

  implicit val writes: Writes[UpdatedOrganisationDetails] = Writes {
    model => Json.obj(
      "organisationName" -> model.organisationName,
      "individualName" -> model.individualName,
      "tradingName" -> model.tradingName
    )
  }

}

case class IndividualName(title: Option[String], firstName: Option[String], middleName: Option[String], lastName: Option[String])

object IndividualName {

  implicit val writes: Writes[IndividualName] = Writes {
    model => Json.obj(
      "title" -> model.title,
      "firstName" -> model.firstName,
      "middleName" -> model.middleName,
      "lastName" -> model.lastName
    )
  }

}
