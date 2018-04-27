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

import uk.gov.hmrc.vatsubscription.models.ControlListInformation.{BusinessEntity, Stagger}

object ControlListIneligibilityMessages {
  val belowVatThresholdMessage: String = "Below VAT threshold"
  val missingReturnsMessage: String = "Missing returns"
  val centralAssessmentsMessage: String = "Central assessments"
  val criminalInvestigationInhibitsMessage: String = "Criminal investigation inhibits"
  val compliancePenaltiesOrSurchargesMessage: String = "Compliance penalties or surcharges"
  val insolvencyMessage: String = "Insolvency"
  val deRegOrDeathMessage: String = "DeReg or death"
  val debtMigrationMessage: String = "Debt migration"
  val directDebitMessage: String = "Direct debit"
  val euSalesOrPurchasesMessage: String = "EU sales or purchases"
  val largeBusinessMessage: String = "Large business"
  val missingTraderMessage: String = "Missing trader"

  def invalidStaggerTypeMessage(staggerType: Stagger): String = s"Invalid stagger type: $staggerType"

  val nonStandardTaxPeriodMessage: String = "Non standard tax period"
  val overseasTraderMessage: String = "Overseas trader"
  val poaTraderMessage: String = "POA trader"

  def invalidEntityTypeMessage(entityType: BusinessEntity): String = s"Invalid entity type: $entityType"

  val dificTraderMessage: String = "DIFIC trader"
  val anythingUnderAppealMessage = "Anything under appeal"
  val repaymentTraderMessage = "Repayment trader"
  val mossTraderMessage = "MOSS trader"
}
