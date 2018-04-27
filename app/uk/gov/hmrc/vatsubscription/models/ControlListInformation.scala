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

import cats._
import cats.data.Validated._
import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import uk.gov.hmrc.vatsubscription.models.ControlListInformation._
import uk.gov.hmrc.vatsubscription.utils.controllist.ControlListIneligibilityMessages._

case class ControlListInformation(belowVatThreshold: Boolean,
                                  missingReturns: Boolean,
                                  centralAssessments: Boolean,
                                  criminalInvestigationInhibits: Boolean,
                                  compliancePenaltiesOrSurcharges: Boolean,
                                  insolvency: Boolean,
                                  deRegOrDeath: Boolean,
                                  debtMigration: Boolean,
                                  directDebit: Boolean,
                                  euSalesOrPurchases: Boolean,
                                  largeBusiness: Boolean,
                                  missingTrader: Boolean,
                                  staggerType: Stagger,
                                  nonStandardTaxPeriod: Boolean,
                                  overseasTrader: Boolean,
                                  poaTrader: Boolean,
                                  entityType: BusinessEntity,
                                  dificTrader: Boolean,
                                  anythingUnderAppeal: Boolean,
                                  repaymentTrader: Boolean,
                                  mossTrader: Boolean
                                 ) {
  // scalastyle:off cyclomatic.complexity
  def validate: ValidatedType = {
    Monoid.combineAll(List(
      if (missingReturns) ineligible(missingReturnsMessage) else eligible,
      if (centralAssessments) ineligible(centralAssessmentsMessage) else eligible,
      if (criminalInvestigationInhibits) ineligible(criminalInvestigationInhibitsMessage) else eligible,
      if (compliancePenaltiesOrSurcharges) ineligible(compliancePenaltiesOrSurchargesMessage) else eligible,
      if (insolvency) ineligible(insolvencyMessage) else eligible,
      if (deRegOrDeath) ineligible(deRegOrDeathMessage) else eligible,
      if (debtMigration) ineligible(debtMigrationMessage) else eligible,
      if (directDebit) ineligible(directDebitMessage) else eligible,
      if (euSalesOrPurchases) ineligible(euSalesOrPurchasesMessage) else eligible,
      if (largeBusiness) ineligible(largeBusinessMessage) else eligible,
      if (missingTrader) ineligible(missingTraderMessage) else eligible,
      staggerType match {
        case AnnualStagger | MonthlyStagger => ineligible(invalidStaggerTypeMessage(staggerType))
        case _ => eligible
      },
      if (nonStandardTaxPeriod) ineligible(nonStandardTaxPeriodMessage) else eligible,
      if (overseasTrader) ineligible(overseasTraderMessage) else eligible,
      if (poaTrader) ineligible(poaTraderMessage) else eligible,
      entityType match {
        case SoleTrader | Company => eligible
        case entity => ineligible(invalidEntityTypeMessage(entity))
      },
      if (dificTrader) ineligible(dificTraderMessage) else eligible,
      if (anythingUnderAppeal) ineligible(anythingUnderAppealMessage) else eligible,
      if (repaymentTrader) ineligible(repaymentTraderMessage) else eligible,
      if (mossTrader) ineligible(mossTraderMessage) else eligible
    ))
  }

  // scalastyle:on cyclomatic.complexity

}

object ControlListInformation {
  type ValidatedType = Validated[NonEmptyList[String], Unit]

  val eligible: ValidatedType = Validated.Valid(())

  def ineligible(errorMessage: String, additionalErrorMessages: String*): ValidatedType =
    Invalid(NonEmptyList.apply(errorMessage, additionalErrorMessages.toList))

  sealed trait Stagger

  case object AnnualStagger extends Stagger

  case object MonthlyStagger extends Stagger

  case object Stagger1 extends Stagger

  case object Stagger2 extends Stagger

  case object Stagger3 extends Stagger


  sealed trait BusinessEntity

  case object Company extends BusinessEntity

  case object Division extends BusinessEntity

  case object Group extends BusinessEntity

  case object Partnership extends BusinessEntity

  case object PublicCorporation extends BusinessEntity

  case object SoleTrader extends BusinessEntity

  case object LocalAuthority extends BusinessEntity

  case object NonProfitMakingBody extends BusinessEntity

}




