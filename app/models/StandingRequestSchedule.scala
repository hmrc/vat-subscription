/*
 * Copyright 2024 HM Revenue & Customs
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

import models.StandingRequest.{srReads, srWrites}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.{JsonObjectSugar, JsonReadUtil}
import java.time.{LocalDate, LocalDateTime, Instant, ZoneId}

case class StandingRequestSchedule(processingDate: Option[LocalDate], standingRequests: List[StandingRequest] = List())

case class StandingRequest(requestNumber: String,
                           requestCategory: String,
                           createdOn: Option[LocalDate],
                           changedOn: Option[LocalDate],
                           requestItems: List[RequestItem]
                          )

case class RequestItem(period: String,
                       periodKey: String,
                       startDate: LocalDate,
                       endDate: LocalDate,
                       dueDate: LocalDate,
                       amount: BigDecimal,
                       chargeReference: Option[String],
                       postingDueDate: Option[LocalDate])

object RequestItem extends JsonReadUtil with JsonObjectSugar {

  private val periodPath = __ \ "period"
  private val periodKeyPath = __ \ "periodKey"
  private val startDatePath = __ \ "startDate"
  private val endDatePath = __ \ "endDate"
  private val dueDatePath = __ \ "dueDate"
  private val amountPath = __ \ "amount"
  private val chargeReferencePath = __ \ "chargeReference"
  private val postingDueDatePath = __ \ "postingDueDate"

  implicit val riReads: Reads[RequestItem] = (
    periodPath.read[String] and
      periodKeyPath.read[String] and
      startDatePath.read[LocalDate] and
      endDatePath.read[LocalDate] and
      dueDatePath.read[LocalDate] and
      amountPath.read[BigDecimal] and
      chargeReferencePath.readNullable[String] and
      postingDueDatePath.readNullable[LocalDate]
    )(RequestItem.apply _)

  implicit val riWrites: Writes[RequestItem] = (
    periodPath.write[String] and
      periodKeyPath.write[String] and
      startDatePath.write[LocalDate] and
      endDatePath.write[LocalDate] and
      dueDatePath.write[LocalDate] and
      amountPath.write[BigDecimal] and
      chargeReferencePath.writeNullable[String] and
      postingDueDatePath.writeNullable[LocalDate]
    )(unlift(RequestItem.unapply))
}

object StandingRequest extends JsonReadUtil with JsonObjectSugar {

  private val requestNumberPath = __ \ "requestNumber"
  private val requestCategoryPath = __ \ "requestCategory"
  private val createdOnPath = __ \ "createdOn"
  private val changedOnPath = __ \ "changedOn"
  private val requestItemsPath = __ \ "requestItems"

  implicit val srReads: Reads[StandingRequest] = (
    requestNumberPath.read[String] and
      requestCategoryPath.read[String] and
      createdOnPath.readNullable[LocalDate] and
      changedOnPath.readNullable[LocalDate] and
      requestItemsPath.read[List[RequestItem]]
    )(StandingRequest.apply _)

  implicit val srWrites: Writes[StandingRequest] = (
    requestNumberPath.write[String] and
      requestCategoryPath.write[String] and
      createdOnPath.writeNullable[LocalDate] and
      changedOnPath.writeNullable[LocalDate] and
      requestItemsPath.write[List[RequestItem]]
    )(unlift(StandingRequest.unapply))

}

object StandingRequestSchedule extends JsonReadUtil with JsonObjectSugar {

  implicit val localDateReads: Reads[LocalDate] = Reads { json =>
    json.validate[String].flatMap { dateStr =>
      try {
        val localDate = Instant.parse(dateStr).atZone(ZoneId.systemDefault()).toLocalDate
        JsSuccess(localDate)
      } catch {
        case _: Exception => JsError(s"Invalid date format: $dateStr")
      }
    }
  }

  val reads: Reads[StandingRequestSchedule] = (
    (JsPath \ "processingDate").readNullable[LocalDate](localDateReads) and
      (JsPath \ "standingRequests").read[List[StandingRequest]]
    )(StandingRequestSchedule.apply _)

  implicit val commsPreferenceWrites: Writes[CommsPreference] = CommsPreference.writes

  implicit val writes: Writes[StandingRequestSchedule] = Writes {
    model =>
      jsonObjNoNulls(
        "processingDate" -> model.processingDate,
        "standingRequests" -> model.standingRequests
      )
  }
}


