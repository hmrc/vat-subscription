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
import play.api.Logger
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.httpparsers.UpdateVatSubscriptionHttpParser._
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.request.UpdateVatSubscription

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateVatSubscriptionConnector @Inject()(val http: HttpClient,
                                               val appConfig: AppConfig) {

  private[connectors] val url: String => String = vrn => s"${appConfig.desUrl}/vat/subscription/vrn/$vrn"

  def updateVatSubscription(vrn: String, vatSubscriptionModel: UpdateVatSubscription, hc: HeaderCarrier)
                           (implicit ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {

    implicit val headerCarrier: HeaderCarrier = hc
      .withExtraHeaders(appConfig.desEnvironmentHeader)
      .copy(authorization = Some(Authorization(appConfig.desAuthorisationToken)))

    Logger.debug(s"[UpdateVatSubscriptionConnector][updateVatSubscription] URL: ${url(vrn)}")
    Logger.debug(s"[UpdateVatSubscriptionConnector][updateVatSubscription] Headers: $headerCarrier")

    http.PUT[UpdateVatSubscription, UpdateVatSubscriptionResponse](url(vrn), vatSubscriptionModel)
  }
}
