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

import java.time.format.{DateTimeFormatter, ResolverStyle}

import uk.gov.hmrc.vatsubscription.models.UserDetailsModel
import uk.gov.hmrc.vatsubscription.services.monitoring.AuditModel

object UserMatchingAuditing {
  val userMatchingTransactionName = "VATUserMatchingRequest"
  val userMatchingAuditType = "mtdVatUserMatching"

  val dateFormatter =  DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)

  case class UserMatchingAuditModel(userDetailsModel: UserDetailsModel,
                                    isSuccess: Boolean) extends AuditModel {


    override val transactionName: String = userMatchingTransactionName
    override val detail: Map[String, String] = Map(
      "firstName" -> userDetailsModel.firstName,
      "lastName" -> userDetailsModel.lastName,
      "dateOfBirth" -> userDetailsModel.dateOfBirth.format(dateFormatter),
      "nino" -> userDetailsModel.nino,
      "isSuccess" -> s"$isSuccess"
    )
    override val auditType: String = userMatchingAuditType
  }
}