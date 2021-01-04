/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import play.api.libs.json._
import config.AppConfig
import utils.JsonObjectSugar

abstract class ReturnPeriod(transOrCapEmail: Option[String],
                            nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                            firstNonNSTPPeriod: Option[TaxPeriod]) {
  def stdReturnPeriod: String
  val transactorOrCapacitorEmail: Option[String] = transOrCapEmail
  val nonStandardTaxPeriods: Option[Seq[TaxPeriod]] = nonStdTaxPeriods
  val firstNonNSTPTaxPeriod: Option[TaxPeriod] = firstNonNSTPPeriod
}

case class MAReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "MA"
}

case class MBReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "MB"
}

case class MCReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "MC"
}

case class MMReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "MM"
}

case class YAReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YA"
}

case class YBReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YB"
}

case class YCReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YC"
}

case class YDReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YD"
}

case class YEReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YE"
}

case class YFReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YF"
}

case class YGReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YG"
}

case class YHReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YH"
}

case class YIReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YI"
}

case class YJReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YJ"
}

case class YKReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YK"
}

case class YLReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: String = "YL"
}

object ReturnPeriod extends JsonObjectSugar {

  val frontendRds: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val currentDesReads: Reads[ReturnPeriod] = readReturnPeriod("stdReturnPeriod")

  val inFlightReads: Reads[ReturnPeriod] = readReturnPeriod("returnPeriod")

  //scalastyle:off
  private def readReturnPeriod(attributeName: String): Reads[ReturnPeriod] = new Reads[ReturnPeriod] {
    override def reads(json: JsValue): JsResult[ReturnPeriod] = {

      val returnPeriod = json.as[Option[String]]((__ \ attributeName).readNullable[String])
      val transactorOrCapacitorEmail: Option[String] =
        json.as[Option[String]]((__ \ "transactorOrCapacitorEmail").readNullable[String])
      val nonStandardTaxPeriods: Option[Seq[TaxPeriod]] =
        json.as[Option[Seq[TaxPeriod]]]((__ \ "nonStdTaxPeriods").readNullable[Seq[TaxPeriod]])
      val firstNonNSTPPeriod: Option[TaxPeriod] =
        json.as[Option[TaxPeriod]]((__ \ "firstNonNSTPPeriod").readNullable[TaxPeriod])

      returnPeriod match {
        case Some("MA") =>
          JsSuccess(MAReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("MB") =>
          JsSuccess(MBReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("MC") =>
          JsSuccess(MCReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("MM") =>
          JsSuccess(MMReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YA") =>
          JsSuccess(YAReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YB") =>
          JsSuccess(YBReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YC") =>
          JsSuccess(YCReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YD") =>
          JsSuccess(YDReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YE") =>
          JsSuccess(YEReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YF") =>
          JsSuccess(YFReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YG") =>
          JsSuccess(YGReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YH") =>
          JsSuccess(YHReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YI") =>
          JsSuccess(YIReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YJ") =>
          JsSuccess(YJReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YK") =>
          JsSuccess(YKReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case Some("YL") =>
          JsSuccess(YLReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
        case _ => JsError("Invalid Return Period supplied")
      }
    }
  }
  //scalastyle:on

  implicit val returnPeriodWriter: Writes[ReturnPeriod] = Writes (
    period => jsonObjNoNulls(
      "stdReturnPeriod" -> period.stdReturnPeriod,
      "nonStdTaxPeriods" -> period.nonStandardTaxPeriods,
      "firstNonNSTPPeriod" -> period.firstNonNSTPTaxPeriod
    )
  )

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
