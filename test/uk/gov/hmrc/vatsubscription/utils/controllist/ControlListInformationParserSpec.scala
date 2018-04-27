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

package uk.gov.hmrc.vatsubscription.utils.controllist

import org.scalatest.EitherValues
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.TestConstants.ControlList._
import uk.gov.hmrc.vatsubscription.models.ControlListInformation._
import uk.gov.hmrc.vatsubscription.utils.controllist.ControlListInformationParser.ControlListInformationIndices._
import uk.gov.hmrc.vatsubscription.utils.controllist.ControlListInformationParser._

class ControlListInformationParserSpec extends UnitSpec with EitherValues {
  "ControlListInformation.tryParse" should {
    "parse into a ControlListInformation object" when {
      "the string is valid" in {
        tryParse(valid).isRight shouldBe true
      }

      "the string is valid and" should {
        "parse below VAT threshold correctly" in {
          tryParse(setupTestData(BELOW_VAT_THRESHOLD -> '0')).right.value.belowVatThreshold shouldBe true
          tryParse(setupTestData(BELOW_VAT_THRESHOLD -> '1')).right.value.belowVatThreshold shouldBe false
        }

        "parse missing returns correctly" in {
          tryParse(setupTestData(MISSING_RETURNS -> '0')).right.value.missingReturns shouldBe true
          tryParse(setupTestData(MISSING_RETURNS -> '1')).right.value.missingReturns shouldBe false
        }

        "parse central assessments correctly" in {
          tryParse(setupTestData(CENTRAL_ASSESSMENTS -> '0')).right.value.centralAssessments shouldBe true
          tryParse(setupTestData(CENTRAL_ASSESSMENTS -> '1')).right.value.centralAssessments shouldBe false
        }

        "parse criminal investigation inhibits correctly" in {
          tryParse(setupTestData(CRIMINAL_INVESTIGATION_INHIBITS -> '0')).right.value.criminalInvestigationInhibits shouldBe true
          tryParse(setupTestData(CRIMINAL_INVESTIGATION_INHIBITS -> '1')).right.value.criminalInvestigationInhibits shouldBe false
        }

        "parse compliance penalities or surcharges correctly" in {
          tryParse(setupTestData(COMPLIANCE_PENALTIES_OR_SURCHARGES -> '0')).right.value.compliancePenaltiesOrSurcharges shouldBe true
          tryParse(setupTestData(COMPLIANCE_PENALTIES_OR_SURCHARGES -> '1')).right.value.compliancePenaltiesOrSurcharges shouldBe false
        }

        "parse insolvency correctly" in {
          tryParse(setupTestData(INSOLVENCY -> '0')).right.value.insolvency shouldBe true
          tryParse(setupTestData(INSOLVENCY -> '1')).right.value.insolvency shouldBe false
        }

        "parse de-reg/death correctly" in {
          tryParse(setupTestData(DEREG_OR_DEATH -> '0')).right.value.deRegOrDeath shouldBe true
          tryParse(setupTestData(DEREG_OR_DEATH -> '1')).right.value.deRegOrDeath shouldBe false
        }

        "parse debt migration correctly" in {
          tryParse(setupTestData(DEBT_MIGRATION -> '0')).right.value.debtMigration shouldBe true
          tryParse(setupTestData(DEBT_MIGRATION -> '1')).right.value.debtMigration shouldBe false
        }

        "parse direct debit correctly" in {
          tryParse(setupTestData(DIRECT_DEBIT -> '0')).right.value.directDebit shouldBe true
          tryParse(setupTestData(DIRECT_DEBIT -> '1')).right.value.directDebit shouldBe false
        }

        "parse large business correctly" in {
          tryParse(setupTestData(LARGE_BUSINESS -> '0')).right.value.largeBusiness shouldBe true
          tryParse(setupTestData(LARGE_BUSINESS -> '1')).right.value.largeBusiness shouldBe false
        }

        "parse missing trader correctly" in {
          tryParse(setupTestData(MISSING_TRADER -> '0')).right.value.missingTrader shouldBe true
          tryParse(setupTestData(MISSING_TRADER -> '1')).right.value.missingTrader shouldBe false
        }

        "parse EU sales/purchases  correctly" in {
          tryParse(setupTestData(EU_SALES_OR_PURCHASES -> '0')).right.value.euSalesOrPurchases shouldBe true
          tryParse(setupTestData(EU_SALES_OR_PURCHASES -> '1')).right.value.euSalesOrPurchases shouldBe false
        }

        "parse Stagger correctly" in {
          tryParse(setupTestData(STAGGER_1 -> '1', ANNUAL_STAGGER -> '0')).right.value.staggerType shouldBe AnnualStagger
          tryParse(setupTestData(STAGGER_1 -> '1', MONTHLY_STAGGER -> '0')).right.value.staggerType shouldBe MonthlyStagger
          tryParse(setupTestData(STAGGER_1 -> '0')).right.value.staggerType shouldBe Stagger1
          tryParse(setupTestData(STAGGER_1 -> '1', STAGGER_2 -> '0')).right.value.staggerType shouldBe Stagger2
          tryParse(setupTestData(STAGGER_1 -> '1', STAGGER_3 -> '0')).right.value.staggerType shouldBe Stagger3
        }

        "parse none standard tax period correctly" in {
          tryParse(setupTestData(NONE_STANDARD_TAX_PERIOD -> '0')).right.value.nonStandardTaxPeriod shouldBe true
          tryParse(setupTestData(NONE_STANDARD_TAX_PERIOD -> '1')).right.value.nonStandardTaxPeriod shouldBe false
        }

        "parse over seas trader correctly" in {
          tryParse(setupTestData(OVERSEAS_TRADER -> '0')).right.value.overseasTrader shouldBe true
          tryParse(setupTestData(OVERSEAS_TRADER -> '1')).right.value.overseasTrader shouldBe false
        }

        "parse POA trader correctly" in {
          tryParse(setupTestData(POA_TRADER -> '0')).right.value.poaTrader shouldBe true
          tryParse(setupTestData(POA_TRADER -> '1')).right.value.poaTrader shouldBe false
        }

        "parse Business Entity correctly" in {
          tryParse(setupTestData(COMPANY -> '0')).right.value.entityType shouldBe Company
          tryParse(setupTestData(COMPANY -> '1', DIVISION -> '0')).right.value.entityType shouldBe Division
          tryParse(setupTestData(COMPANY -> '1', GROUP -> '0')).right.value.entityType shouldBe Group
          tryParse(setupTestData(COMPANY -> '1', PARTNERSHIP -> '0')).right.value.entityType shouldBe Partnership
          tryParse(setupTestData(COMPANY -> '1', PUBLIC_CORPORATION -> '0')).right.value.entityType shouldBe PublicCorporation
          tryParse(setupTestData(COMPANY -> '1', SOLE_TRADER -> '0')).right.value.entityType shouldBe SoleTrader
          tryParse(setupTestData(COMPANY -> '1', LOCAL_AUTHORITY -> '0')).right.value.entityType shouldBe LocalAuthority
          tryParse(setupTestData(COMPANY -> '1', NON_PROFIT -> '0')).right.value.entityType shouldBe NonProfitMakingBody
        }

        "parse DIFIC trader correctly" in {
          tryParse(setupTestData(DIFIC_TRADER -> '0')).right.value.dificTrader shouldBe true
          tryParse(setupTestData(DIFIC_TRADER -> '1')).right.value.dificTrader shouldBe false
        }

        "parse anything under appeal correctly" in {
          tryParse(setupTestData(ANYTHING_UNDER_APPEAL -> '0')).right.value.anythingUnderAppeal shouldBe true
          tryParse(setupTestData(ANYTHING_UNDER_APPEAL -> '1')).right.value.anythingUnderAppeal shouldBe false
        }

        "parse repayment trader correctly" in {
          tryParse(setupTestData(REPAYMENT_TRADER -> '0')).right.value.repaymentTrader shouldBe true
          tryParse(setupTestData(REPAYMENT_TRADER -> '1')).right.value.repaymentTrader shouldBe false
        }

        "parse MOSS trader correctly" in {
          tryParse(setupTestData(MOSS_TRADER -> '0')).right.value.mossTrader shouldBe true
          tryParse(setupTestData(MOSS_TRADER -> '1')).right.value.mossTrader shouldBe false
        }
      }
    }

    "fail and return InvalidFormat" when {
      "the string is not exactly 32 characters long" in {
        tryParse(valid.drop(1)) shouldBe Left(InvalidFormat)
      }
      "the string does not represent a binary number" in {
        tryParse(valid.replaceFirst("1", "2")) shouldBe Left(InvalidFormat)
      }
    }
    "fail and return EntityConflict" when {
      "multiple business entity is defined" in {
        tryParse(businessEntityConflict) shouldBe Left(EntityConflict)
      }
    }
    "fail and return StaggerConflict" when {
      "multiple stagger is defined" in {
        tryParse(staggerConflict) shouldBe Left(StaggerConflict)
      }
    }
  }

}
