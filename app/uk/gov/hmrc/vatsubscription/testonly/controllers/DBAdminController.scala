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

package uk.gov.hmrc.vatsubscription.testonly.controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.JsPath
import play.api.mvc.Action
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest.vatNumberKey
import uk.gov.hmrc.vatsubscription.testonly.services.{DatabaseAdminService, DatabaseCallSuccess, DeleteRecordFailure}

import scala.concurrent.ExecutionContext

@Singleton
class DBAdminController @Inject()(databaseAdminService: DatabaseAdminService
                                 )(implicit ec: ExecutionContext)
  extends BaseController {

  val delete: Action[String] =
    Action.async(parse.json((JsPath \ vatNumberKey).read[String])) {
      implicit req =>
        val vatNumber = req.body
        databaseAdminService.deleteRecord(vatNumber) map {
          case Right(DatabaseCallSuccess) => NoContent
          case Left(DeleteRecordFailure) => InternalServerError
        }
    }

}
