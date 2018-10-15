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

package uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.deregistration

import play.api.libs.json.{JsError, JsString, JsSuccess, Json}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.DeregistrationInfoTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.{DeregistrationInfoTestConstants, TurnoverBelowThresholdTestConstants}

class DeregistrationInfoSpec extends UnitSpec {

  "DeregInfo" when {

    "calling the .validate method" should {

      "Return 'unexpxected turnoverBelowThreshold object when journey is ceasedTrading'" in {
        val invalid = DeregistrationInfo(
          deregReason = CeasedTrading,
          deregDate = Some(DeregistrationInfoTestConstants.deregDate),
          deregLaterDate = None,
          turnoverBelowThreshold = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdModelMax),
          optionToTax = false,
          intendSellCapitalAssets = false,
          additionalTaxInvoices = false,
          cashAccountingScheme = false,
          optionToTaxValue = None,
          stocksValue = None,
          capitalAssetsValue = None
        )

        invalid.validate shouldBe JsError("unexpxected turnoverBelowThreshold object when journey is ceasedTrading")
      }

      "Return 'deregDate is mandatory when journey is ceasedTrading'" in {
        val invalid = DeregistrationInfo(
          deregReason = CeasedTrading,
          deregDate = None,
          deregLaterDate = None,
          turnoverBelowThreshold = None,
          optionToTax = false,
          intendSellCapitalAssets = false,
          additionalTaxInvoices = false,
          cashAccountingScheme = false,
          optionToTaxValue = None,
          stocksValue = None,
          capitalAssetsValue = None
        )

        invalid.validate shouldBe JsError("deregDate is mandatory when journey is ceasedTrading")
      }

      "Return 'optionToTaxValue is mandatory when optionToTax is true'" in {
        val invalid = DeregistrationInfo(
          deregReason = CeasedTrading,
          deregDate = Some(DeregistrationInfoTestConstants.deregDate),
          deregLaterDate = None,
          turnoverBelowThreshold = None,
          optionToTax = true,
          intendSellCapitalAssets = false,
          additionalTaxInvoices = false,
          cashAccountingScheme = false,
          optionToTaxValue = None,
          stocksValue = None,
          capitalAssetsValue = None
        )

        invalid.validate shouldBe JsError("optionToTaxValue is mandatory when optionToTax is true")
      }

      "Return 'capitalAssetsValue is mandatory when intendSellCapitalAssets is true'" in {
        val invalid = DeregistrationInfo(
          deregReason = CeasedTrading,
          deregDate = Some(DeregistrationInfoTestConstants.deregDate),
          deregLaterDate = None,
          turnoverBelowThreshold = None,
          optionToTax = false,
          intendSellCapitalAssets = true,
          additionalTaxInvoices = false,
          cashAccountingScheme = false,
          optionToTaxValue = None,
          stocksValue = None,
          capitalAssetsValue = None
        )

        invalid.validate shouldBe JsError("capitalAssetsValue is mandatory when intendSellCapitalAssets is true")
      }

      "Return 'turnoverBelowThreshold is mandatory when deregReason is belowThreshold'" in {
        val invalid = DeregistrationInfo(
          deregReason = ReducedTurnover,
          deregDate = None,
          deregLaterDate = None,
          turnoverBelowThreshold = None,
          optionToTax = false,
          intendSellCapitalAssets = false,
          additionalTaxInvoices = false,
          cashAccountingScheme = false,
          optionToTaxValue = None,
          stocksValue = None,
          capitalAssetsValue = None
        )

        invalid.validate shouldBe JsError("turnoverBelowThreshold is mandatory when deregReason is belowThreshold")
      }

      "Return itself if it validates" in {
        deregistrationInfoReducedTurnoverModel.validate shouldBe JsSuccess(deregistrationInfoReducedTurnoverModel)
      }

    }

    "deserializing JSON" should {

      "return correct DeregistrationInfo model when valid JSON max is received" in {
        deregInfoCeasedTradingFrontendJson.as[DeregistrationInfo] shouldBe deregInfoCeasedTradingModel
      }

      "return correct DeregistrationInfo model when valid JSON min is received" in {
        deregInfoReducedTurnoverFrontendJson.as[DeregistrationInfo] shouldBe deregistrationInfoReducedTurnoverModel
      }

      "return JsError when invalid JSON is received" in {
        JsString("banana").validate[DeregistrationInfo].isError shouldBe true
      }
    }

    "serializing to JSON" should {

      "for turnoverBelowThresholdModelMax output correct JSON" in {
        Json.toJson(deregInfoCeasedTradingModel) shouldBe deregInfoCeasedTradingDESJson
      }

      "for turnoverBelowThresholdModelMin output correct JSON" in {
        Json.toJson(deregistrationInfoReducedTurnoverModel) shouldBe deregInfoReducedTurnoverDESJson
      }
    }
  }
}