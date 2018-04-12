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
  val desString: String

  def toString: String
}

case object MTDfBMandated extends MandationStatus {
  val desString: String = "1"

  override def toString: String = "MTDfB Mandated"
}

case object MTDfBVoluntary extends MandationStatus {
  val desString: String = "2"

  override def toString: String = "MTDfB Voluntary"
}


case object NonMTDfB extends MandationStatus {
  val desString: String = "3"

  override def toString: String = "Non MTDfB"
}


case object NonDigital extends MandationStatus {
  val desString: String = "4"

  override def toString: String = "Non Digital"
}


object MandationStatus {

  def unapply(arg: MandationStatus): Option[String] = Some(arg.toString)

  val desReader: Reads[MandationStatus] = for {
    value <- JsPath.read[String].map {
      case MTDfBMandated.desString => MTDfBMandated
      case MTDfBVoluntary.desString => MTDfBVoluntary
      case NonMTDfB.desString => NonMTDfB
      case NonDigital.desString => NonDigital
    }
  } yield value

  val writer: Writes[MandationStatus] = Writes(
    (status: MandationStatus) => JsString(status.toString)
  )

  implicit val format: Format[MandationStatus] = Format[MandationStatus](
    desReader,
    writer
  )
}
