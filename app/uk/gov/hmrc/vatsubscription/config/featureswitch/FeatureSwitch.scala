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

package uk.gov.hmrc.vatsubscription.config.featureswitch

import FeatureSwitch._

sealed trait FeatureSwitch {
  val name: String
  val displayName: String
}

object FeatureSwitch {
  val prefix = "feature-switch"

  val switches: Set[FeatureSwitch] = Set(
    MTDEligibilityCheck,
    AlreadySubscribedCheck
  )

  def apply(str: String): FeatureSwitch =
    switches find (_.name == str) match {
      case Some(switch) => switch
      case None => throw new IllegalArgumentException("Invalid feature switch: " + str)
    }

  def apply(setting: FeatureSwitchSetting): FeatureSwitch =
    switches find (_.displayName == setting.feature) match {
      case Some(switch) => switch
      case None => throw new IllegalArgumentException("Invalid feature switch: " + setting.feature)
    }
}

object MTDEligibilityCheck extends FeatureSwitch {
  val displayName = s"Enable MTD VAT control list eligibility check"
  val name = s"$prefix.control-list-check"
}

object AlreadySubscribedCheck extends FeatureSwitch {
  override val displayName: String = "Enable check for already subscribed VAT numbers (API 1363)"
  override val name: String = s"$prefix.already-subscribed-check"
}
