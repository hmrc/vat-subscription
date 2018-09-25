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

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class RequestedChanges(addressDetails: Boolean = false,
                            returnPeriod: Boolean = false,
                            deregInfo: Boolean = false,
                            repaymentBankDetails: Boolean = false,
                            businessActivities: Boolean = false,
                            flateRateScheme: Boolean = false,
                            correspDetails: Boolean = false)

object ChangePPOB extends RequestedChanges(addressDetails = true)
object ChangeReturnPeriod extends RequestedChanges(returnPeriod = true)
object DeregistrationRequest extends RequestedChanges(deregInfo = true)

object RequestedChanges {

  implicit val writes: Writes[RequestedChanges] = (
    (JsPath \ "PPOBDetails").write[Boolean] and
    (JsPath \ "returnPeriod").write[Boolean] and
    (JsPath \ "deregInfo").write[Boolean] and
    (JsPath \ "repaymentBankDetails").write[Boolean] and
    (JsPath \ "businessActivities").write[Boolean] and
    (JsPath \ "flateRateScheme").write[Boolean] and
    (JsPath \ "correspDetails").write[Boolean]
  )(unlift(RequestedChanges.unapply))
}
