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

import uk.gov.hmrc.vatsubscription.models.ControlListInformation.{BusinessEntity, Stagger}

import scala.util.Try

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
                                 )

object ControlListInformation {

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


  sealed trait ControlListParseError

  case object InvalidFormat extends ControlListParseError

  case object EntityConflict extends ControlListParseError

  case object StaggerConflict extends ControlListParseError

  import ControlListInformationIndicies._

  private[models] val CONTROL_INFORMATION_STRING_LENGTH = 32

  private def parseStagger(controlList: Seq[Boolean]): Either[StaggerConflict.type, Stagger] = {
    (controlList(ANNUAL_STAGGER),
      controlList(MONTHLY_STAGGER),
      controlList(STAGGER_1),
      controlList(STAGGER_2),
      controlList(STAGGER_3)) match {
      case (true, false, false, false, false) => Right(AnnualStagger)
      case (false, true, false, false, false) => Right(MonthlyStagger)
      case (false, false, true, false, false) => Right(Stagger1)
      case (false, false, false, true, false) => Right(Stagger2)
      case (false, false, false, false, true) => Right(Stagger3)
      case _ => Left(StaggerConflict)
    }
  }

  private def parseBusinessEntity(controlList: Seq[Boolean]): Either[EntityConflict.type, BusinessEntity] =
    (controlList(COMPANY),
      controlList(DIVISION),
      controlList(GROUP),
      controlList(PARTNERSHIP),
      controlList(PUBLIC_CORPORATION),
      controlList(SOLE_TRADER),
      controlList(LOCAL_AUTHORITY),
      controlList(NON_PROFIT)
    ) match {
      case (true, false, false, false, false, false, false, false) => Right(Company)
      case (false, true, false, false, false, false, false, false) => Right(Division)
      case (false, false, true, false, false, false, false, false) => Right(Group)
      case (false, false, false, true, false, false, false, false) => Right(Partnership)
      case (false, false, false, false, true, false, false, false) => Right(PublicCorporation)
      case (false, false, false, false, false, true, false, false) => Right(SoleTrader)
      case (false, false, false, false, false, false, true, false) => Right(LocalAuthority)
      case (false, false, false, false, false, false, false, true) => Right(NonProfitMakingBody)
      case _ => Left(EntityConflict)
    }

  def tryParse(controlList: String): Either[ControlListParseError, ControlListInformation] = {
    val controlListSeq: Seq[Boolean] = Try(controlList.map {
      case '0' => true
      case '1' => false
      case _ => throw new IllegalArgumentException("control list string must be binary")
    }).getOrElse(Seq.empty)

    if (controlListSeq.length != CONTROL_INFORMATION_STRING_LENGTH) Left(InvalidFormat)
    else {
      (parseStagger(controlListSeq), parseBusinessEntity(controlListSeq)) match {
        case (Left(err), _) => Left(err)
        case (_, Left(err)) => Left(err)
        case (Right(stagger), Right(businessEntity)) =>
          Right(ControlListInformation(
            belowVatThreshold = controlListSeq(BELOW_VAT_THRESHOLD),
            missingReturns = controlListSeq(MISSING_RETURNS),
            centralAssessments = controlListSeq(CENTRAL_ASSESSMENTS),
            criminalInvestigationInhibits = controlListSeq(CRIMINAL_INVESTIGATION_INHIBITS),
            compliancePenaltiesOrSurcharges = controlListSeq(COMPLIANCE_PENALTIES_OR_SURCHARGES),
            insolvency = controlListSeq(INSOLVENCY),
            deRegOrDeath = controlListSeq(DEREG_OR_DEATH),
            debtMigration = controlListSeq(DEBT_MIGRATION),
            directDebit = controlListSeq(DIRECT_DEBIT),
            euSalesOrPurchases = controlListSeq(EU_SALES_OR_PURCHASES),
            largeBusiness = controlListSeq(LARGE_BUSINESS),
            missingTrader = controlListSeq(MISSING_TRADER),
            staggerType = stagger,
            nonStandardTaxPeriod = controlListSeq(NONE_STANDARD_TAX_PERIOD),
            overseasTrader = controlListSeq(OVERSEAS_TRADER),
            poaTrader = controlListSeq(POA_TRADER),
            entityType = businessEntity,
            dificTrader = controlListSeq(DIFIC_TRADER),
            anythingUnderAppeal = controlListSeq(ANYTHING_UNDER_APPEAL),
            repaymentTrader = controlListSeq(REPAYMENT_TRADER),
            mossTrader = controlListSeq(MOSS_TRADER)
          ))
      }
    }
  }

}

object ControlListInformationIndicies {
  val BELOW_VAT_THRESHOLD = 0
  val ANNUAL_STAGGER = 1
  val MISSING_RETURNS = 2
  val CENTRAL_ASSESSMENTS = 3
  val CRIMINAL_INVESTIGATION_INHIBITS = 4
  val COMPLIANCE_PENALTIES_OR_SURCHARGES = 5
  val INSOLVENCY = 6
  val DEREG_OR_DEATH = 7
  val DEBT_MIGRATION = 8
  val DIRECT_DEBIT = 9
  val EU_SALES_OR_PURCHASES = 10
  val LARGE_BUSINESS = 11
  val MISSING_TRADER = 12
  val MONTHLY_STAGGER = 13
  val NONE_STANDARD_TAX_PERIOD = 14
  val OVERSEAS_TRADER = 15
  val POA_TRADER = 16
  val STAGGER_1 = 17
  val STAGGER_2 = 18
  val STAGGER_3 = 19
  val COMPANY = 20
  val DIVISION = 21
  val GROUP = 22
  val PARTNERSHIP = 23
  val PUBLIC_CORPORATION = 24
  val SOLE_TRADER = 25
  val LOCAL_AUTHORITY = 26
  val NON_PROFIT = 27
  val DIFIC_TRADER = 28
  val ANYTHING_UNDER_APPEAL = 29
  val REPAYMENT_TRADER = 30
  val MOSS_TRADER = 31
}
