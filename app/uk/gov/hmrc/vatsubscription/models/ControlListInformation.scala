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
                                  annualAccounting: Boolean,
                                  missingReturns: Boolean,
                                  centralAssessments: Boolean,
                                  criminalInvestigationInhibits: Boolean,
                                  compliancePenaltiesOrSurcharges: Boolean,
                                  insolvency: Boolean,
                                  deRegOrDeath: Boolean,
                                  debtMigration: Boolean,
                                  directDebit: Boolean,
                                  eu: Boolean,
                                  largeBusiness: Boolean,
                                  missing: Boolean,
                                  staggerType: Stagger,
                                  nonStandard: Boolean,
                                  overseas: Boolean,
                                  poa: Boolean,
                                  entityType: BusinessEntity,
                                  dific: Boolean,
                                  anythingUnderAppeal: Boolean,
                                  repaymentCustomer: Boolean,
                                  moss: Boolean
                                 )

object ControlListInformation {

  sealed trait Stagger

  case object Stagger0 extends Stagger

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


  def tryParse(controlList: String): Either[ControlListParseError, ControlListInformation] = {
    val controlListSeq: Seq[Boolean] = Try(controlList.map {
      case '0' => true
      case '1' => false
      case _ => throw new IllegalArgumentException("control list string must be binary")
    }).getOrElse(Seq.empty)

    if (controlListSeq.length != 32) Left(InvalidFormat)
    else {
      import ControlListInformationIndicies._

      lazy val parseStagger: Either[StaggerConflict.type, Stagger] =
        (controlListSeq(STAGGER_0),
          controlListSeq(STAGGER_1),
          controlListSeq(STAGGER_2),
          controlListSeq(STAGGER_3)) match {
          case (true, false, false, false) => Right(Stagger0)
          case (false, true, false, false) => Right(Stagger1)
          case (false, false, true, false) => Right(Stagger2)
          case (false, false, false, true) => Right(Stagger3)
          case _ => Left(StaggerConflict)
        }

      lazy val parseBusinessEntity: Either[EntityConflict.type, BusinessEntity] =
        (controlListSeq(COMPANY),
          controlListSeq(DIVISION),
          controlListSeq(GROUP),
          controlListSeq(PARTNERSHIP),
          controlListSeq(PUBLIC_CORPORATION),
          controlListSeq(SOLE_TRADER),
          controlListSeq(LOCAL_AUTHORITY),
          controlListSeq(NON_PROFIT)
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

      (parseStagger, parseBusinessEntity) match {
        case (Left(err), _) => Left(err)
        case (_, Left(err)) => Left(err)
        case (Right(stagger), Right(businessEntity)) =>
          Right(ControlListInformation(
            belowVatThreshold = controlListSeq(VAT_THRESHOLD),
            annualAccounting = controlListSeq(ANNUAL_ACCOUNTING),
            missingReturns = controlListSeq(MISSING_RETURNS),
            centralAssessments = controlListSeq(CENTRAL_ASSESSMENTS),
            criminalInvestigationInhibits = controlListSeq(CRIMINAL_INVESTIGATION_INHIBITS),
            compliancePenaltiesOrSurcharges = controlListSeq(COMPLIANCE_PENALTIES_OR_SURCHARGES),
            insolvency = controlListSeq(INSOLVENCY),
            deRegOrDeath = controlListSeq(DEREG_OR_DEATH),
            debtMigration = controlListSeq(DEBT_MIGRATION),
            directDebit = controlListSeq(DIRECT_DEBIT),
            eu = controlListSeq(EU),
            largeBusiness = controlListSeq(LARGE_BUSINESS),
            missing = controlListSeq(MISSING),
            staggerType = stagger,
            nonStandard = controlListSeq(NONE_STANDARD_TAX_PERIOD),
            overseas = controlListSeq(OVERSEAS),
            poa = controlListSeq(POA),
            entityType = businessEntity,
            dific = controlListSeq(DIFIC),
            anythingUnderAppeal = controlListSeq(ANYTHING_UNDER_APPEAL),
            repaymentCustomer = controlListSeq(REPAYMENT_CUSTOMERS),
            moss = controlListSeq(MOSS)
          ))
      }
    }

  }

}

object ControlListInformationIndicies {
  val VAT_THRESHOLD = 0
  val ANNUAL_ACCOUNTING = 1
  val MISSING_RETURNS = 2
  val CENTRAL_ASSESSMENTS = 3
  val CRIMINAL_INVESTIGATION_INHIBITS = 4
  val COMPLIANCE_PENALTIES_OR_SURCHARGES = 5
  val INSOLVENCY = 6
  val DEREG_OR_DEATH = 7
  val DEBT_MIGRATION = 8
  val DIRECT_DEBIT = 9
  val EU = 10
  val LARGE_BUSINESS = 11
  val MISSING = 12
  val STAGGER_0 = 13
  val NONE_STANDARD_TAX_PERIOD = 14
  val OVERSEAS = 15
  val POA = 16
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
  val DIFIC = 28
  val ANYTHING_UNDER_APPEAL = 29
  val REPAYMENT_CUSTOMERS = 30
  val MOSS = 31
}
