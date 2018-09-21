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

import play.api.Logger
import play.api.libs.json._

sealed trait ReturnPeriod {
  def stdReturnPeriod: String
}

case object MAReturnPeriod extends ReturnPeriod {
  override val stdReturnPeriod: String = "MA"
}

case object MBReturnPeriod extends ReturnPeriod {
  override val stdReturnPeriod: String = "MB"
}


case object MCReturnPeriod extends ReturnPeriod {
  override val stdReturnPeriod: String = "MC"
}


case object MMReturnPeriod extends ReturnPeriod {
  override val stdReturnPeriod: String = "MM"
}

object ReturnPeriod {

  val frontendRds: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val currentDesReads: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val newDesReads: Reads[ReturnPeriod] = readReturnPeriod("returnPeriod")

  private def readReturnPeriod(attributeName: String): Reads[ReturnPeriod] = new Reads[ReturnPeriod] {
    override def reads(json: JsValue): JsResult[ReturnPeriod] = {
      json.as[Option[String]]((__ \ attributeName).readNullable[String]) match {
        case Some(MAReturnPeriod.stdReturnPeriod) => JsSuccess(MAReturnPeriod)
        case Some(MBReturnPeriod.stdReturnPeriod) => JsSuccess(MBReturnPeriod)
        case Some(MCReturnPeriod.stdReturnPeriod) => JsSuccess(MCReturnPeriod)
        case Some(MMReturnPeriod.stdReturnPeriod) => JsSuccess(MMReturnPeriod)
        case _ => JsError("Invalid Return Period supplied")
      }
    }
  }

  implicit val returnPeriodWriter: Writes[ReturnPeriod] = Writes {
    period => Json.obj("stdReturnPeriod" -> period.stdReturnPeriod)
  }
}
