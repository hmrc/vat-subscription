/*
 * Copyright 2024 HM Revenue & Customs
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
import httpparsers.StandingRequestScheduleHttpParser
import play.api.http.Status._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Request
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException}
import utils.LoggingUtil

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StandingRequestScheduleConnector @Inject()(val http: HttpClient,
                                                   val appConfig: AppConfig,
                                                   val httpParser: StandingRequestScheduleHttpParser) extends LoggingUtil {

  import httpParser.StandingRequestScheduleHttpParserResponse

  def getSrsInformation(vatNumber: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext, user: Request[_]): Future[StandingRequestScheduleHttpParserResponse] = {
    val headerCarrier = hc.copy(authorization = None)
    val url = s"${appConfig.hipUrl}/etmp/RESTAdapter/VATC/standing-requests/VRN/$vatNumber"
    val correlationId = randomUUID.toString
    val hipHeaders = buildHeadersV1(correlationId)

    logger.info(s"[StandingRequestScheduleConnector][getSrsInformation] URL: $url, Correlation ID: $correlationId")

    http.GET[StandingRequestScheduleHttpParserResponse](
      url = url,
      headers = hipHeaders
    )(
      httpParser.GetStandingRequestScheduleHttpReads,
      headerCarrier,
      implicitly[ExecutionContext]
    ).recover {
      case ex: HttpException =>
        warnLog(s"[StandingRequestScheduleConnector][getSrsInformation] - HTTP exception received: ${ex.message}")
        Left(UnexpectedStandingRequestScheduleFailure(BAD_GATEWAY, ex.message))
    }
  }

  private val CorrelationIdHeader: String   = "CorrelationId"
  private val AuthorizationHeader: String = "Authorization"
  private val xOriginatingSystemHeader: String  = "X-Originating-System"
  private val xReceiptDateHeader: String        = "X-Receipt-Date"
  private val xTransmittingSystemHeader: String = "X-Transmitting-System"

  private def buildHeadersV1(correlationId: String): Seq[(String, String)] =
    Seq(
      CorrelationIdHeader                  -> correlationId,
      AuthorizationHeader                -> s"Basic ${appConfig.hipAuthorisationToken}",
      xOriginatingSystemHeader -> "MDTP",
      xReceiptDateHeader ->  DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.SECONDS)),
      xTransmittingSystemHeader -> "HIP" 
    )

}

sealed trait GetStandingRequestScheduleFailure {
  val status: Int
  val body: String
}

object GetStandingRequestScheduleFailure {
  implicit val writes: Writes[GetStandingRequestScheduleFailure] = Writes {
    error => Json.obj("status" -> error.status.toString, "body" -> error.body)
  }
}

case object SrsInvalidVatNumber extends GetStandingRequestScheduleFailure {
  override val status: Int = BAD_REQUEST
  override val body = "Bad request"
  implicit val writes: Writes[SrsInvalidVatNumber.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case object SrsVatNumberNotFound extends GetStandingRequestScheduleFailure {
  override val status: Int = NOT_FOUND
  override val body = "Not found"
  implicit val writes: Writes[SrsVatNumberNotFound.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case object SrsForbidden extends GetStandingRequestScheduleFailure {
  override val status: Int = FORBIDDEN
  override val body: String = "Forbidden"
  implicit val writes: Writes[SrsForbidden.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case object SrsInactiveUser extends GetStandingRequestScheduleFailure {
  override val status: Int = UNPROCESSABLE_ENTITY
  override val body: String = "Customer is not on VAT scheme"
  implicit val writes: Writes[SrsInactiveUser.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case class UnexpectedStandingRequestScheduleFailure(override val status: Int, override val body: String)
  extends GetStandingRequestScheduleFailure
