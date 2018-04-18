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

package uk.gov.hmrc.vatsubscription.testonly.controllers

import javax.inject.Inject
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.config.featureswitch.FeatureSwitch.switches
import uk.gov.hmrc.vatsubscription.config.featureswitch.{FeatureSwitch, FeatureSwitchSetting, FeatureSwitching}

class FeatureSwitchController @Inject()(val messagesApi: MessagesApi)
  extends BaseController with FeatureSwitching {

  private def returnCurrentSettings = {
    val featureSwitches = switches map (featureSwitch => FeatureSwitchSetting(featureSwitch, isEnabled(featureSwitch)))

    Ok(Json.toJson(featureSwitches))
  }

  lazy val get: Action[AnyContent] = Action {
    returnCurrentSettings
  }

  lazy val update: Action[List[FeatureSwitchSetting]] = Action(parse.json[List[FeatureSwitchSetting]]) { req =>
    req.body foreach { setting =>
      val featureSwitch = FeatureSwitch(setting)

      if (setting.enable) enable(featureSwitch)
      else disable(featureSwitch)
    }
    returnCurrentSettings
  }
}
