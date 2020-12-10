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

import play.api.libs.json._
import config.AppConfig
import config.featureSwitch.{Api1365Latest, Api1365PreRelease}

case class RequestedChanges(ppobDetails: Boolean = false,
                            returnPeriod: Boolean = false,
                            deregInfo: Boolean = false,
                            repaymentBankDetails: Boolean = false,
                            businessActivities: Boolean = false,
                            flatRateScheme: Boolean = false,
                            organisationDetails: Boolean = false,
                            correspDetails: Boolean = false,
                            mandationStatus: Boolean = false,
                            commsPreference: Boolean = false)

object ChangePPOB extends RequestedChanges(ppobDetails = true)
object ChangeReturnPeriod extends RequestedChanges(returnPeriod = true)
object DeregistrationRequest extends RequestedChanges(deregInfo = true)
object ChangeOrganisationDetailsRequest extends RequestedChanges(organisationDetails = true)
object ChangeMandationStatus extends RequestedChanges(mandationStatus = true)
object ChangeCommsPreference extends RequestedChanges(commsPreference = true)
object ChangeCommsPreferenceAndEmail extends RequestedChanges(commsPreference = true,
                                                              ppobDetails = true)

object RequestedChanges {

  val DESApi1365Writes: AppConfig => Writes[RequestedChanges] = appConfig => Writes { model =>
    Json.obj(
      "PPOBDetails" -> model.ppobDetails,
      "returnPeriod" -> model.returnPeriod,
      "deregInfo" -> model.deregInfo,
      "repaymentBankDetails" -> model.repaymentBankDetails,
      "businessActivities" -> model.businessActivities,
      "flatRateScheme" -> model.flatRateScheme,
      "correspDetails" -> model.correspDetails,
      "organisationDetails" -> model.organisationDetails,
      "mandationStatus" -> model.mandationStatus
    ) ++ (appConfig.features.api1365Version() match {
      case Api1365Latest => Json.obj("commsPreference" -> model.commsPreference)
      case Api1365PreRelease => Json.obj()
    })
  }
}
