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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class RequestedChanges(ppobDetails: Boolean = false,
                            returnPeriod: Boolean = false,
                            deregInfo: Boolean = false,
                            repaymentBankDetails: Boolean = false,
                            businessActivities: Boolean = false,
                            flateRateScheme: Boolean = false,
                            organisationDetails: Boolean = false,
                            correspDetails: Boolean = false)

object ChangePPOB extends RequestedChanges(ppobDetails = true)
object ChangeReturnPeriod extends RequestedChanges(returnPeriod = true)
object DeregistrationRequest extends RequestedChanges(deregInfo = true)

object RequestedChanges {

  val currentDESApi1365Writes: Writes[RequestedChanges] = Writes { model =>
    Json.obj(
      "PPOBDetails" -> model.ppobDetails,
      "returnPeriod" -> model.returnPeriod,
      "repaymentBankDetails" -> model.repaymentBankDetails
    )
  }

  val latestDESApi1365Writes: Writes[RequestedChanges] = (
    (__ \ "PPOBDetails").write[Boolean] and
      (__ \ "returnPeriod").write[Boolean] and
      (__ \ "deregInfo").write[Boolean] and
      (__ \ "repaymentBankDetails").write[Boolean] and
      (__ \ "businessActivities").write[Boolean] and
      (__ \ "flateRateScheme").write[Boolean] and
      (__ \ "organisationDetails").write[Boolean] and
      (__ \ "correspDetails").write[Boolean]
    )(unlift(RequestedChanges.unapply))
}
