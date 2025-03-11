/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import helpers.IntegrationTestConstants._
import helpers.servicemocks.AuthStub._
import helpers.servicemocks.GetStandingRequestScheduleStub._
import helpers.{ComponentSpecBase, CustomMatchers}
import models._
import models.get.{PPOBGet, PPOBAddressGet}
import com.github.tomakehurst.wiremock.client.WireMock._

class RetrieveStandingRequestScheduleControllerISpec
    extends ComponentSpecBase
    with BeforeAndAfterEach
    with CustomMatchers {

  "/:vatNumber/standing-requests" when {
    "the user does not have an mtd vat enrolment" should {
      "return FORBIDDEN" in {
        stubAuthFailure()

        val res = get(s"/$testVatNumber/standing-requests")

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to HIP is successful" should {

      val expectedStandingRequestSchedule: JsObject = Json.obj(
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

      "return OK with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetStandingRequestSchedule(testVatNumber)(
          OK,
          standingRequestScheduleJson
        )

        val res = get(s"/$testVatNumber/standing-requests")
        val unmatchedRequests =
          findAll(getRequestedFor(urlMatching(".*standing-requests.*")))
        println(s"Unmatched Requests in WireMock: ${unmatchedRequests.size()}")
        unmatchedRequests.forEach(req => println(req))
        res should have(
          httpStatus(OK),
          jsonBodyAs(expectedStandingRequestSchedule)
        )
      }

      "return OK with requestCategory 3 standing requests only" in {

        val expectedStandingRequestRequestCategory3Only: JsObject = Json.obj(
          "processingDate" -> "2025-03-17T09:30:47Z",
          "standingRequests" -> Json.arr(
            Json.obj(
              "requestNumber" -> "20000037272",
              "requestCategory" -> "3",
              "createdOn" -> "2025-01-05",
              "changedOn" -> "2025-02-26",
              "requestItems" -> Json.arr(
                Json.obj(
                  "period" -> "1",
                  "periodKey" -> "25A1",
                  "startDate" -> "2025-01-01",
                  "endDate" -> "2025-03-31",
                  "dueDate" -> "2025-03-31",
                  "amount" -> 22945.23,
                  "chargeReference" -> "XD006411191344",
                  "postingDueDate" -> "2025-03-31"
                ),
                Json.obj(
                  "period" -> "2",
                  "periodKey" -> "25A2",
                  "startDate" -> "2025-04-01",
                  "endDate" -> "2025-06-30",
                  "dueDate" -> "2025-05-31",
                  "amount" -> 22945.23,
                  "chargeReference" -> "XD006411191344",
                  "postingDueDate" -> "2025-05-31"
                ),
                Json.obj(
                  "period" -> "3",
                  "periodKey" -> "25A2",
                  "startDate" -> "2025-04-01",
                  "endDate" -> "2025-06-30",
                  "dueDate" -> "2025-06-30",
                  "amount" -> 22945.23,
                  "chargeReference" -> "XD006411191344",
                  "postingDueDate" -> "2025-06-30"
                )
              )
            )
          )
        )

        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetStandingRequestSchedule(testVatNumber)(
          OK,
          standingRequestMultipleRequestCategoriesJson
        )

        val res = get(s"/$testVatNumber/standing-requests")

        res should have(
          httpStatus(OK),
          jsonBodyAs(expectedStandingRequestRequestCategory3Only)
        )
      }
    }

    "calls to HIP returned BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetStandingRequestSchedule(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = get(s"/$testVatNumber/standing-requests")

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to HIP returned NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetStandingRequestSchedule(testVatNumber)(NOT_FOUND, Json.obj())

        val res = get(s"/$testVatNumber/standing-requests")

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to HIP returned FORBIDDEN with no body" should {
      "return FORBIDDEN with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetStandingRequestSchedule(testVatNumber)(FORBIDDEN, Json.obj())

        val res = get(s"/$testVatNumber/standing-requests")

        res should have(
          httpStatus(FORBIDDEN)
        )
      }
    }

    "calls to HIP returned anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse(mtdVatEnrolment))
        stubGetStandingRequestSchedule(testVatNumber)(
          INTERNAL_SERVER_ERROR,
          Json.obj()
        )

        val res = get(s"/$testVatNumber/standing-requests")

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }
}
