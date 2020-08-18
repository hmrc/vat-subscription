/*
 * Copyright 2020 HM Revenue & Customs
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

package models.updateVatSubscription.request.deregistration

import play.api.libs.json.{JsError, JsString, JsSuccess, Json}
import uk.gov.hmrc.play.test.UnitSpec
import helpers.DeregistrationInfoTestConstants._
import helpers.{TurnoverBelowThresholdTestConstants, ZeroRatedExmpApplicationConstants}

class DeregistrationInfoSpec extends UnitSpec {

  val invalidDeregInfo = DeregistrationInfo(
    deregReason = CeasedTrading,
    deregDate = None,
    deregLaterDate = None,
    turnoverBelowThreshold = None,
    zeroRatedExmpApplication = None,
    optionToTax = false,
    intendSellCapitalAssets = false,
    additionalTaxInvoices = false,
    cashAccountingScheme = false,
    optionToTaxValue = None,
    stocksValue = None,
    capitalAssetsValue = None,
    transactorOrCapacitorEmail = None
  )

  "DeregInfo" when {

    "calling the .validate method" should {

      "Return 'unexpected turnoverBelowThreshold object when journey is ceasedTrading'" in {
        val invalid = invalidDeregInfo.copy(
          turnoverBelowThreshold = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdModelMax)
        )

        invalid.validate shouldBe JsError("unexpected turnoverBelowThreshold object when journey is ceasedTrading")
      }

      "Return 'deregDate is mandatory when journey is ceasedTrading'" in {
        invalidDeregInfo.validate shouldBe JsError("deregDate is mandatory when journey is ceasedTrading")
      }

      "Return 'optionToTaxValue is mandatory when optionToTax is true'" in {
        val invalid = invalidDeregInfo.copy(
          optionToTax = true
        )

        invalid.validate shouldBe JsError("optionToTaxValue is mandatory when optionToTax is true")
      }

      "Return 'capitalAssetsValue is mandatory when intendSellCapitalAssets is true'" in {
        val invalid = invalidDeregInfo.copy(
          intendSellCapitalAssets = true
        )

        invalid.validate shouldBe JsError("capitalAssetsValue is mandatory when intendSellCapitalAssets is true")
      }

      "Return 'turnoverBelowThreshold is mandatory when deregReason is belowThreshold'" in {
        val invalid = invalidDeregInfo.copy(
          deregReason = ReducedTurnover
        )

        invalid.validate shouldBe JsError("turnoverBelowThreshold is mandatory when deregReason is belowThreshold")
      }

      "Return 'zeroRatedExmpApplication is mandatory when deregReason is zeroRated'" in {
        val invalid = invalidDeregInfo.copy(
          deregReason = ZeroRated
        )

        invalid.validate shouldBe JsError("zeroRatedExmpApplication is mandatory when deregReason is zeroRated")
      }

      "Return 'unexpected turnoverBelowThreshold object was provided when deregReason is exemptOnly'" in {
        val invalid = invalidDeregInfo.copy(
          deregReason = ExemptOnly,
          turnoverBelowThreshold = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdModelMax)
        )

        invalid.validate shouldBe
          JsError("unexpected turnoverBelowThreshold object was provided when deregReason is exemptOnly")
      }

      "Return 'unexpected zeroRatedExmpApplication object was provided when deregReason is exemptOnly'" in {
        val invalid = invalidDeregInfo.copy(
          deregReason = ExemptOnly,
          zeroRatedExmpApplication = Some(ZeroRatedExmpApplicationConstants.zeroRatedExmpApplicationFrontendModelMax)
        )

        invalid.validate shouldBe
          JsError("unexpected zeroRatedExmpApplication object was provided when deregReason is exemptOnly")
      }

      "Return itself if it validates" in {
        deregistrationInfoReducedTurnoverModel.validate shouldBe JsSuccess(deregistrationInfoReducedTurnoverModel)
      }
    }

    "deserializing JSON" should {

      "return correct DeregistrationInfo model when valid JSON that has deregReason CeasedTrading is received" in {
        deregInfoCeasedTradingFrontendJson.as[DeregistrationInfo] shouldBe deregInfoCeasedTradingModel
      }

      "return correct DeregistrationInfo model when valid JSON that has deregReason ReducedTurnover is received" in {
        deregInfoReducedTurnoverFrontendJson.as[DeregistrationInfo] shouldBe deregistrationInfoReducedTurnoverModel
      }

      "return correct DeregistrationInfo model when valid JSON that has deregReason ZeroRated is received" in {
        deregInfoZeroRatedExmpApplicationFrontendJson.as[DeregistrationInfo] shouldBe deregistrationInfoZeroRatedExmpApplicationModel
      }

      "return correct DeregistrationInfo model when valid JSON that has deregReason ExemptOnly is received" in {
        deregInfoExemptOnlyFrontendJson.as[DeregistrationInfo] shouldBe deregistrationInfoExemptOnlyModel
      }

      "return JsError when invalid JSON is received" in {
        JsString("banana").validate[DeregistrationInfo].isError shouldBe true
      }
    }

    "serializing to JSON" should {

      "for deregReason CeasedTrading output correct JSON" in {
        Json.toJson(deregInfoCeasedTradingModel) shouldBe deregInfoCeasedTradingDESJson
      }

      "for deregReason ReducedTurnover output correct JSON" in {
        Json.toJson(deregistrationInfoReducedTurnoverModel) shouldBe deregInfoReducedTurnoverDESJson
      }

      "for deregReason ZeroRated output correct JSON" in {
        Json.toJson(deregistrationInfoZeroRatedExmpApplicationModel) shouldBe deregInfoZeroRatedExmpApplicationDESJson
      }

      "for deregReason ExemptOnly output correct JSON" in {
        Json.toJson(deregistrationInfoExemptOnlyModel) shouldBe deregInfoExemptOnlyDESJson
      }
    }
  }
}
