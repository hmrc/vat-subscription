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

package uk.gov.hmrc.vatsubscription.models

import play.api.libs.json._

abstract class ReturnPeriod(transOrCapEmail: Option[String]) {
  def stdReturnPeriod: String
  val transactorOrCapacitorEmail: Option[String] = transOrCapEmail
}

case class MAReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "MA"
}

case class MBReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "MB"
}

case class MCReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "MC"
}

case class MMReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "MM"
}

object ReturnPeriod {

  val frontendRds: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val currentDesReads: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val newDesReads: Reads[ReturnPeriod] = readReturnPeriod("returnPeriod")

  private def readReturnPeriod(attributeName: String): Reads[ReturnPeriod] = new Reads[ReturnPeriod] {
    override def reads(json: JsValue): JsResult[ReturnPeriod] = {

      val returnPeriod = json.as[Option[String]]((__ \ attributeName).readNullable[String])
      val transactorOrCapacitorEmail: Option[String] = json.as[Option[String]]((__ \ "transactorOrCapacitorEmail").readNullable[String])

      returnPeriod match {
        case Some("MA") =>
          JsSuccess(MAReturnPeriod(transactorOrCapacitorEmail))
        case Some("MB") =>
          JsSuccess(MBReturnPeriod(transactorOrCapacitorEmail))
        case Some("MC") =>
          JsSuccess(MCReturnPeriod(transactorOrCapacitorEmail))
        case Some("MM") =>
          JsSuccess(MMReturnPeriod(transactorOrCapacitorEmail))
        case _ => JsError("Invalid Return Period supplied")
      }
    }
  }

  implicit val returnPeriodWriter: Writes[ReturnPeriod] = Writes {
    period => Json.obj("stdReturnPeriod" -> period.stdReturnPeriod)
  }
}
