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

import play.api.libs.json._

sealed trait MandationStatus {
  def DesString: String

  def Name: String
}

case object MTDfBMandated extends MandationStatus {
  override val DesString: String = "1"

  override val Name: String = "MTDfB Mandated"
}

case object MTDfBVoluntary extends MandationStatus {
  override val DesString: String = "2"

  override val Name: String = "MTDfB Voluntary"
}


case object NonMTDfB extends MandationStatus {
  override val DesString: String = "3"

  override val Name: String = "Non MTDfB"
}


case object NonDigital extends MandationStatus {
  override val DesString: String = "4"

  override val Name: String = "Non Digital"
}


object MandationStatus {

  def unapply(arg: MandationStatus): Option[String] = Some(arg.Name)

  implicit val desReader: Reads[MandationStatus] = for {
    value <- JsPath.read[String].map {
      case MTDfBMandated.DesString => MTDfBMandated
      case MTDfBVoluntary.DesString => MTDfBVoluntary
      case NonMTDfB.DesString => NonMTDfB
      case NonDigital.DesString => NonDigital
    }
  } yield value

  implicit val writer: Writes[MandationStatus] = Writes(
    status => JsString(status.Name)
  )
}
