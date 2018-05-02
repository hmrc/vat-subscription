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

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{Format, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.models.EmailRequest
import uk.gov.hmrc.vatsubscription.models.EmailRequest.{creationTimestampKey, idKey}
import reactivemongo.play.json.JSONSerializationPack.Writer
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailRequestRepository @Inject()(mongo: ReactiveMongoComponent,
                                       appConfig: AppConfig)(implicit ec: ExecutionContext)
  extends ReactiveRepository[EmailRequest, String](
    "emailRequestRepository",
    mongo.mongoConnector.db,
    EmailRequest.mongoFormat,
    implicitly[Format[String]]
  ) {

  def upsertEmail(vatNumber: String, email: String): Future[UpdateWriteResult] = {
    collection.update(
      selector = Json.obj(idKey -> vatNumber),
      update = EmailRequest(vatNumber, email),
      upsert = true
    )(implicitly[Writer[JsObject]], EmailRequest.mongoFormat, implicitly[ExecutionContext])
  }

  def deleteRecord(vatNumber: String): Future[WriteResult] =
    removeById(vatNumber)

  private lazy val ttlIndex = Index(
    Seq((creationTimestampKey, IndexType(Ascending.value))),
    name = Some("emailRequestExpires"),
    options = BSONDocument("expireAfterSeconds" -> appConfig.emailTimeToLiveSeconds)
  )

  private def setIndex(): Unit = {
    collection.indexesManager.drop(ttlIndex.name.get) onComplete {
      _ => collection.indexesManager.ensure(ttlIndex)
    }
  }

  setIndex()

}