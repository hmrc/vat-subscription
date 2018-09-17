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

case object InvalidReturnPeriod extends ReturnPeriod {
  override val stdReturnPeriod: String = "XX"
}

object ReturnPeriod {

  def apply(period: String): ReturnPeriod = period match {
    case MAReturnPeriod.stdReturnPeriod => MAReturnPeriod
    case MBReturnPeriod.stdReturnPeriod => MBReturnPeriod
    case MCReturnPeriod.stdReturnPeriod => MCReturnPeriod
    case MMReturnPeriod.stdReturnPeriod => MMReturnPeriod
    case _ =>
      Logger.warn(s"[ReturnPeriod][apply] Invalid Return Period: '$period'")
      InvalidReturnPeriod
  }

  def unapply(arg: ReturnPeriod): String = arg.stdReturnPeriod

  val currentReads: Reads[ReturnPeriod] = for {
    value <- (__ \ "stdReturnPeriod").readNullable[String] map {
      case Some(MAReturnPeriod.stdReturnPeriod) => MAReturnPeriod
      case Some(MBReturnPeriod.stdReturnPeriod) => MBReturnPeriod
      case Some(MCReturnPeriod.stdReturnPeriod) => MCReturnPeriod
      case Some(MMReturnPeriod.stdReturnPeriod) => MMReturnPeriod
      case invalid =>
        Logger.warn(s"[ReturnPeriod][apply] Invalid Return Period: '$invalid'")
        InvalidReturnPeriod
    }
  } yield value

  val newReads: Reads[ReturnPeriod] = {
    for {
      value <- (__ \ "returnPeriod").readNullable[String] map {
        case Some(MAReturnPeriod.stdReturnPeriod) => MAReturnPeriod
        case Some(MBReturnPeriod.stdReturnPeriod) => MBReturnPeriod
        case Some(MCReturnPeriod.stdReturnPeriod) => MCReturnPeriod
        case Some(MMReturnPeriod.stdReturnPeriod) => MMReturnPeriod
        case invalid =>
          Logger.warn(s"[InflightReturnPeriod][apply] Invalid Return Period: '$invalid'")
          InvalidReturnPeriod
      }
    } yield value
  }

  implicit val returnPeriodWriter: Writes[ReturnPeriod] = Writes {
    period => Json.obj("stdReturnPeriod" -> period.stdReturnPeriod)
  }
}
