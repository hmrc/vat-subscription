/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import config.AppConfig
import httpparsers.UpdateVatSubscriptionHttpParser._
import models.User
import models.updateVatSubscription.request.UpdateVatSubscription
import models.updateVatSubscription.request.UpdateVatSubscription._
import models.updateVatSubscription.response.ErrorModel
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException}
import utils.LoggerUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateVatSubscriptionConnector @Inject()(val http: HttpClient,
                                               val appConfig: AppConfig) extends LoggerUtil {

  private[connectors] val url: String => String = vrn => s"${appConfig.desUrl}/vat/subscription/vrn/$vrn"

  implicit val writes: Writes[UpdateVatSubscription] = DESApi1365Writes(appConfig)

  def desHeaders(user: User[_]): Seq[(String, String)] = Seq(
    "Authorization" -> appConfig.desAuthorisationToken,
    appConfig.desEnvironmentHeader,
    "Credential-Id" -> user.credId
  )

  def updateVatSubscription(user: User[_], vatSubscriptionModel: UpdateVatSubscription, hc: HeaderCarrier)
                           (implicit ec: ExecutionContext): Future[UpdateVatSubscriptionResponse] = {

    val headerCarrier: HeaderCarrier = hc.copy(authorization = None)

    logger.debug(s"[UpdateVatSubscriptionConnector][updateVatSubscription] URL: ${url(user.vrn)}")
    logger.debug(s"[UpdateVatSubscriptionConnector][updateVatSubscription] Headers: ${desHeaders(user)}")
    logger.debug(s"[UpdateVatSubscriptionConnector][updateVatSubscription] Body: \n\n"
      + s"${Json.toJson(vatSubscriptionModel).toString}")

    http.PUT[UpdateVatSubscription, UpdateVatSubscriptionResponse](
      url(user.vrn), vatSubscriptionModel, desHeaders(user)
    )(writes, UpdateVatSubscriptionReads, headerCarrier, ec).recover {
      case ex: HttpException =>
        logger.warn(s"[UpdateVatSubscriptionConnector][updateVatSubscription] - HTTP exception received: ${ex.message}")
        Left(ErrorModel("BAD_GATEWAY", ex.message))
    }
  }
}
