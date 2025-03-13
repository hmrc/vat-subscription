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

package helpers

import play.api.libs.json.{JsObject, JsValue, Json}
import helpers.BankDetailsTestConstants._
import helpers.BaseTestConstants._
import models._
import play.api.libs.json.JsArray
import java.time.{LocalDate, LocalDateTime, Instant, ZoneId}

object StandingRequestScheduleConstants {

  def parseLocalTimeDate(date: String) = Instant.parse(date)
  .atZone(ZoneId.systemDefault())
  .toLocalDate

  val standingRequestEmptyModel = StandingRequestSchedule(None, Nil)

  val standingRequestEmptyStandingRequestsModel = StandingRequestSchedule(
    Some(parseLocalTimeDate("2025-03-17T09:30:47Z")),
    Nil
  )

   val standingRequestNonPOACategoryModel = StandingRequestSchedule(
    Some(parseLocalTimeDate("2025-03-17T09:30:47Z")),
    List(
      StandingRequest(
        "20000037277",
        "4",
        Some(LocalDate.parse("2025-12-31")),
        Some(LocalDate.parse("2026-01-26")),
        List(
          RequestItem(
            "1",
            "26A1",
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2026-03-31"),
            LocalDate.parse("2026-02-28"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2026-02-28"))
          ),
          RequestItem(
            "2",
            "26A1",
           LocalDate.parse("2026-01-01"),
           LocalDate.parse("2026-03-31"),
           LocalDate.parse("2026-03-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2026-03-31"))
          ),
          RequestItem(
            "3",
            "25A2",
            LocalDate.parse("2026-04-01"),
            LocalDate.parse("2026-06-30"),
            LocalDate.parse("2026-05-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2026-05-31"))
          )
        )
      )
    )
  )

  val standingRequestMultipleModel = StandingRequestSchedule(
    Some(parseLocalTimeDate("2025-03-17T09:30:47Z")),
    List(
      StandingRequest(
        "20000037272",
        "3",
        Some(LocalDate.parse("2025-01-05")),
        Some(LocalDate.parse("2025-02-26")),
        List(
          RequestItem(
            "1",
            "25A1",
            LocalDate.parse("2025-01-01"),
            LocalDate.parse("2025-03-31"),
            LocalDate.parse("2025-03-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2025-03-31"))
          ),
          RequestItem(
            "2",
            "25A2",
            LocalDate.parse("2025-04-01"),
            LocalDate.parse("2025-06-30"),
            LocalDate.parse("2025-05-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2025-05-31"))
          ),
          RequestItem(
            "3",
            "25A2",
            LocalDate.parse("2025-04-01"),
            LocalDate.parse("2025-06-30"),
            LocalDate.parse("2025-06-30"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2025-06-30"))
          )
        )
      ),
      StandingRequest(
        "20000037277",
        "4",
        Some(LocalDate.parse("2025-12-31")),
        Some(LocalDate.parse("2026-01-26")),
        List(
          RequestItem(
            "1",
            "26A1",
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2026-03-31"),
            LocalDate.parse("2026-02-28"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2026-02-28"))
          ),
          RequestItem(
            "2",
            "26A1",
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2026-03-31"),
            LocalDate.parse("2026-03-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2026-03-31"))
          ),
          RequestItem(
            "3",
            "25A2",
            LocalDate.parse("2026-04-01"),
            LocalDate.parse("2026-06-30"),
            LocalDate.parse("2026-05-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2026-05-31"))
          )
        )
      )
    )
  )


  val standingRequestCategory3ModelSingle = StandingRequestSchedule(
    Some(parseLocalTimeDate("2025-03-17T09:30:47Z")),
    List(
      StandingRequest(
        "20000037272",
        "3",
        Some(LocalDate.parse("2025-01-05")),
        Some(LocalDate.parse("2025-02-26")),
        List(
          RequestItem(
            "1",
            "25A1",
            LocalDate.parse("2025-01-01"),
            LocalDate.parse("2025-03-31"),
            LocalDate.parse("2025-03-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2025-03-31"))
          ),
          RequestItem(
            "2",
            "25A2",
            LocalDate.parse("2025-04-01"),
            LocalDate.parse("2025-06-30"),
            LocalDate.parse("2025-05-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2025-05-31"))
          ),
          RequestItem(
            "3",
            "25A2",
            LocalDate.parse("2025-04-01"),
            LocalDate.parse("2025-06-30"),
            LocalDate.parse("2025-06-30"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2025-06-30"))
          )
        )
      )
    )
  )

  val standingRequestScheduleModel = StandingRequestSchedule(
    Some(parseLocalTimeDate("2024-07-15T09:30:47Z")),
    List(
      StandingRequest(
        "20000037272",
        "3",
        Some(LocalDate.parse("2023-11-30")),
        Some(LocalDate.parse("2024-12-26")),
        List(
          RequestItem(
            "1",
            "24A1",
            LocalDate.parse("2024-02-01"),
            LocalDate.parse("2024-04-30"),
            LocalDate.parse("2024-03-31"),
            22945.23,
            Some("XD006411191344"),
            Some(LocalDate.parse("2024-03-31"))
          ),
          RequestItem(
            "2",
            "24A1",
            LocalDate.parse("2024-02-01"),
            LocalDate.parse("2024-04-30"),
            LocalDate.parse("2024-04-30"),
            22945.23,
            Some("XD006411191345"),
            Some(LocalDate.parse("2024-04-30"))
          ),
          RequestItem(
            "3",
            "24A2",
            LocalDate.parse("2024-05-01"),
            LocalDate.parse("2024-07-31"),
            LocalDate.parse("2024-06-30"),
            22945.23,
            Some("XD006411191346"),
            Some(LocalDate.parse("2024-06-30"))
          ),
          RequestItem(
            "4",
            "24A2",
            LocalDate.parse("2024-05-01"),
            LocalDate.parse("2024-07-31"),
            LocalDate.parse("2024-07-31"),
            22945.23,
            Some("XD006411191347"),
            Some(LocalDate.parse("2024-07-31"))
          ),
          RequestItem(
            "5",
            "24A3",
            LocalDate.parse("2024-08-01"),
            LocalDate.parse("2024-10-31"),
            LocalDate.parse("2024-09-30"),
            22945.23,
            Some("XD006411191348"),
            Some(LocalDate.parse("2024-09-30"))
          ),
          RequestItem(
            "6",
            "24A3",
            LocalDate.parse("2024-08-01"),
            LocalDate.parse("2024-10-31"),
            LocalDate.parse("2024-10-31"),
            22945.23,
            Some("XD006411191349"),
            Some(LocalDate.parse("2024-10-31"))
          ),
          RequestItem(
            "7",
            "24A4",
            LocalDate.parse("2024-11-01"),
            LocalDate.parse("2025-01-31"),
            LocalDate.parse("2024-12-31"),
            22945.23,
            Some("XD006411191350"),
            Some(LocalDate.parse("2024-12-31"))
          ),
          RequestItem(
            "8",
            "24A4",
            LocalDate.parse("2024-11-01"),
            LocalDate.parse("2025-01-31"),
            LocalDate.parse("2025-01-31"),
            22945.23,
            Some("XD006411191351"),
            Some(LocalDate.parse("2025-01-31"))
          )
        )
      ),
      StandingRequest(
        "20000037277",
        "3",
        Some(LocalDate.parse("2024-11-30")),
        Some(LocalDate.parse("2025-01-26")),
        List(
          RequestItem(
            "1",
            "25A1",
            LocalDate.parse("2025-02-01"),
            LocalDate.parse("2025-04-30"),
            LocalDate.parse("2025-03-31"),
            122945.23,
            None,
            None
          ),
          RequestItem(
            "2",
            "25A1",
            LocalDate.parse("2025-02-01"),
            LocalDate.parse("2025-04-30"),
            LocalDate.parse("2025-04-30"),
            122945.23,
            None,
            None
          ),
          RequestItem(
            "3",
            "25A2",
            LocalDate.parse("2025-05-01"),
            LocalDate.parse("2025-07-31"),
            LocalDate.parse("2025-06-30"),
            122945.23,
            None,
            None
          ),
          RequestItem(
            "4",
            "25A2",
            LocalDate.parse("2025-05-01"),
            LocalDate.parse("2025-07-31"),
            LocalDate.parse("2025-07-31"),
            122945.23,
            None,
            None
          ),
          RequestItem(
            "5",
            "25A3",
            LocalDate.parse("2025-08-01"),
            LocalDate.parse("2025-10-31"),
            LocalDate.parse("2025-09-30"),
            122945.23,
            None,
            None
          ),
          RequestItem(
            "6",
            "25A3",
            LocalDate.parse("2025-08-01"),
            LocalDate.parse("2025-10-31"),
            LocalDate.parse("2025-10-31"),
            122945.23,
            None,
            None
          ),
          RequestItem(
            "7",
            "25A4",
            LocalDate.parse("2025-11-01"),
            LocalDate.parse("2026-01-31"),
            LocalDate.parse("2025-12-31"),
            122945.23,
            None,
            None
          ),
          RequestItem(
            "8",
            "25A4",
            LocalDate.parse("2025-11-01"),
            LocalDate.parse("2026-01-31"),
            LocalDate.parse("2026-01-31"),
            122945.23,
            None,
            None
          )
        )
      )
    )
  )

  val standingRequestErrorJson = Json.obj(
    "processingDate" -> "2024-07-15",
    "code" -> "003",
    "text" -> "Request Could Not Be Processed"
  )

  val standingRequestEmptyJson = Json.obj(
    "processingDate" -> "2025-03-17",
    "standingRequests" -> JsArray.empty
  )

  val standingRequestJsonOneOpen = Json.obj(
    "processingDate" -> "2025-02-15",
    "standingRequests" -> Json.arr(
      Json.obj(
        "requestNumber" -> "20000037272",
        "requestCategory" -> "3",
        "createdOn" -> "2023-11-30",
        "changedOn" -> "2024-12-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "24A1",
            "startDate" -> "2024-02-01",
            "endDate" -> "2024-04-30",
            "dueDate" -> "2024-03-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2024-03-31"
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "24A1",
            "startDate" -> "2024-02-01",
            "endDate" -> "2024-04-30",
            "dueDate" -> "2024-04-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191345",
            "postingDueDate" -> "2024-04-30"
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "24A2",
            "startDate" -> "2024-05-01",
            "endDate" -> "2024-07-31",
            "dueDate" -> "2024-06-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191346",
            "postingDueDate" -> "2024-06-30"
          )
        )
      ),
      Json.obj(
        "requestNumber" -> "20000037277",
        "requestCategory" -> "3",
        "createdOn" -> "2024-11-30",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "25A1",
            "startDate" -> "2025-02-01",
            "endDate" -> "2025-04-30",
            "dueDate" -> "2025-03-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "25A1",
            "startDate" -> "2025-02-01",
            "endDate" -> "2025-04-30",
            "dueDate" -> "2025-04-30",
            "amount" -> 122945.23
          )
        )
      )
    )
  )

  val standingRequestScheduleJson: JsObject = Json.obj(
    "processingDate" -> "2024-07-15T09:30:47Z",
    "standingRequests" -> Json.arr(
      Json.obj(
        "requestNumber" -> "20000037272",
        "requestCategory" -> "3",
        "createdOn" -> "2023-11-30",
        "changedOn" -> "2024-12-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "24A1",
            "startDate" -> "2024-02-01",
            "endDate" -> "2024-04-30",
            "dueDate" -> "2024-03-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191344",
            "postingDueDate" -> "2024-03-31"
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "24A1",
            "startDate" -> "2024-02-01",
            "endDate" -> "2024-04-30",
            "dueDate" -> "2024-04-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191345",
            "postingDueDate" -> "2024-04-30"
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "24A2",
            "startDate" -> "2024-05-01",
            "endDate" -> "2024-07-31",
            "dueDate" -> "2024-06-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191346",
            "postingDueDate" -> "2024-06-30"
          ),
          Json.obj(
            "period" -> "4",
            "periodKey" -> "24A2",
            "startDate" -> "2024-05-01",
            "endDate" -> "2024-07-31",
            "dueDate" -> "2024-07-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191347",
            "postingDueDate" -> "2024-07-31"
          ),
          Json.obj(
            "period" -> "5",
            "periodKey" -> "24A3",
            "startDate" -> "2024-08-01",
            "endDate" -> "2024-10-31",
            "dueDate" -> "2024-09-30",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191348",
            "postingDueDate" -> "2024-09-30"
          ),
          Json.obj(
            "period" -> "6",
            "periodKey" -> "24A3",
            "startDate" -> "2024-08-01",
            "endDate" -> "2024-10-31",
            "dueDate" -> "2024-10-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191349",
            "postingDueDate" -> "2024-10-31"
          ),
          Json.obj(
            "period" -> "7",
            "periodKey" -> "24A4",
            "startDate" -> "2024-11-01",
            "endDate" -> "2025-01-31",
            "dueDate" -> "2024-12-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191350",
            "postingDueDate" -> "2024-12-31"
          ),
          Json.obj(
            "period" -> "8",
            "periodKey" -> "24A4",
            "startDate" -> "2024-11-01",
            "endDate" -> "2025-01-31",
            "dueDate" -> "2025-01-31",
            "amount" -> 22945.23,
            "chargeReference" -> "XD006411191351",
            "postingDueDate" -> "2025-01-31"
          )
        )
      ),
      Json.obj(
        "requestNumber" -> "20000037277",
        "requestCategory" -> "3",
        "createdOn" -> "2024-11-30",
        "changedOn" -> "2025-01-26",
        "requestItems" -> Json.arr(
          Json.obj(
            "period" -> "1",
            "periodKey" -> "25A1",
            "startDate" -> "2025-02-01",
            "endDate" -> "2025-04-30",
            "dueDate" -> "2025-03-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "2",
            "periodKey" -> "25A1",
            "startDate" -> "2025-02-01",
            "endDate" -> "2025-04-30",
            "dueDate" -> "2025-04-30",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "3",
            "periodKey" -> "25A2",
            "startDate" -> "2025-05-01",
            "endDate" -> "2025-07-31",
            "dueDate" -> "2025-06-30",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "4",
            "periodKey" -> "25A2",
            "startDate" -> "2025-05-01",
            "endDate" -> "2025-07-31",
            "dueDate" -> "2025-07-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "5",
            "periodKey" -> "25A3",
            "startDate" -> "2025-08-01",
            "endDate" -> "2025-10-31",
            "dueDate" -> "2025-09-30",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "6",
            "periodKey" -> "25A3",
            "startDate" -> "2025-08-01",
            "endDate" -> "2025-10-31",
            "dueDate" -> "2025-10-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "7",
            "periodKey" -> "25A4",
            "startDate" -> "2025-11-01",
            "endDate" -> "2026-01-31",
            "dueDate" -> "2025-12-31",
            "amount" -> 122945.23
          ),
          Json.obj(
            "period" -> "8",
            "periodKey" -> "25A4",
            "startDate" -> "2025-11-01",
            "endDate" -> "2026-01-31",
            "dueDate" -> "2026-01-31",
            "amount" -> 122945.23
          )
        )
      )
    )
  )


}
