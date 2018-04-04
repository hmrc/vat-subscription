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

package uk.gov.hmrc.vatsubscription.models.monitoring

import uk.gov.hmrc.vatsubscription.services.monitoring.AuditModel

object RegisterWithMultipleIDsAuditing {
  val registerWithMultipleIDsTransactionName : String => String =
    businessEntity => s"VATRegister${businessEntity}WithMultipleIDs"
  val registerWithMultipleIDsAuditType : String => String =
    businessEntity => s"mtdVatRegister${businessEntity}WithMultipleIDs"

  case class RegisterWithMultipleIDsCompanyAuditModel(vatNumber: String,
                                                      companyNumber: String,
                                                      agentReferenceNumber: Option[String],
                                                      isSuccess: Boolean) extends AuditModel {
    override val transactionName: String = registerWithMultipleIDsTransactionName("Company")
    override val detail: Map[String, String] = Map(
      "vatNumber" -> Some(vatNumber),
      "companyNumber" -> Some(companyNumber),
      "agentReferenceNumber" -> agentReferenceNumber,
      "matchSuccess" -> Some(s"$isSuccess")
    ).collect { case (key, Some(value)) => key -> value }

    override val auditType: String = registerWithMultipleIDsAuditType("Company")
  }

  case class RegisterWithMultipleIDsIndividualAuditModel(vatNumber: String,
                                                         nino: String,
                                                         agentReferenceNumber: Option[String],
                                                         isSuccess: Boolean) extends AuditModel {
    override val transactionName: String = registerWithMultipleIDsTransactionName("Individual")
    override val detail: Map[String, String] = Map(
      "vatNumber" -> Some(vatNumber),
      "nino" -> Some(nino),
      "agentReferenceNumber" -> agentReferenceNumber,
      "matchSuccess" -> Some(s"$isSuccess")
    ).collect { case (key, Some(value)) => key -> value }

    override val auditType: String = registerWithMultipleIDsAuditType("Individual")
  }
}