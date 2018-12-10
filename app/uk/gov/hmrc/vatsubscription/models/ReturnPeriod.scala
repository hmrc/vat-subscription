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

sealed trait ReturnPeriod {
  def stdReturnPeriod: String
  def transactorOrCapacitorEmail: Option[String]
}

case class MAReturnPeriod(agentOrTransEmail: Option[String]) extends ReturnPeriod {
  override val stdReturnPeriod: String = "MA"
  override val transactorOrCapacitorEmail: Option[String] = agentOrTransEmail
}

case class MBReturnPeriod(agentOrTransEmail: Option[String]) extends ReturnPeriod {
  override val stdReturnPeriod: String = "MB"
  override val transactorOrCapacitorEmail: Option[String] = agentOrTransEmail
}

case class MCReturnPeriod(agentOrTransEmail: Option[String]) extends ReturnPeriod {
  override val stdReturnPeriod: String = "MC"
  override val transactorOrCapacitorEmail: Option[String] = agentOrTransEmail
}

case class MMReturnPeriod(agentOrTransEmail: Option[String]) extends ReturnPeriod {
  override val stdReturnPeriod: String = "MM"
  override val transactorOrCapacitorEmail: Option[String] = agentOrTransEmail
}

object ReturnPeriod {

  val frontendRds: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val currentDesReads: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val newDesReads: Reads[ReturnPeriod] = readReturnPeriod("returnPeriod")

  private def readReturnPeriod(attributeName: String): Reads[ReturnPeriod] = new Reads[ReturnPeriod] {
    override def reads(json: JsValue): JsResult[ReturnPeriod] = {

      val returnPeriod = json.as[Option[String]]((__ \ attributeName).readNullable[String])
      val transactorOrCapacitorEmail = json.as[Option[String]]((__ \ "transactorOrCapacitorEmail").readNullable[String])

      (returnPeriod, transactorOrCapacitorEmail) match {
        case (Some("MA"), _) =>
          JsSuccess(MAReturnPeriod(transactorOrCapacitorEmail))
        case (Some("MB"), _) =>
          JsSuccess(MBReturnPeriod(transactorOrCapacitorEmail))
        case (Some("MC"), _) =>
          JsSuccess(MCReturnPeriod(transactorOrCapacitorEmail))
        case (Some("MM"), _) =>
          JsSuccess(MMReturnPeriod(transactorOrCapacitorEmail))
        case _ => JsError("Invalid Return Period supplied")
      }
    }
  }

  implicit val returnPeriodWriter: Writes[ReturnPeriod] = Writes {
    period => Json.obj("stdReturnPeriod" -> period.stdReturnPeriod)
  }
}
