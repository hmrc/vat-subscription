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

import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.utils
import uk.gov.hmrc.vatsubscription.utils.controllist.ControlListInformationParser.ControlListInformationIndices._
import ControlListInformationParser._
import uk.gov.hmrc.vatsubscription.models.ControlListInformation._


class ControlListInformationParserSpec extends UnitSpec {

  val allFalse: String = "1" * CONTROL_INFORMATION_STRING_LENGTH
  val valid: String = setupTestDataCore(allFalse)(ANNUAL_STAGGER -> '0', COMPANY -> '0')
  val businessEntityConflict: String = setupTestData(COMPANY -> '0', SOLE_TRADER -> '0')
  val staggerConflict: String = setupTestData(ANNUAL_STAGGER -> '0', STAGGER_1 -> '0')

  private def setupTestDataCore(startString: String)(amendments: (Int, Character)*): String = {
    require(amendments.forall { case (index, _) => index >= 0 && index < CONTROL_INFORMATION_STRING_LENGTH })
    require(amendments.forall { case (_, newValue) => newValue == '0' || newValue == '1' })

    amendments.foldLeft[String](startString) {
      case (pre: String, (index: Int, value: Character)) =>
        pre.substring(0, index) + value + pre.substring(index + 1, pre.length)
    }
  }

  private def setupTestData(amendments: (Int, Character)*): String = setupTestDataCore(valid)(amendments: _*)

