package uk.gov.hmrc.vatsubscription.models

import uk.gov.hmrc.vatsubscription.models.ControlListInformation.{BusinessEntity, Stagger}

case class ControlListInformation(belowVatThreshold: Boolean,
                                  annualAccounting: Boolean,
                                  missingReturns: Boolean,
                                  centralAssessments: Boolean,
                                  criminalInvestigationInhibits: Boolean,
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


  // TODO implement
  def tryParse(controlList: String): Either[ControlListParseError, ControlListInformation] = ???

}
