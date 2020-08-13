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

package httpparsers

import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}

object UpdateVatSubscriptionHttpParser {

  type UpdateVatSubscriptionResponse = Either[ErrorModel, SuccessModel]

  implicit object UpdateVatSubscriptionReads extends HttpReads[UpdateVatSubscriptionResponse] {

    override def read(method: String, url: String, response: HttpResponse): UpdateVatSubscriptionResponse = {

      response.status match {
        case OK =>
          Logger.debug("[UpdateVatSubscriptionHttpParser][read]: Status OK")
          response.json.validate[SuccessModel].fold(
            _ => {
              Logger.warn(s"[UpdateVatSubscriptionHttpParser][read]: Invalid Success Response Json")
              Left(ErrorModel("INTERNAL_SERVER_ERROR", "Invalid Json returned in Success response."))
            },
            valid => Right(valid)
          )
        case status =>
          Logger.warn(s"[UpdateVatSubscriptionReads][read]: Unexpected response, status $status returned. Body: ${response.body}")
          response.json.validate[ErrorModel].fold(
            invalid => {
              Logger.warn(s"[UpdateVatSubscriptionHttpParser][read]: Invalid Error Response Json - $invalid")
              Left(ErrorModel("INTERNAL_SERVER_ERROR", s"Invalid Json returned in Error response. Status $status returned"))
            },
            valid => Left(valid)
          )
      }
    }
  }
}
