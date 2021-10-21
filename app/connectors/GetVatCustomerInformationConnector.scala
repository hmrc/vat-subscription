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
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, NOT_FOUND, PRECONDITION_FAILED}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetVatCustomerInformationConnector @Inject()(val http: HttpClient,
                                                   val appConfig: AppConfig,
                                                   val httpParser: GetVatCustomerInformationHttpParser) extends LoggerUtil {

  import httpParser.GetVatCustomerInformationHttpParserResponse

  private def url(vatNumber: String) = s"${appConfig.desUrl}/vat/customer/vrn/$vatNumber/information"

  val desHeaders = Seq(
    "Authorization" -> appConfig.desAuthorisationToken,
    appConfig.desEnvironmentHeader
  )

  def getInformation(vatNumber: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetVatCustomerInformationHttpParserResponse] = {
    val headerCarrier = hc.copy(authorization = None)

    logger.debug(s"[GetVatCustomerInformationConnector][getInformation] URL: ${url(vatNumber)}")
    logger.debug(s"[GetVatCustomerInformationConnector][getInformation] Headers: $desHeaders")

    http.GET[GetVatCustomerInformationHttpParserResponse](
      url = url(vatNumber),
      headers = desHeaders
    )(
      httpParser.GetVatCustomerInformationHttpReads,
      headerCarrier,
      implicitly[ExecutionContext]
    )
  }
}

sealed trait GetVatCustomerInformationFailure {
  val status: Int
  val message: String
}

object GetVatCustomerInformationFailure {
  implicit val writes: Writes[GetVatCustomerInformationFailure] = Writes {
    error => Json.obj("status" -> error.status.toString, "message" -> error.message)
  }
}

case object InvalidVatNumber extends GetVatCustomerInformationFailure {
  override val status: Int = BAD_REQUEST
  override val message = "Bad request"
}

case object VatNumberNotFound extends GetVatCustomerInformationFailure {
  override val status: Int = NOT_FOUND
  override val message = "Not found"
}

case object Forbidden extends GetVatCustomerInformationFailure {
  override val status: Int = FORBIDDEN
  override val message: String = "Forbidden"
}

case object Migration extends GetVatCustomerInformationFailure {
  override val status: Int = PRECONDITION_FAILED
  override val message: String = "Migration"
}

case class UnexpectedGetVatCustomerInformationFailure(override val status: Int, override val message: String)
  extends GetVatCustomerInformationFailure
