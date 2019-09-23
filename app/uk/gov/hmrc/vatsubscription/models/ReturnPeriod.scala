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
import uk.gov.hmrc.vatsubscription.config.AppConfig

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

case class YAReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YA"
}

case class YBReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YB"
}

case class YCReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YC"
}

case class YDReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YD"
}

case class YEReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YE"
}

case class YFReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YF"
}

case class YGReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YG"
}

case class YHReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YH"
}

case class YIReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YI"
}

case class YJReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YJ"
}

case class YKReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YK"
}

case class YLReturnPeriod(transOrCapEmail: Option[String]) extends ReturnPeriod(transOrCapEmail) {
  override val stdReturnPeriod: String = "YL"
}

object ReturnPeriod {

  val frontendRds: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val currentDesReads: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val newDesReads: Reads[ReturnPeriod] = readReturnPeriod("returnPeriod")

  //scalastyle:off
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
        case Some("YA") =>
          JsSuccess(YAReturnPeriod(transactorOrCapacitorEmail))
        case Some("YB") =>
          JsSuccess(YBReturnPeriod(transactorOrCapacitorEmail))
        case Some("YC") =>
          JsSuccess(YCReturnPeriod(transactorOrCapacitorEmail))
        case Some("YD") =>
          JsSuccess(YDReturnPeriod(transactorOrCapacitorEmail))
        case Some("YE") =>
          JsSuccess(YEReturnPeriod(transactorOrCapacitorEmail))
        case Some("YF") =>
          JsSuccess(YFReturnPeriod(transactorOrCapacitorEmail))
        case Some("YG") =>
          JsSuccess(YGReturnPeriod(transactorOrCapacitorEmail))
        case Some("YH") =>
          JsSuccess(YHReturnPeriod(transactorOrCapacitorEmail))
        case Some("YI") =>
          JsSuccess(YIReturnPeriod(transactorOrCapacitorEmail))
        case Some("YJ") =>
          JsSuccess(YJReturnPeriod(transactorOrCapacitorEmail))
        case Some("YK") =>
          JsSuccess(YKReturnPeriod(transactorOrCapacitorEmail))
        case Some("YL") =>
          JsSuccess(YLReturnPeriod(transactorOrCapacitorEmail))
        case _ => JsError("Invalid Return Period supplied")
      }
    }
  }
  //scalastyle:on

  implicit val returnPeriodWriter: Writes[ReturnPeriod] = Writes {
    period => Json.obj("stdReturnPeriod" -> period.stdReturnPeriod)
  }

  def filterReturnPeriod(returnPeriod: Option[ReturnPeriod], appConf: AppConfig): Option[ReturnPeriod] = {
    if(!appConf.features.enableAnnualAccounting()){
      returnPeriod.filter(period =>
        period.isInstanceOf[MAReturnPeriod] ||
          period.isInstanceOf[MBReturnPeriod] ||
          period.isInstanceOf[MCReturnPeriod] ||
          period.isInstanceOf[MMReturnPeriod]
      )
    } else returnPeriod
  }
}
