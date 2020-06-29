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

package uk.gov.hmrc.vatsubscription.config.featureSwitch

import play.api.Configuration
import play.api.libs.json._


class Api1365VersionFeature(key: String)(implicit config: Configuration) extends BaseFeature {

  def apply(value: Api1365Version): Unit = sys.props += key -> value.id

  def apply(): Api1365Version = Api1365Version(getConfig(key))

}

sealed trait Api1365Version {
  val id: String
}

object Api1365Latest extends Api1365Version {
  override val id: String = "Latest"
}

object Api1365PreRelease extends Api1365Version {
  override val id: String = "Pre-release"
}

object Api1365Version {

  implicit val rds: Reads[Api1365Version] = JsPath.read[String].map(apply)

  implicit val writes: Writes[Api1365Version] = Writes { model =>
    JsString(model.id)
  }

  def apply(id: String): Api1365Version = id match {
    case Api1365Latest.id => Api1365Latest
    case Api1365PreRelease.id => Api1365PreRelease
    case _ => throw new RuntimeException(s"Invalid API 1365 Version. Version supplied: $id")
  }

  def unapply(version: Api1365Version): String = version.id

}