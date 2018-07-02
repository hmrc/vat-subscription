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
import uk.gov.hmrc.vatsubscription.models.post.PPOBPost
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.ErrorModel
import uk.gov.hmrc.vatsubscription.models.{InvalidReturnPeriod, ReturnPeriod, User}
import uk.gov.hmrc.vatsubscription.services._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateVatCustomerDetailsController @Inject()(VatAuthorised: VatAuthorised,
                                                   vatSubscriptionService: UpdateVatSubscriptionService)
                                                  (implicit ec: ExecutionContext) extends BaseController {

  private def returnPeriod(implicit user: User[_]): ReturnPeriod = user.body match {
    case body: AnyContentAsJson => body.json.as[ReturnPeriod]
    case _ =>
      Logger.warn("[UpdateVatCustomerDetailsController][AnyContentAsJson] Body of request was not JSON")
      InvalidReturnPeriod
  }

  private def ppob(implicit user: User[_]): Either[ErrorModel, PPOBPost] = user.body match {
    case body: AnyContentAsJson => Right(body.json.as[PPOBPost])
    case _ =>
      Logger.warn("[UpdateVatCustomerDetailsController][AnyContentAsJson] Body of request was not JSON")
      Left(ErrorModel("PPOB_ERROR", "Unknown body retrieved"))
  }

  def updateVatReturnPeriod(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      returnPeriod match {
        case InvalidReturnPeriod =>
          Future.successful(BadRequest(Json.toJson(ErrorModel("RETURN_PERIOD_ERROR", s"Invalid Json or Invalid Return Period supplied"))))
        case period =>
          vatSubscriptionService.updateReturnPeriod(period) map {
            case Right(success) => Ok(Json.toJson(success))
            case Left(error) => InternalServerError(Json.toJson(error))
          }
      }
  }

  def updatePPOB(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      ppob match {
        case Left(error) =>
          Future.successful(BadRequest(Json.toJson(error)))
        case Right(updatedPpob) =>
          vatSubscriptionService.updatePPOB(updatedPpob) map {
            case Right(success) => Ok(Json.toJson(success))
            case Left(error) => InternalServerError(Json.toJson(error))
          }
      }
  }
}
