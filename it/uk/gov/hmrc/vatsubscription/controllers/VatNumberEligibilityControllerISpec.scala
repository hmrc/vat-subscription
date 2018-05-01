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

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import uk.gov.hmrc.vatsubscription.config.featureswitch.{AlreadySubscribedCheck, MTDEligibilityCheck}
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.GetMandationStatusStub.{mandationStatusBody, stubGetMandationStatus}
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.KnownFactsAndControlListInformationStub._
import uk.gov.hmrc.vatsubscription.helpers.{ComponentSpecBase, CustomMatchers}
import uk.gov.hmrc.vatsubscription.httpparsers.{MtdEligible, MtdIneligible}
import uk.gov.hmrc.vatsubscription.models.MTDfBVoluntary

class VatNumberEligibilityControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {


  "/subscription-request/vat-number/:vatNumber/mtdfb-eligibility" when {
    "the user does not exist on ETMP" when {
      "the user is eligible" should {
        "return OK" in {
          enable(AlreadySubscribedCheck)
          enable(MTDEligibilityCheck)

          stubAuth(OK, successfulAuthResponse())
          stubGetMandationStatus(testVatNumber)(NOT_FOUND)
          stubGetKnownFactsAndControlListInformation(testVatNumber, MtdEligible(testPostCode, testDateOfRegistration))

          val res = await(get(s"/subscription-request/vat-number/$testVatNumber/mtdfb-eligibility"))

          res should have(
            httpStatus(NO_CONTENT)
          )
        }
      }
    }
    "the user does not exist on ETMP" when {
      "the user is ineligible" should {
        "return BAD_REQUEST" in {
          enable(AlreadySubscribedCheck)
          enable(MTDEligibilityCheck)

          stubAuth(OK, successfulAuthResponse())
          stubGetMandationStatus(testVatNumber)(NOT_FOUND)
          stubGetKnownFactsAndControlListInformation(testVatNumber, MtdIneligible)

          val res = await(get(s"/subscription-request/vat-number/$testVatNumber/mtdfb-eligibility"))

          res should have(
            httpStatus(BAD_REQUEST)
          )
        }
      }
    }
    "the user is already subscribed" should {
      "return CONFLICT" in {
        enable(AlreadySubscribedCheck)
        enable(MTDEligibilityCheck)

        stubAuth(OK, successfulAuthResponse())
        stubGetMandationStatus(testVatNumber)(OK, mandationStatusBody(MTDfBVoluntary))

        val res = await(get(s"/subscription-request/vat-number/$testVatNumber/mtdfb-eligibility"))

        res should have(
          httpStatus(CONFLICT)
        )
      }
    }
  }
}
