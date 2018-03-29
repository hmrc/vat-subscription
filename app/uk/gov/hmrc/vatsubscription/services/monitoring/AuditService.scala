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

package uk.gov.hmrc.vatsubscription.services.monitoring

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.DataEvent

import scala.annotation.implicitNotFound
import scala.concurrent.ExecutionContext

@Singleton
class AuditService @Inject()(configuration: Configuration,
                                auditConnector: AuditConnector) {

  private lazy val appName: String = configuration.getString("appName")
    .getOrElse(throw new Exception(s"Missing configuration key: appName"))

  def audit[A](dataSource: AuditModel, path: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit =
    auditConnector.sendEvent(toDataEvent(appName, dataSource, path))

  def toDataEvent(appName: String, auditModel: AuditModel, path: String)(implicit hc: HeaderCarrier): DataEvent = {
    val auditType: String = auditModel.auditType
    val transactionName: String = auditModel.transactionName
    val detail: Map[String, String] = auditModel.detail
    val tags: Map[String, String] = Map.empty[String, String]

    DataEvent(
      auditSource = appName,
      auditType = auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(transactionName, path) ++ tags,
      detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(detail.toSeq: _*)
    )
  }
}

trait AuditModel {
  val auditType: String
  val transactionName: String
  val detail: Map[String, String]
}