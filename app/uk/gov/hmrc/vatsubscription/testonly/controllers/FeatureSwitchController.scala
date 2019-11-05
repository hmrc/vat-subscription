/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.testonly.controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.config.featureSwitch.FeatureSwitchModel

class FeatureSwitchController @Inject()(appConfig: AppConfig) extends BaseController {

  val get: Action[AnyContent] = Action { implicit request => result }

  lazy val update: Action[FeatureSwitchModel] = Action(parse.json[FeatureSwitchModel]) { req =>
    appConfig.features.api1365Version(req.body.Api1365Version)
    appConfig.features.api1363Version(req.body.Api1363Version)
    appConfig.features.enableAnnualAccounting(req.body.enableAnnualAccounting)
    result
  }

  def result: Result = {
    Ok(Json.toJson(FeatureSwitchModel(
      Api1365Version = appConfig.features.api1365Version(),
      Api1363Version = appConfig.features.api1363Version(),
      enableAnnualAccounting = appConfig.features.enableAnnualAccounting()
    )))
  }
}
