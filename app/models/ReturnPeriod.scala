/*
 * Copyright 2022 HM Revenue & Customs
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
import utils.JsonObjectSugar

abstract class ReturnPeriod(transOrCapEmail: Option[String],
                            nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                            firstNonNSTPPeriod: Option[TaxPeriod]) {
  def stdReturnPeriod: Option[String]
  val transactorOrCapacitorEmail: Option[String] = transOrCapEmail
  val nonStandardTaxPeriods: Option[Seq[TaxPeriod]] = nonStdTaxPeriods
  val firstNonNSTPTaxPeriod: Option[TaxPeriod] = firstNonNSTPPeriod
}

case class MAReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("MA")
}

case class MBReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("MB")
}

case class MCReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("MC")
}

case class MMReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("MM")
}

case class YAReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YA")
}

case class YBReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YB")
}

case class YCReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YC")
}

case class YDReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YD")
}

case class YEReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YE")
}

case class YFReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YF")
}

case class YGReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YG")
}

case class YHReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YH")
}

case class YIReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YI")
}

case class YJReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YJ")
}

case class YKReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YK")
}

case class YLReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = Some("YL")
}

case class noReturnPeriod(transOrCapEmail: Option[String],
                          nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                          firstNonNSTPPeriod: Option[TaxPeriod]) extends ReturnPeriod(transOrCapEmail, nonStdTaxPeriods, firstNonNSTPPeriod) {
  override val stdReturnPeriod: Option[String] = None
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
        case None =>
          JsSuccess(noReturnPeriod(transactorOrCapacitorEmail, nonStandardTaxPeriods, firstNonNSTPPeriod))
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
}
