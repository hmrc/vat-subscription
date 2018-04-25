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
import uk.gov.hmrc.vatsubscription.models.ControlListInformationIndicies._


class ControlInformationSpec extends UnitSpec {

  val allFalse: String = "1" * CONTROL_INFORMATION_STRING_LENGTH
  val valid: String = setupTestDataCore(allFalse)(STAGGER_0 -> '0', COMPANY -> '0')
  val businessEntityConflict: String = setupTestData(COMPANY -> '0', SOLE_TRADER -> '0')
  val staggerConflict: String = setupTestData(STAGGER_0 -> '0', STAGGER_1 -> '0')

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
        ControlListInformation.tryParse(valid).isRight shouldBe true
      }

      "the string is valid and" should {
        "parse below VAT threshold correctly" in {
          ControlListInformation.tryParse(setupTestData(BELOW_VAT_THRESHOLD -> '0')).right.get.belowVatThreshold shouldBe true
          ControlListInformation.tryParse(setupTestData(BELOW_VAT_THRESHOLD -> '1')).right.get.belowVatThreshold shouldBe false
        }

        "parse annual accounting correctly" in {
          ControlListInformation.tryParse(setupTestData(ANNUAL_ACCOUNTING -> '0')).right.get.annualAccounting shouldBe true
          ControlListInformation.tryParse(setupTestData(ANNUAL_ACCOUNTING -> '1')).right.get.annualAccounting shouldBe false
        }

        "parse missing returns correctly" in {
          ControlListInformation.tryParse(setupTestData(MISSING_RETURNS -> '0')).right.get.missingReturns shouldBe true
          ControlListInformation.tryParse(setupTestData(MISSING_RETURNS -> '1')).right.get.missingReturns shouldBe false
        }

        "parse central assessments correctly" in {
          ControlListInformation.tryParse(setupTestData(CENTRAL_ASSESSMENTS -> '0')).right.get.centralAssessments shouldBe true
          ControlListInformation.tryParse(setupTestData(CENTRAL_ASSESSMENTS -> '1')).right.get.centralAssessments shouldBe false
        }

        "parse criminal investigation inhibits correctly" in {
          ControlListInformation.tryParse(setupTestData(CRIMINAL_INVESTIGATION_INHIBITS -> '0')).right.get.criminalInvestigationInhibits shouldBe true
          ControlListInformation.tryParse(setupTestData(CRIMINAL_INVESTIGATION_INHIBITS -> '1')).right.get.criminalInvestigationInhibits shouldBe false
        }

        "parse compliance penalities or surcharges correctly" in {
          ControlListInformation.tryParse(setupTestData(COMPLIANCE_PENALTIES_OR_SURCHARGES -> '0')).right.get.compliancePenaltiesOrSurcharges shouldBe true
          ControlListInformation.tryParse(setupTestData(COMPLIANCE_PENALTIES_OR_SURCHARGES -> '1')).right.get.compliancePenaltiesOrSurcharges shouldBe false
        }

        "parse insolvency correctly" in {
          ControlListInformation.tryParse(setupTestData(INSOLVENCY -> '0')).right.get.insolvency shouldBe true
          ControlListInformation.tryParse(setupTestData(INSOLVENCY -> '1')).right.get.insolvency shouldBe false
        }

        "parse de-reg/death correctly" in {
          ControlListInformation.tryParse(setupTestData(DEREG_OR_DEATH -> '0')).right.get.deRegOrDeath shouldBe true
          ControlListInformation.tryParse(setupTestData(DEREG_OR_DEATH -> '1')).right.get.deRegOrDeath shouldBe false
        }

        "parse debt migration correctly" in {
          ControlListInformation.tryParse(setupTestData(DEBT_MIGRATION -> '0')).right.get.debtMigration shouldBe true
          ControlListInformation.tryParse(setupTestData(DEBT_MIGRATION -> '1')).right.get.debtMigration shouldBe false
        }

        "parse direct debit correctly" in {
          ControlListInformation.tryParse(setupTestData(DIRECT_DEBIT -> '0')).right.get.directDebit shouldBe true
          ControlListInformation.tryParse(setupTestData(DIRECT_DEBIT -> '1')).right.get.directDebit shouldBe false
        }

        "parse large business correctly" in {
          ControlListInformation.tryParse(setupTestData(LARGE_BUSINESS -> '0')).right.get.largeBusiness shouldBe true
          ControlListInformation.tryParse(setupTestData(LARGE_BUSINESS -> '1')).right.get.largeBusiness shouldBe false
        }

        "parse missing trader correctly" in {
          ControlListInformation.tryParse(setupTestData(MISSING_TRADER -> '0')).right.get.missingTrader shouldBe true
          ControlListInformation.tryParse(setupTestData(MISSING_TRADER -> '1')).right.get.missingTrader shouldBe false
        }

        "parse EU sales/purchases  correctly" in {
          ControlListInformation.tryParse(setupTestData(EU_SALES_OR_PURCHASES -> '0')).right.get.euSalesOrPurchases shouldBe true
          ControlListInformation.tryParse(setupTestData(EU_SALES_OR_PURCHASES -> '1')).right.get.euSalesOrPurchases shouldBe false
        }

        "parse Stagger correctly" in {
          ControlListInformation.tryParse(setupTestData(STAGGER_0 -> '0')).right.get.staggerType shouldBe Stagger0
          ControlListInformation.tryParse(setupTestData(STAGGER_0 -> '1', STAGGER_1 -> '0')).right.get.staggerType shouldBe Stagger1
          ControlListInformation.tryParse(setupTestData(STAGGER_0 -> '1', STAGGER_2 -> '0')).right.get.staggerType shouldBe Stagger2
          ControlListInformation.tryParse(setupTestData(STAGGER_0 -> '1', STAGGER_3 -> '0')).right.get.staggerType shouldBe Stagger3
        }

        "parse none standard tax period correctly" in {
          ControlListInformation.tryParse(setupTestData(NONE_STANDARD_TAX_PERIOD -> '0')).right.get.nonStandardTaxPeriod shouldBe true
          ControlListInformation.tryParse(setupTestData(NONE_STANDARD_TAX_PERIOD -> '1')).right.get.nonStandardTaxPeriod shouldBe false
        }

        "parse over seas trader correctly" in {
          ControlListInformation.tryParse(setupTestData(OVERSEAS_TRADER -> '0')).right.get.overseasTrader shouldBe true
          ControlListInformation.tryParse(setupTestData(OVERSEAS_TRADER -> '1')).right.get.overseasTrader shouldBe false
        }

        "parse POA trader correctly" in {
          ControlListInformation.tryParse(setupTestData(POA_TRADER -> '0')).right.get.poaTrader shouldBe true
          ControlListInformation.tryParse(setupTestData(POA_TRADER -> '1')).right.get.poaTrader shouldBe false
        }

        "parse Business Entity correctly" in {
          ControlListInformation.tryParse(setupTestData(COMPANY -> '0')).right.get.entityType shouldBe Company
          ControlListInformation.tryParse(setupTestData(COMPANY -> '1', DIVISION -> '0')).right.get.entityType shouldBe Division
          ControlListInformation.tryParse(setupTestData(COMPANY -> '1', GROUP -> '0')).right.get.entityType shouldBe Group
          ControlListInformation.tryParse(setupTestData(COMPANY -> '1', PARTNERSHIP -> '0')).right.get.entityType shouldBe Partnership
          ControlListInformation.tryParse(setupTestData(COMPANY -> '1', PUBLIC_CORPORATION -> '0')).right.get.entityType shouldBe PublicCorporation
          ControlListInformation.tryParse(setupTestData(COMPANY -> '1', SOLE_TRADER -> '0')).right.get.entityType shouldBe SoleTrader
          ControlListInformation.tryParse(setupTestData(COMPANY -> '1', LOCAL_AUTHORITY -> '0')).right.get.entityType shouldBe LocalAuthority
          ControlListInformation.tryParse(setupTestData(COMPANY -> '1', NON_PROFIT -> '0')).right.get.entityType shouldBe NonProfitMakingBody
        }

        "parse DIFIC trader correctly" in {
          ControlListInformation.tryParse(setupTestData(DIFIC_TRADER -> '0')).right.get.dificTrader shouldBe true
          ControlListInformation.tryParse(setupTestData(DIFIC_TRADER -> '1')).right.get.dificTrader shouldBe false
        }

        "parse anything under appeal correctly" in {
          ControlListInformation.tryParse(setupTestData(ANYTHING_UNDER_APPEAL -> '0')).right.get.anythingUnderAppeal shouldBe true
          ControlListInformation.tryParse(setupTestData(ANYTHING_UNDER_APPEAL -> '1')).right.get.anythingUnderAppeal shouldBe false
        }

        "parse repayment trader correctly" in {
          ControlListInformation.tryParse(setupTestData(REPAYMENT_TRADER -> '0')).right.get.repaymentTrader shouldBe true
          ControlListInformation.tryParse(setupTestData(REPAYMENT_TRADER -> '1')).right.get.repaymentTrader shouldBe false
        }

        "parse MOSS trader correctly" in {
          ControlListInformation.tryParse(setupTestData(MOSS_TRADER -> '0')).right.get.mossTrader shouldBe true
          ControlListInformation.tryParse(setupTestData(MOSS_TRADER -> '1')).right.get.mossTrader shouldBe false
        }
      }
    }

    "fail and return InvalidFormat" when {
      "the string is not exactly 32 characters long" in {
        ControlListInformation.tryParse(valid.drop(1)) shouldBe Left(InvalidFormat)
      }
      "the string does not represent a binary number" in {
        ControlListInformation.tryParse(valid.replaceFirst("1", "2")) shouldBe Left(InvalidFormat)
      }
    }
    "fail and return EntityConflict" when {
      "multiple business entity is defined" in {
        ControlListInformation.tryParse(businessEntityConflict) shouldBe Left(EntityConflict)
      }
    }
    "fail and return StaggerConflict" when {
      "multiple stagger is defined" in {
        ControlListInformation.tryParse(staggerConflict) shouldBe Left(StaggerConflict)
      }
    }
  }

}
