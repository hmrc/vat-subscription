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

package uk.gov.hmrc.vatsubscription.models

import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.models.ControlListInformation._
import uk.gov.hmrc.vatsubscription.utils.controllist.ControlListIneligibilityMessages._

class ControlListInformationSpec extends UnitSpec {
  private def valid(entityType: BusinessEntity) = ControlListInformation(
    belowVatThreshold = false,
    missingReturns = false,
    centralAssessments = false,
    criminalInvestigationInhibits = false,
    compliancePenaltiesOrSurcharges = false,
    insolvency = false,
    deRegOrDeath = false,
    debtMigration = false,
    directDebit = false,
    euSalesOrPurchases = false,
    largeBusiness = false,
    missingTrader = false,
    staggerType = Stagger1,
    nonStandardTaxPeriod = false,
    overseasTrader = false,
    poaTrader = false,
    entityType = entityType,
    dificTrader = false,
    anythingUnderAppeal = false,
    repaymentTrader = false,
    mossTrader = false
  )

  private val invalid = ControlListInformation(
    belowVatThreshold = false,
    missingReturns = true,
    centralAssessments = true,
    criminalInvestigationInhibits = true,
    compliancePenaltiesOrSurcharges = true,
    insolvency = true,
    deRegOrDeath = true,
    debtMigration = true,
    directDebit = true,
    euSalesOrPurchases = true,
    largeBusiness = true,
    missingTrader = true,
    staggerType = AnnualStagger,
    nonStandardTaxPeriod = true,
    overseasTrader = true,
    poaTrader = true,
    entityType = Division,
    dificTrader = true,
    anythingUnderAppeal = true,
    repaymentTrader = true,
    mossTrader = true
  )

