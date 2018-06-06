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

package uk.gov.hmrc.vatsubscription.models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.vatsubscription.utils.JsonReadUtil

case class PPOB(address: Option[PPOBAddress])

object PPOB {

  private val addressPath = JsPath \ "address"

  implicit val ppobReader: Reads[PPOB] = for {
    address <- addressPath.readNullable[PPOBAddress]
  } yield PPOB(address)

  implicit val ppobWriter: Writes[PPOB] = Writes(
    ppob => Json.obj("address" -> ppob.address)
  )
}


case class PPOBAddress(line1: Option[String],
                       line2: Option[String],
                       line3: Option[String],
                       line4: Option[String],
                       line5: Option[String],
                       postCode: Option[String],
                       countryCode: Option[String])

object PPOBAddress extends JsonReadUtil {

  private val line1Path = JsPath \ "line1"
  private val line2Path =  JsPath \ "line2"
  private val line3Path = JsPath \ "line3"
  private val line4Path = JsPath \ "line4"
  private val line5Path = JsPath \ "line5"
  private val postCodePath = JsPath \ "postCode"
  private val countryCodePath = JsPath \ "countryCode"

  implicit val ppobAddressReader: Reads[PPOBAddress] = for {
    line1 <- line1Path.readNullable[String]
    line2 <- line2Path.readNullable[String]
    line3 <- line3Path.readNullable[String]
    line4 <- line4Path.readNullable[String]
    line5 <- line5Path.readNullable[String]
    postCode <- postCodePath.readNullable[String]
    countryCode <- countryCodePath.readNullable[String]
  } yield PPOBAddress(line1, line2, line3, line4, line5, postCode, countryCode)

  implicit val ppobAddressWriter: Writes[PPOBAddress] = (
    line1Path.writeNullable[String] and
      line2Path.writeNullable[String] and
      line3Path.writeNullable[String] and
      line4Path.writeNullable[String] and
      line5Path.writeNullable[String] and
      postCodePath.writeNullable[String] and
      countryCodePath.writeNullable[String]
    )(unlift(PPOBAddress.unapply))
}
