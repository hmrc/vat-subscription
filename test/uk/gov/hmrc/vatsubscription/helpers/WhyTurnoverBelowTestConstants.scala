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

package uk.gov.hmrc.vatsubscription.helpers

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration.WhyTurnoverBelow

object WhyTurnoverBelowTestConstants {

  val whyTurnoverBelowModel: WhyTurnoverBelow = WhyTurnoverBelow(
    lostContract = true,
    semiRetiring = true,
    moreCompetitors = true,
    reducedTradingHours = true,
    seasonalBusiness = true,
    closedPlacesOfBusiness = true,
    turnoverLowerThanExpected = true)

  val whyTurnoverBelowJson: JsValue = Json.obj(
    "lostContract" -> true,
    "semiRetiring" -> true,
    "moreCompetitors" -> true,
    "reducedTradingHours" -> true,
    "seasonalBusiness" -> true,
    "closedPlacesOfBusiness" -> true,
    "turnoverLowerThanExpected" -> true
  )

  val whyTurnoverBelowString: String =
    "Lost Contract;Semi-Retiring;More Competitors;Reduced Trading Hours;Seasonal Business;Closed Places of Business;Turnover Lower than Expected"


}
