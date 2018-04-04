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

object SignUpAuditing {
  val signUpTransactionName = "VATSignUpRequest"
  val signUpAuditType = "mtdVatSignUp"

  case class SignUpAuditModel(safeId: String,
                              vatNumber: String,
                              emailAddress: String,
                              emailAddressVerified: Boolean,
                              isSuccess: Boolean) extends AuditModel {

    override val transactionName: String = signUpTransactionName
    override val detail: Map[String, String] = Map(
      "safeId" -> safeId,
      "vatNumber" -> vatNumber,
      "emailAddress" -> emailAddress,
      "emailAddressVerified" -> emailAddressVerified.toString,
      "matchSuccess" -> s"$isSuccess"
    )
    override val auditType: String = signUpAuditType
  }
}