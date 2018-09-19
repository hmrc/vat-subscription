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

package uk.gov.hmrc.vatsubscription.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.vatsubscription.controllers.actions.VatAuthorised
import uk.gov.hmrc.vatsubscription.models.ReturnPeriod
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.ErrorModel
import uk.gov.hmrc.vatsubscription.services.UpdateVatSubscriptionService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateReturnPeriodController @Inject()(VatAuthorised: VatAuthorised,
                                             vatSubscriptionService: UpdateVatSubscriptionService)
                                            (implicit ec: ExecutionContext) extends MicroserviceBaseController {

  def updateVatReturnPeriod(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      parseJsonBody[ReturnPeriod] match {
        case Left(error) =>
          Future.successful(BadRequest(Json.toJson(error)))
        case Right(invalid) if invalid.stdReturnPeriod.equals("XX") => Future.successful(BadRequest(Json.toJson(ErrorModel("RETURN_PERIOD_ERROR","Invalid Return Period supplied"))))
        case Right(period) =>
          vatSubscriptionService.updateReturnPeriod(period) map {
            case Right(success) => Ok(Json.toJson(success))
            case Left(error) => InternalServerError(Json.toJson(error))
          }
      }
  }

}
