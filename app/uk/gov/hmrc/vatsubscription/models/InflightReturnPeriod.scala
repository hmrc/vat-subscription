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

sealed trait InflightReturnPeriod{
  def returnPeriod: String
}

case object MAInflightReturnPeriod extends InflightReturnPeriod {
  override val returnPeriod: String = "MA"
}

case object MBInflightReturnPeriod extends InflightReturnPeriod {
  override val returnPeriod: String = "MB"
}


case object MCInflightReturnPeriod extends InflightReturnPeriod {
  override val returnPeriod: String = "MC"
}


case object MMInflightReturnPeriod extends InflightReturnPeriod {
  override val returnPeriod: String = "MM"
}

case object InvalidInflightReturnPeriod extends InflightReturnPeriod {
  override val returnPeriod: String = "XX"
}

object InflightReturnPeriod {

  def apply(period: String): InflightReturnPeriod = period match {
    case MAInflightReturnPeriod.returnPeriod => MAInflightReturnPeriod
    case MBInflightReturnPeriod.returnPeriod => MBInflightReturnPeriod
    case MCInflightReturnPeriod.returnPeriod => MCInflightReturnPeriod
    case MMInflightReturnPeriod.returnPeriod => MMInflightReturnPeriod
    case _ =>
      Logger.warn(s"[ReturnPeriod][apply] Invalid Return Period: '$period'")
      InvalidInflightReturnPeriod
  }

  def unapply(arg: InflightReturnPeriod): String = arg.returnPeriod

  implicit val inflightReturnPeriodReader: Reads[InflightReturnPeriod] = for {
    value <- (__ \ "returnPeriod").readNullable[String] map {
      case Some(MAInflightReturnPeriod.returnPeriod) => MAInflightReturnPeriod
      case Some(MBInflightReturnPeriod.returnPeriod) => MBInflightReturnPeriod
      case Some(MCInflightReturnPeriod.returnPeriod) => MCInflightReturnPeriod
      case Some(MMInflightReturnPeriod.returnPeriod) => MMInflightReturnPeriod
      case invalid =>
        Logger.warn(s"[InflightReturnPeriod][apply] Invalid Inflight Return Period: '$invalid'")
        InvalidInflightReturnPeriod
    }
  } yield value


  implicit val inflightReturnPeriodWriter: Writes[InflightReturnPeriod] = Writes {
    period => Json.obj("stdReturnPeriod" -> period.returnPeriod)
  }
}
