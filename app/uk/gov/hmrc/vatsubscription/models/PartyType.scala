/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json._
import play.api.Logger

sealed trait PartyType {
  val value: String
}

case object IndividualType extends PartyType {
  override val value: String = "0"
}

case object SoleTraderType extends PartyType {
  override val value: String = "1"
}

case object LLPType extends PartyType {
  override val value: String = "2"
}

case object PartnershipType extends PartyType {
  override val value: String = "3"
}

case object IncorpBodyType extends PartyType {
  override val value: String = "4"
}

case object UnicorpBodyType extends PartyType {
  override val value: String = "5"
}

case object TrustIncomeTaxType extends PartyType {
  override val value: String = "6"
}

case object CompanyType extends PartyType {
  override val value: String = "7"
}

case object CIOType extends PartyType {
  override val value: String = "8"
}

case object NonUkCharityType extends PartyType {
  override val value: String = "9"
}

case object IPSType extends PartyType {
  override val value: String = "10"
}

case object OrganisationType extends PartyType {
  override val value: String = "11"
}

case object LloydsSyndicateType extends PartyType {
  override val value: String = "12"
}

case object UKCompanyType extends PartyType {
  override val value: String = "50"
}

case object NonUKCompanyType extends PartyType {
  override val value: String = "51"
}

case object NonUkCompWithUKEstablishmentType extends PartyType {
  override val value: String = "51"
}

case object LimitedLiaPartnershipType extends PartyType {
  override val value: String = "52"
}

case object CharitableIncorpOrgType extends PartyType {
  override val value: String = "53"
}

case object CommunityBenefitSocietyType extends PartyType {
  override val value: String = "54"
}

case object RegisteredSocietyType extends PartyType {
  override val value: String = "54"
}

case object CoopSocietyType extends PartyType {
  override val value: String = "55"
}

case object NonUkCompNoUKEstabType extends PartyType {
  override val value: String = "55"
}

case object GovernmentOrgType extends PartyType {
  override val value: String = "56"
}

case object CorportationSoleType extends PartyType {
  override val value: String = "57"
}

case object ScottishPartnershipType extends PartyType {
  override val value: String = "58"
}

case object ScottishLimitedPartnershipType extends PartyType {
  override val value: String = "59"
}

case object TrustType extends PartyType {
  override val value: String = "60"
}

case object OrdinaryPartnershipType extends PartyType {
  override val value: String = "61"
}

case object LimitedPartnershipType extends PartyType {
  override val value: String = "62"
}

case object UnincorporatedAssociationType extends PartyType {
  override val value: String = "63"
}

case object AdminDivisionType extends PartyType {
  override val value: String = "65"
}

case object IndividualZ1Type extends PartyType {
  override val value: String = "Z1"
}

case object VATGroupType extends PartyType {
  override val value: String = "Z2"
}

object PartyType {

  val logger = Logger(getClass.getSimpleName)

  val partyTypes: Set[PartyType] = Set(
    SoleTraderType,
    LLPType,
    PartnershipType,
    IncorpBodyType,
    UnicorpBodyType,
    TrustIncomeTaxType,
    CompanyType,
    CIOType,
    NonUkCharityType,
    IPSType,
    OrganisationType,
    LloydsSyndicateType,
    UKCompanyType,
    NonUkCompWithUKEstablishmentType,
    LimitedLiaPartnershipType,
    CharitableIncorpOrgType,
    RegisteredSocietyType,
    NonUkCompNoUKEstabType,
    GovernmentOrgType,
    CorportationSoleType,
    ScottishPartnershipType,
    ScottishLimitedPartnershipType,
    TrustType,
    OrdinaryPartnershipType,
    LimitedPartnershipType,
    UnincorporatedAssociationType,
    AdminDivisionType,
    IndividualZ1Type,
    VATGroupType
  )

  def apply(typeOfParty: Set[PartyType]): String => PartyType = input => {
    typeOfParty.find { partyType =>
      partyType.value.toUpperCase.equals(input.trim.toUpperCase)
    }.getOrElse(throw new IllegalArgumentException(s"Invalid Party Type: $input"))
  }

  def unapply(arg: PartyType): String = arg.value

  def isValidPartyType(input: String, typeOfParty: Set[PartyType]): Boolean = {
    try {
      PartyType.apply(typeOfParty)(input)
      true
    } catch {
      case t: IllegalArgumentException =>
        logger.info(s"""Invalid Party Type - Received "$input"""", t)
        false
    }
  }

  val reads: Reads[PartyType] = __.read[String].map(apply(partyTypes))

  implicit val writes: Writes[PartyType] = Writes { charge => JsString(charge.value) }

}
