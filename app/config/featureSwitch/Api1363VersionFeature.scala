/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.Configuration
import play.api.libs.json._


class Api1363VersionFeature(key: String)(implicit config: Configuration) extends BaseFeature {

  def apply(value: Api1363Version): Unit = sys.props += key -> value.id

  def apply(): Api1363Version = Api1363Version(getConfig(key))

}

sealed trait Api1363Version {
  val id: String
}

object Api1363Latest extends Api1363Version {
  override val id: String = "Latest"
}

object Api1363Version {

  implicit val rds: Reads[Api1363Version] = JsPath.read[String].map(apply)

  implicit val writes: Writes[Api1363Version] = Writes { model =>
    JsString(model.id)
  }

  def apply(id: String): Api1363Version = id match {
    case Api1363Latest.id => Api1363Latest
    case _ => throw new RuntimeException(s"Invalid API 1363 Version. Version supplied: $id")
  }

  def unapply(version: Api1363Version): String = version.id

}
