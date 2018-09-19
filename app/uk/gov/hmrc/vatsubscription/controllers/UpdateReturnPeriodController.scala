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
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, AnyContentAsJson}
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.controllers.actions.VatAuthorised
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.ErrorModel
import uk.gov.hmrc.vatsubscription.models.{InvalidReturnPeriod, ReturnPeriod, User}
import uk.gov.hmrc.vatsubscription.services.UpdateVatSubscriptionService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateReturnPeriodController @Inject()(VatAuthorised: VatAuthorised,
                                             vatSubscriptionService: UpdateVatSubscriptionService)
                                            (implicit ec: ExecutionContext) extends BaseController {

  private def returnPeriod(implicit user: User[_]): ReturnPeriod = user.body match {
    case body: AnyContentAsJson => body.json.as[ReturnPeriod](ReturnPeriod.currentReads)
    case _ =>
      Logger.warn("[UpdateVatCustomerDetailsController][AnyContentAsJson] Body of request was not JSON")
      InvalidReturnPeriod
  }

  def updateVatReturnPeriod(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      returnPeriod match {
        case InvalidReturnPeriod =>
          Future.successful(BadRequest(Json.toJson(ErrorModel("RETURN_PERIOD_ERROR", s"Invalid Json or Invalid Return Period supplied"))))
        case period =>
          vatSubscriptionService.updateReturnPeriod(period) map {
            case Right(success) => Ok(Json.toJson(success))
            case Left(error) =>
              Logger.debug(s"[UpdateVatCustomerDetailsController][updateVatReturnPeriod]: error returned from vatSubcriptionService - $error")
              InternalServerError(Json.toJson(error))
          }
      }
  }

}
