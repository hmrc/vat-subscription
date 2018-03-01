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

package uk.gov.hmrc.vatsubscription.config

object Constants {
  val AgentEnrolmentKey: String = "HMRC-AS-AGENT"
  val AgentReferenceNumberKey: String = "AgentReferenceNumber"
  val HttpCodeKey: String = "CODE"

  object Des {
    val IdTypeKey = "idType"
    val IdValueKey = "idValue"
    val idNumberKey = "idNumber"
    val CrnKey = "CRN"
    val NinoKey = "NINO"
    val VrnKey = "VRN"
    val SafeIdKey = "SAFEID"
    val emailKey = "EMAIL"

    val RegistrationRequestKey = "registrationRequest"
    val IdentificationKey = "identification"
  }

  object GGEnrolment {
    val ServiceName = "HMRC-MTD-VAT"
  }
}
