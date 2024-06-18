/*
 * Copyright 2024 HM Revenue & Customs
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

package models.post

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, Writes, __}
import utils.JsonReadUtil

case class PPOBAddressPost(line1: Option[String],
                           line2: Option[String],
                           line3: Option[String],
                           line4: Option[String],
                           line5: Option[String],
                           postCode: Option[String],
                           countryCode: Option[String],
                           addressValidated: Boolean = true)

object PPOBAddressPost extends JsonReadUtil {

  private val line1Path = __ \ "line1"
  private val line2Path =  __ \ "line2"
  private val line3Path = __ \ "line3"
  private val line4Path = __ \ "line4"
  private val line5Path = __ \ "line5"
  private val postCodePath = __ \ "postCode"
  private val countryCodePath = __ \ "countryCode"
  private val addressValidatedPath = __ \ "addressValidated"

  implicit val ppobAddressReader: Reads[PPOBAddressPost] = for {
    line1 <- line1Path.readNullable[String]
    line2 <- line2Path.readNullable[String]
    line3 <- line3Path.readNullable[String]
    line4 <- line4Path.readNullable[String]
    line5 <- line5Path.readNullable[String]
    postCode <- postCodePath.readNullable[String]
    countryCode <- countryCodePath.readNullable[String]
  } yield PPOBAddressPost(line1, line2, line3, line4, line5, postCode, countryCode)

  implicit val ppobAddressWriter: Writes[PPOBAddressPost] = (
    line1Path.writeNullable[String] and
      line2Path.writeNullable[String] and
      line3Path.writeNullable[String] and
      line4Path.writeNullable[String] and
      line5Path.writeNullable[String] and
      postCodePath.writeNullable[String] and
      countryCodePath.writeNullable[String] and
      addressValidatedPath.write[Boolean]
    )(unlift(PPOBAddressPost.unapply))
}
