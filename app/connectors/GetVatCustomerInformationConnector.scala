/*
 * Copyright 2021 HM Revenue & Customs
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
import httpparsers.GetVatCustomerInformationHttpParser
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, NOT_FOUND, PRECONDITION_FAILED}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.logging.Authorization

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetVatCustomerInformationConnector @Inject()(val http: HttpClient,
                                                   val applicationConfig: AppConfig,
                                                   val httpParser: GetVatCustomerInformationHttpParser) {

  import httpParser.GetVatCustomerInformationHttpParserResponse

  private def url(vatNumber: String) = s"${applicationConfig.desUrl}/vat/customer/vrn/$vatNumber/information"

  def getInformation(vatNumber: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetVatCustomerInformationHttpParserResponse] = {
    val headerCarrier = hc
      .withExtraHeaders(applicationConfig.desEnvironmentHeader)
      .copy(authorization = Some(Authorization(applicationConfig.desAuthorisationToken)))

    Logger.debug(s"[GetVatCustomerInformationConnector][getInformation] URL: ${url(vatNumber)}")
    Logger.debug(s"[GetVatCustomerInformationConnector][getInformation] Headers: ${headerCarrier.headers}")

    http.GET[GetVatCustomerInformationHttpParserResponse](
      url = url(vatNumber)
    )(
      httpParser.GetVatCustomerInformationHttpReads,
      headerCarrier,
      implicitly[ExecutionContext]
    )
  }
}

sealed trait GetVatCustomerInformationFailure {
  val status: Int
  val body: String
}

object GetVatCustomerInformationFailure {
  implicit val writes: Writes[GetVatCustomerInformationFailure] = Writes {
    error => Json.obj("status" -> error.status.toString, "body" -> error.body)
  }
}

case object InvalidVatNumber extends GetVatCustomerInformationFailure {
  override val status: Int = BAD_REQUEST
  override val body = "Bad request"
}

case object VatNumberNotFound extends GetVatCustomerInformationFailure {
  override val status: Int = NOT_FOUND
  override val body = "Not found"
}

case object Forbidden extends GetVatCustomerInformationFailure {
  override val status: Int = FORBIDDEN
  override val body: String = "Forbidden"
}

case object Migration extends GetVatCustomerInformationFailure {
  override val status: Int = PRECONDITION_FAILED
  override val body: String = "Migration"
}

case class UnexpectedGetVatCustomerInformationFailure(override val status: Int, override val body: String)
  extends GetVatCustomerInformationFailure