  "ControlListInformation.tryParse" should {
    "parse into a ControlListInformation object" when {
      "the string is valid" in {
        tryParse(valid).isRight shouldBe true
      }

      "the string is valid and" should {
        "parse below VAT threshold correctly" in {
          tryParse(setupTestData(BELOW_VAT_THRESHOLD -> '0')).right.get.belowVatThreshold shouldBe true
          tryParse(setupTestData(BELOW_VAT_THRESHOLD -> '1')).right.get.belowVatThreshold shouldBe false
        }

        "parse missing returns correctly" in {
          tryParse(setupTestData(MISSING_RETURNS -> '0')).right.get.missingReturns shouldBe true
          tryParse(setupTestData(MISSING_RETURNS -> '1')).right.get.missingReturns shouldBe false
        }

        "parse central assessments correctly" in {
          tryParse(setupTestData(CENTRAL_ASSESSMENTS -> '0')).right.get.centralAssessments shouldBe true
          tryParse(setupTestData(CENTRAL_ASSESSMENTS -> '1')).right.get.centralAssessments shouldBe false
        }

        "parse criminal investigation inhibits correctly" in {
          tryParse(setupTestData(CRIMINAL_INVESTIGATION_INHIBITS -> '0')).right.get.criminalInvestigationInhibits shouldBe true
          tryParse(setupTestData(CRIMINAL_INVESTIGATION_INHIBITS -> '1')).right.get.criminalInvestigationInhibits shouldBe false
        }

        "parse compliance penalities or surcharges correctly" in {
          tryParse(setupTestData(COMPLIANCE_PENALTIES_OR_SURCHARGES -> '0')).right.get.compliancePenaltiesOrSurcharges shouldBe true
          tryParse(setupTestData(COMPLIANCE_PENALTIES_OR_SURCHARGES -> '1')).right.get.compliancePenaltiesOrSurcharges shouldBe false
        }

        "parse insolvency correctly" in {
          tryParse(setupTestData(INSOLVENCY -> '0')).right.get.insolvency shouldBe true
          tryParse(setupTestData(INSOLVENCY -> '1')).right.get.insolvency shouldBe false
        }

        "parse de-reg/death correctly" in {
          tryParse(setupTestData(DEREG_OR_DEATH -> '0')).right.get.deRegOrDeath shouldBe true
          tryParse(setupTestData(DEREG_OR_DEATH -> '1')).right.get.deRegOrDeath shouldBe false
        }

        "parse debt migration correctly" in {
          tryParse(setupTestData(DEBT_MIGRATION -> '0')).right.get.debtMigration shouldBe true
          tryParse(setupTestData(DEBT_MIGRATION -> '1')).right.get.debtMigration shouldBe false
        }

        "parse direct debit correctly" in {
          tryParse(setupTestData(DIRECT_DEBIT -> '0')).right.get.directDebit shouldBe true
          tryParse(setupTestData(DIRECT_DEBIT -> '1')).right.get.directDebit shouldBe false
        }

        "parse large business correctly" in {
          tryParse(setupTestData(LARGE_BUSINESS -> '0')).right.get.largeBusiness shouldBe true
          tryParse(setupTestData(LARGE_BUSINESS -> '1')).right.get.largeBusiness shouldBe false
        }

        "parse missing trader correctly" in {
          tryParse(setupTestData(MISSING_TRADER -> '0')).right.get.missingTrader shouldBe true
          tryParse(setupTestData(MISSING_TRADER -> '1')).right.get.missingTrader shouldBe false
        }

        "parse EU sales/purchases  correctly" in {
          tryParse(setupTestData(EU_SALES_OR_PURCHASES -> '0')).right.get.euSalesOrPurchases shouldBe true
          tryParse(setupTestData(EU_SALES_OR_PURCHASES -> '1')).right.get.euSalesOrPurchases shouldBe false
        }

        "parse Stagger correctly" in {
          tryParse(setupTestData(ANNUAL_STAGGER -> '0')).right.get.staggerType shouldBe AnnualStagger
          tryParse(setupTestData(ANNUAL_STAGGER -> '1', MONTHLY_STAGGER -> '0')).right.get.staggerType shouldBe MonthlyStagger
          tryParse(setupTestData(ANNUAL_STAGGER -> '1', STAGGER_1 -> '0')).right.get.staggerType shouldBe Stagger1
          tryParse(setupTestData(ANNUAL_STAGGER -> '1', STAGGER_2 -> '0')).right.get.staggerType shouldBe Stagger2
          tryParse(setupTestData(ANNUAL_STAGGER -> '1', STAGGER_3 -> '0')).right.get.staggerType shouldBe Stagger3
        }

        "parse none standard tax period correctly" in {
          tryParse(setupTestData(NONE_STANDARD_TAX_PERIOD -> '0')).right.get.nonStandardTaxPeriod shouldBe true
          tryParse(setupTestData(NONE_STANDARD_TAX_PERIOD -> '1')).right.get.nonStandardTaxPeriod shouldBe false
        }

        "parse over seas trader correctly" in {
          tryParse(setupTestData(OVERSEAS_TRADER -> '0')).right.get.overseasTrader shouldBe true
          tryParse(setupTestData(OVERSEAS_TRADER -> '1')).right.get.overseasTrader shouldBe false
        }

        "parse POA trader correctly" in {
          tryParse(setupTestData(POA_TRADER -> '0')).right.get.poaTrader shouldBe true
          tryParse(setupTestData(POA_TRADER -> '1')).right.get.poaTrader shouldBe false
        }

        "parse Business Entity correctly" in {
          tryParse(setupTestData(COMPANY -> '0')).right.get.entityType shouldBe Company
          tryParse(setupTestData(COMPANY -> '1', DIVISION -> '0')).right.get.entityType shouldBe Division
          tryParse(setupTestData(COMPANY -> '1', GROUP -> '0')).right.get.entityType shouldBe Group
          tryParse(setupTestData(COMPANY -> '1', PARTNERSHIP -> '0')).right.get.entityType shouldBe Partnership
          tryParse(setupTestData(COMPANY -> '1', PUBLIC_CORPORATION -> '0')).right.get.entityType shouldBe PublicCorporation
          tryParse(setupTestData(COMPANY -> '1', SOLE_TRADER -> '0')).right.get.entityType shouldBe SoleTrader
          tryParse(setupTestData(COMPANY -> '1', LOCAL_AUTHORITY -> '0')).right.get.entityType shouldBe LocalAuthority
          tryParse(setupTestData(COMPANY -> '1', NON_PROFIT -> '0')).right.get.entityType shouldBe NonProfitMakingBody
        }

        "parse DIFIC trader correctly" in {
          tryParse(setupTestData(DIFIC_TRADER -> '0')).right.get.dificTrader shouldBe true
          tryParse(setupTestData(DIFIC_TRADER -> '1')).right.get.dificTrader shouldBe false
        }

        "parse anything under appeal correctly" in {
          tryParse(setupTestData(ANYTHING_UNDER_APPEAL -> '0')).right.get.anythingUnderAppeal shouldBe true
          tryParse(setupTestData(ANYTHING_UNDER_APPEAL -> '1')).right.get.anythingUnderAppeal shouldBe false
        }

        "parse repayment trader correctly" in {
          tryParse(setupTestData(REPAYMENT_TRADER -> '0')).right.get.repaymentTrader shouldBe true
          tryParse(setupTestData(REPAYMENT_TRADER -> '1')).right.get.repaymentTrader shouldBe false
        }

        "parse MOSS trader correctly" in {
          tryParse(setupTestData(MOSS_TRADER -> '0')).right.get.mossTrader shouldBe true
          tryParse(setupTestData(MOSS_TRADER -> '1')).right.get.mossTrader shouldBe false
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
