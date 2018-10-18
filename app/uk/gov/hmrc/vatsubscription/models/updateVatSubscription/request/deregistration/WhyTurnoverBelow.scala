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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration

import play.api.libs.json._

case class WhyTurnoverBelow(lostContract: Boolean,
                            semiRetiring: Boolean,
                            moreCompetitors: Boolean,
                            reducedTradingHours: Boolean,
                            seasonalBusiness: Boolean,
                            closedPlacesOfBusiness: Boolean,
                            turnoverLowerThanExpected: Boolean,
                            alreadyBelow: Boolean = false) {

  override def toString: String = List(
    lostContract -> "Lost Contract",
    semiRetiring -> "Semi-Retiring",
    moreCompetitors -> "More Competitors",
    reducedTradingHours -> "Reduced Trading Hours",
    seasonalBusiness -> "Seasonal Business",
    closedPlacesOfBusiness -> "Closed Places of Business",
    turnoverLowerThanExpected -> "Turnover Lower than Expected",
    alreadyBelow -> "Already below the threshold"
  ).filter(_._1).map(_._2).mkString(";")

}

object TurnoverAlreadyBelow extends WhyTurnoverBelow(
  lostContract = false,
  semiRetiring = false,
  moreCompetitors = false,
  reducedTradingHours = false,
  seasonalBusiness = false,
  closedPlacesOfBusiness = false,
  turnoverLowerThanExpected = false,
  alreadyBelow = true
)

object WhyTurnoverBelow {

  implicit val frontendReads: Reads[WhyTurnoverBelow] = for {
    lostContract <- (__ \ "lostContract").read[Boolean]
    semiRetiring <- (__ \ "semiRetiring").read[Boolean]
    moreCompetitors <- (__ \ "moreCompetitors").read[Boolean]
    reducedTradingHours <- (__ \ "reducedTradingHours").read[Boolean]
    seasonalBusiness <- (__ \ "seasonalBusiness").read[Boolean]
    closedPlacesOfBusiness <- (__ \ "closedPlacesOfBusiness").read[Boolean]
    turnoverLowerThanExpected <- (__ \ "turnoverLowerThanExpected").read[Boolean]
  } yield WhyTurnoverBelow(
    lostContract,
    semiRetiring,
    moreCompetitors,
    reducedTradingHours,
    seasonalBusiness,
    closedPlacesOfBusiness,
    turnoverLowerThanExpected
  )

  implicit val desWrites: Writes[WhyTurnoverBelow] = Writes { model => JsString(model.toString) }

}
