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

package uk.gov.hmrc.vatsubscription.repositories

import javax.inject.Inject

import play.api.libs.json.{Format, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.play.json._
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest._

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionRequestRepository @Inject()(mongo: ReactiveMongoComponent)(implicit ec: ExecutionContext)
  extends ReactiveRepository[SubscriptionRequest, String](
    "subscriptionRequestRepository",
    mongo.mongoConnector.db,
    SubscriptionRequest.mongoFormat,
    implicitly[Format[String]]
  ) {

  private def upsert(internalId: String, elementKey: String, elementValue: String) = {
    collection.update(
      selector = Json.obj(internalIdKey -> internalId),
      update = Json.obj("$set" -> Json.obj(
        elementKey -> elementValue
      )),
      upsert = true
    )
  }

  def upsertVatNumber(internalId: String, vatNumber: String): Future[UpdateWriteResult] =
    upsert(internalId, vatNumberKey, vatNumber)

  def upsertCompanyNumber(internalId: String, companyNumber: String): Future[UpdateWriteResult] =
    upsert(internalId, companyNumberKey, companyNumber)

}
