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

package uk.gov.hmrc.vatsubscription.connectors

import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.httpparsers.GetMandationStatusHttpParser._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MandationStatusConnector @Inject()(http: HttpClient,
                                         appConfig: AppConfig
                                        )(implicit ec: ExecutionContext) {
  def getMandationStatus(vatNumber: String)(implicit hc: HeaderCarrier): Future[GetMandationStatusResponse] =
    http.GET(appConfig.mandationStatusUrl(vatNumber))
}