  "isValid" when {
    "the user is a sole trader with no ineligible data and a stagger type of Stagger1" should {
      "return Valid" in {
        valid(SoleTrader).validate shouldBe eligible
      }
    }
    "the user is a limited company with no ineligible data and a stagger type of Stagger1" should {
      "return Valid" in {
        valid(Company).validate shouldBe eligible
      }
    }
    "the user has a stagger type of Stagger 2" should {
      "return Valid" in {
        valid(SoleTrader).copy(staggerType = Stagger2).validate shouldBe eligible
      }
    }
    "the user has a stagger type of Stagger 3" should {
      "return Valid" in {
        valid(SoleTrader).copy(staggerType = Stagger3).validate shouldBe eligible
      }
    }
    "the user is below the VAT threshold" should {
      "return Valid" in {
        valid(SoleTrader).copy(belowVatThreshold = true).validate shouldBe eligible
      }
    }
    "the user has missing returns" should {
      "return Invalid wtih a missing returns message" in {
        valid(SoleTrader).copy(missingReturns = true).validate shouldBe ineligible(missingReturnsMessage)
      }
    }
    "the user has central assessments" should {
      "return Invalid with a central assessments message" in {
        valid(SoleTrader).copy(centralAssessments = true).validate shouldBe ineligible(centralAssessmentsMessage)
      }
    }
    "the user has criminal investigation inhibits" should {
      "return Invalid with a criminal investigations message" in {
        valid(SoleTrader).copy(criminalInvestigationInhibits = true).validate shouldBe ineligible(criminalInvestigationInhibitsMessage)
      }
    }
    "the user has compliance penalties or surcharges" should {
      "return Invalid with a compliance penalties or surcharges message" in {
        valid(SoleTrader).copy(compliancePenaltiesOrSurcharges = true).validate shouldBe ineligible(compliancePenaltiesOrSurchargesMessage)
      }
    }
    "the user is or was insolvent" should {
      "return Invalid with an insolvency message" in {
        valid(SoleTrader).copy(insolvency = true).validate shouldBe ineligible(insolvencyMessage)
      }
    }
    "the user is de-registered or dead" should {
      "return Invalid with a deRegOrDeathMessage" in {
        valid(SoleTrader).copy(deRegOrDeath = true).validate shouldBe ineligible(deRegOrDeathMessage)
      }
    }
    "the user is debt migration" should {
      "return Invalid with a debtMigrationMessage" in {
        valid(SoleTrader).copy(debtMigration = true).validate shouldBe ineligible(debtMigrationMessage)
      }
    }
    "the user pays by direct debit" should {
      "return Invalid with a directDebitMessage" in {
        valid(SoleTrader).copy(directDebit = true).validate shouldBe ineligible(directDebitMessage)
      }
    }
    "the user has EU sales or purchases" should {
      "return Invalid with an euSalesOrPurchasesMessage" in {
        valid(SoleTrader).copy(euSalesOrPurchases = true).validate shouldBe ineligible(euSalesOrPurchasesMessage)
      }
    }
    "the user is a large business" should {
      "return Invalid with a largeBusinessMessage" in {
        valid(SoleTrader).copy(largeBusiness = true).validate shouldBe ineligible(largeBusinessMessage)
      }
    }
    "the user is a missing trader" should {
      "return Invalid with a missingTraderMessage" in {
        valid(SoleTrader).copy(missingTrader = true).validate shouldBe ineligible(missingTraderMessage)
      }
    }
    "the user has a monthly stagger" should {
      "return Invalid with an invalidStaggerTypeMessage for MonthlyStagger" in {
        valid(SoleTrader).copy(staggerType = MonthlyStagger).validate shouldBe ineligible(invalidStaggerTypeMessage(MonthlyStagger))
      }
    }
    "the user has an annual stagger" should {
      "return Invalid with an invalidStaggerTypeMessage for AnnualStagger" in {
        valid(SoleTrader).copy(staggerType = AnnualStagger).validate shouldBe ineligible(invalidStaggerTypeMessage(AnnualStagger))
      }
    }
    "the user has a non standard tax period" should {
      "return Invalid with an nonStandardTaxPeriodMessage" in {
        valid(SoleTrader).copy(nonStandardTaxPeriod = true).validate shouldBe ineligible(nonStandardTaxPeriodMessage)
      }
    }
    "the user is an overseas trader" should {
      "return Invalid with an overseasTraderMessage" in {
        valid(SoleTrader).copy(overseasTrader = true).validate shouldBe ineligible(overseasTraderMessage)
      }
    }
    "the user is a POA trader" should {
      "return Invalid with an poaTraderMessage" in {
        valid(SoleTrader).copy(poaTrader = true).validate shouldBe ineligible(poaTraderMessage)
      }
    }
    "the user has an entity type of Division" should {
      "return Invalid with an invalidEntityTypeMessage for Division" in {
        valid(Division).validate shouldBe ineligible(invalidEntityTypeMessage(Division))
      }
    }
    "the user has an entity type of Group" should {
      "return Invalid with an invalidEntityTypeMessage for Group" in {
        valid(Group).validate shouldBe ineligible(invalidEntityTypeMessage(Group))
      }
    }
    "the user has an entity type of Partnership" should {
      "return Invalid with an invalidEntityTypeMessage for Partnership" in {
        valid(Partnership).validate shouldBe ineligible(invalidEntityTypeMessage(Partnership))
      }
    }
    "the user has an entity type of PublicCorporation" should {
      "return Invalid with an invalidEntityTypeMessage for PublicCorporation" in {
        valid(PublicCorporation).validate shouldBe ineligible(invalidEntityTypeMessage(PublicCorporation))
      }
    }
    "the user has an entity type of LocalAuthority" should {
      "return Invalid with an invalidEntityTypeMessage for LocalAuthority" in {
        valid(LocalAuthority).validate shouldBe ineligible(invalidEntityTypeMessage(LocalAuthority))
      }
    }
    "the user has an entity type of NonProfitMakingBody" should {
      "return Invalid with an invalidEntityTypeMessage for NonProfitMakingBody" in {
        valid(NonProfitMakingBody).validate shouldBe ineligible(invalidEntityTypeMessage(NonProfitMakingBody))
      }
    }
    "the user is a DIFIC trader" should {
      "return Invalid with a dificTraderMessage" in {
        valid(SoleTrader).copy(dificTrader = true).validate shouldBe ineligible(dificTraderMessage)
      }
    }
    "the user has anything under appeal" should {
      "return Invalid with an anythingUnderAppealMessage" in {
        valid(SoleTrader).copy(anythingUnderAppeal = true).validate shouldBe ineligible(anythingUnderAppealMessage)
      }
    }
    "the user is a repayment trader" should {
      "return Invalid with a repaymentTraderMessage" in {
        valid(SoleTrader).copy(repaymentTrader = true).validate shouldBe ineligible(repaymentTraderMessage)
      }
    }
    "the user is a MOSS trader" should {
      "return Invalid with a mossTraderMessage" in {
        valid(SoleTrader).copy(mossTrader = true).validate shouldBe ineligible(mossTraderMessage)
      }
    }
    "the user is ineligible for multiple reasons" should {
      "return Invalid with all relevant error messages" in {
        invalid.validate shouldBe ineligible(
          missingReturnsMessage,
          centralAssessmentsMessage,
          criminalInvestigationInhibitsMessage,
          compliancePenaltiesOrSurchargesMessage,
          insolvencyMessage,
          deRegOrDeathMessage,
          debtMigrationMessage,
          directDebitMessage,
          euSalesOrPurchasesMessage,
          largeBusinessMessage,
          missingTraderMessage,
          invalidStaggerTypeMessage(AnnualStagger),
          nonStandardTaxPeriodMessage,
          overseasTraderMessage,
          poaTraderMessage,
          invalidEntityTypeMessage(Division),
          dificTraderMessage,
          anythingUnderAppealMessage,
          repaymentTraderMessage,
          mossTraderMessage
        )
      }
    }
  }
}
