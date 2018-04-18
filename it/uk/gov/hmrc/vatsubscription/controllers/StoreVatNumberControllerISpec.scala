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

package uk.gov.hmrc.vatsubscription.controllers

import java.util.UUID

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.config.Constants
import uk.gov.hmrc.vatsubscription.config.featureswitch.AlreadySubscribedCheck
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AgentClientRelationshipsStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsubscription.httpparsers.AgentClientRelationshipsHttpParser.NoRelationshipCode
import uk.gov.hmrc.vatsubscription.repositories.SubscriptionRequestRepository
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.GetMandationStatusStub._
import uk.gov.hmrc.vatsubscription.models.{MTDfBVoluntary, NonMTDfB}

import scala.concurrent.ExecutionContext.Implicits.global

class StoreVatNumberControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val repo: SubscriptionRequestRepository = app.injector.instanceOf[SubscriptionRequestRepository]

  override def beforeEach: Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  "PUT /subscription-request/vat-number" when {
    "the user is an agent" should {
      "return CREATED when the vat number has been stored successfully" in {
        enable(AlreadySubscribedCheck)

        stubAuth(OK, successfulAuthResponse(agentEnrolment))
        stubCheckAgentClientRelationship(testAgentNumber, testVatNumber)(OK, Json.obj())
        stubGetMandationStatus(testVatNumber)(OK, mandationStatusBody(NonMTDfB))

        val res = post("/subscription-request/vat-number")(Json.obj("vatNumber" -> testVatNumber))

        res should have(
          httpStatus(CREATED),
          emptyBody
        )
      }

      "return CONFLICT when the client is already subscribed" in {
        enable(AlreadySubscribedCheck)

        stubAuth(OK, successfulAuthResponse(agentEnrolment))
        stubCheckAgentClientRelationship(testAgentNumber, testVatNumber)(OK, Json.obj())
        stubGetMandationStatus(testVatNumber)(OK, mandationStatusBody(MTDfBVoluntary))

        val res = post("/subscription-request/vat-number")(Json.obj("vatNumber" -> testVatNumber))

        res should have(
          httpStatus(CONFLICT),
          emptyBody
        )
      }

      "return FORBIDDEN when there is no relationship" in {
        stubAuth(OK, successfulAuthResponse(agentEnrolment))
        stubCheckAgentClientRelationship(testAgentNumber, testVatNumber)(NOT_FOUND, Json.obj("code" -> NoRelationshipCode))

        val res = post("/subscription-request/vat-number")(Json.obj("vatNumber" -> testVatNumber))

        res should have(
          httpStatus(FORBIDDEN),
          jsonBodyAs(Json.obj(Constants.HttpCodeKey -> NoRelationshipCode))
        )
      }

      "return BAD_REQUEST when the json is invalid" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/subscription-request/vat-number")(Json.obj())

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "the user is a principal user" should {
      "return CREATED when the vat number has been stored successfully" in {
        enable(AlreadySubscribedCheck)

        stubAuth(OK, successfulAuthResponse(vatDecEnrolment))
        stubGetMandationStatus(testVatNumber)(OK, mandationStatusBody(NonMTDfB))

        val res = post("/subscription-request/vat-number")(Json.obj("vatNumber" -> testVatNumber))

        res should have(
          httpStatus(CREATED),
          emptyBody
        )
      }

      "return CONFLICT when the user is already subscribed" in {
        enable(AlreadySubscribedCheck)

        stubAuth(OK, successfulAuthResponse(vatDecEnrolment))
        stubCheckAgentClientRelationship(testAgentNumber, testVatNumber)(OK, Json.obj())
        stubGetMandationStatus(testVatNumber)(OK, mandationStatusBody(MTDfBVoluntary))

        val res = post("/subscription-request/vat-number")(Json.obj("vatNumber" -> testVatNumber))

        res should have(
          httpStatus(CONFLICT),
          emptyBody
        )
      }

      "return FORBIDDEN when vat number does not match the one in enrolment" in {
        stubAuth(OK, successfulAuthResponse(vatDecEnrolment))

        val res = post("/subscription-request/vat-number")(Json.obj("vatNumber" -> UUID.randomUUID().toString))

        res should have(
          httpStatus(FORBIDDEN),
          jsonBodyAs(Json.obj(Constants.HttpCodeKey -> "DoesNotMatchEnrolment"))
        )
      }

      "return BAD_REQUEST when the json is invalid" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/subscription-request/vat-number")(Json.obj())

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }
  }

  "the user does not have VAT or Agent enrolments" should {
    "return FORBIDDEN" in {
      stubAuth(OK, successfulAuthResponse())

      val res = post("/subscription-request/vat-number")(Json.obj("vatNumber" -> testVatNumber))

      res should have(
        httpStatus(FORBIDDEN),
        jsonBodyAs(Json.obj(Constants.HttpCodeKey -> "InsufficientEnrolments"))
      )
    }
  }

}
