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

import play.api.libs.json.{Format, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.play.json.JSONSerializationPack.Writer
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

  private def upsert(vatNumber: String, elementKey: String, elementValue: String): Future[UpdateWriteResult] = {
    collection.update(
      selector = Json.obj(idKey -> vatNumber),
      update = Json.obj("$set" -> Json.obj(
        elementKey -> elementValue
      )),
      upsert = false
    ).filter(_.n == 1)
  }

  def upsertVatNumber(vatNumber: String): Future[UpdateWriteResult] = {
    collection.update(
      selector = Json.obj(idKey -> vatNumber),
      update = SubscriptionRequest(vatNumber),
      upsert = true
    )(implicitly[Writer[JsObject]], mongoFormat, implicitly[ExecutionContext])
  }

  def upsertCompanyNumber(vatNumber: String, companyNumber: String): Future[UpdateWriteResult] =
    collection.update(
      selector = Json.obj(idKey -> vatNumber),
      update = Json.obj("$set" -> Json.obj(
        companyNumberKey -> companyNumber
      ), "$unset" -> Json.obj(
        ninoKey -> ""
      )),
      upsert = false
    ).filter(_.n == 1)

  def upsertEmail(vatNumber: String, email: String): Future[UpdateWriteResult] =
    upsert(vatNumber, emailKey, email)

  def upsertNino(vatNumber: String, nino: String): Future[UpdateWriteResult] =
    collection.update(
      selector = Json.obj(idKey -> vatNumber),
      update = Json.obj("$set" -> Json.obj(
        ninoKey -> nino,
        identityVerifiedKey -> false
      ), "$unset" -> Json.obj(
        companyNumberKey -> ""
      )),
      upsert = false
    ).filter(_.n == 1)

  def upsertIdentityVerified(vatNumber: String): Future[UpdateWriteResult] =
    collection.update(
      selector = Json.obj(idKey -> vatNumber),
      update = Json.obj("$set" -> Json.obj(
        identityVerifiedKey -> true
      )),
      upsert = false
    ).filter(_.n == 1)

  def deleteRecord(vatNumber: String): Future[WriteResult] =
    collection.remove(selector = Json.obj(idKey -> vatNumber))

}
