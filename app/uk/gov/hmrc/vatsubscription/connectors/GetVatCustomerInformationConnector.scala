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

import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.httpparsers.GetVatCustomerInformationHttpParser._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetVatCustomerInformationConnector @Inject()(val http: HttpClient,
                                                   val applicationConfig: AppConfig) {

  private def url(vatNumber: String) = s"${applicationConfig.desUrl}vat/customer/vrn/$vatNumber/information"

  def getInformation(vatNumber: String)(implicit hc: HeaderCarrier): Future[GetVatCustomerInformationHttpParserResponse] = {
    val headerCarrier = hc
      .withExtraHeaders(applicationConfig.desEnvironmentHeader)
      .copy(authorization = Some(Authorization(applicationConfig.desAuthorisationToken)))

    http.GET[GetVatCustomerInformationHttpParserResponse](
      url = url(vatNumber)
    )(
      implicitly[HttpReads[GetVatCustomerInformationHttpParserResponse]],
      headerCarrier,
      implicitly[ExecutionContext]
    )
  }

}

