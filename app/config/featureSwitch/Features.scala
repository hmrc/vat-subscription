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

package config.featureSwitch

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class Features @Inject()(implicit config: Configuration) extends BaseFeature {

  private val featureSwitch: String = "feature-switch"
  lazy val api1365Version = new Api1365VersionFeature(s"$featureSwitch.Api1365Version")
  lazy val api1363Version = new Api1363VersionFeature(s"$featureSwitch.Api1363Version")
}
