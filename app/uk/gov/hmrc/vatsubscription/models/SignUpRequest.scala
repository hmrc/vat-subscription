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

import play.api.libs.json.Json


case class Identification(idType: String, idNumber: String)

object Identification {
  implicit val format = Json.format[Identification]
}

case class AdditionalInformation(typeOfField: String, fieldContents: String, infoVerified: Option[Boolean])

object AdditionalInformation {
  implicit val format = Json.format[AdditionalInformation]
}

case class SignUpRequestContent(identification: List[Identification], additionalInformation: Option[List[AdditionalInformation]])

object SignUpRequestContent {
  implicit val format = Json.format[SignUpRequestContent]
}

case class SignUpRequest(signUpRequest: SignUpRequestContent)

object SignUpRequest {
  implicit val format = Json.format[SignUpRequest]

  def apply(safeId: String, vatNumber: String, email: String, emailVerified: Boolean): SignUpRequest = {
    import uk.gov.hmrc.vatsubscription.config.Constants.Des._
    SignUpRequest(
      SignUpRequestContent(
        List(
          Identification(SafeIdKey, safeId),
          Identification(VrnKey, vatNumber)
        ),
        Some(List(AdditionalInformation(emailKey, email, Some(emailVerified))))
      )
    )
  }

}

