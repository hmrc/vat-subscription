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

import javax.inject.{Inject, Singleton}
import play.api.http.Status._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Request
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException}
import utils.LoggingUtil
import java.time.{Clock, Instant}
import java.util.Base64
import java.util.UUID.randomUUID
import scala.concurrent.{ExecutionContext, Future}
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Singleton
class StandingRequestScheduleConnector @Inject()(val http: HttpClient,
                                                   val appConfig: AppConfig,
                                                   val httpParser: StandingRequestScheduleHttpParser) extends LoggingUtil {

  import httpParser.StandingRequestScheduleHttpParserResponse

  private def url(vatNumber: String) = s"${appConfig.hipUrl}/etmp/RESTAdapter/VATC/standing-requests/VRN/$vatNumber"

  def getSrsInformation(vatNumber: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext, user: Request[_]): Future[StandingRequestScheduleHttpParserResponse] = {
    val headerCarrier = hc.copy(authorization = None)

    val hipHeaders = buildHeadersV1 

    logger.info(s"[StandingRequestScheduleConnector][getSrsInformation] URL: ${url(vatNumber)}")
    logger.info(s"[StandingRequestScheduleConnector][getSrsInformation] Headers: $hipHeaders")
    logger.info(s"[StandingRequestScheduleConnector][getSrsInformation] URL: ${url(vatNumber)}")
    logger.info(s"[StandingRequestScheduleConnector][getSrsInformation] Headers: $hipHeaders")

    http.GET[StandingRequestScheduleHttpParserResponse](
      url = url(vatNumber),
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

  private val requestIdPattern      = """.*([A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}).*""".r

  private val CorrelationIdHeader: String   = "CorrelationId"
  private val AuthorizationHeader: String = "Authorization"
  private val xOriginatingSystemHeader: String  = "X-Originating-System"
  private val xReceiptDateHeader: String        = "X-Receipt-Date"
  private val xTransmittingSystemHeader: String = "X-Transmitting-System"

  private def buildHeadersV1(implicit hc: HeaderCarrier): Seq[(String, String)] =
    Seq(
      appConfig.hipServiceOriginatorIdKeyV1 -> appConfig.hipServiceOriginatorIdV1,
      CorrelationIdHeader                  -> getCorrelationId(hc),
      AuthorizationHeader                -> s"Basic ${appConfig.hipAuthorisationToken}",
      appConfig.hipEnvironmentHeader,
      xOriginatingSystemHeader -> "MTDP",
      xReceiptDateHeader ->  DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.SECONDS)),
      xTransmittingSystemHeader -> "HIP" 
    )

  def generateNewUUID: String = randomUUID.toString

  def getCorrelationId(hc: HeaderCarrier): String =
    hc.requestId match {
      case Some(requestId) =>
        requestId.value match {
          case requestIdPattern(prefix) =>
            val lastTwelveChars = generateNewUUID.takeRight(12)
            prefix + "-" + lastTwelveChars
          case _                        => generateNewUUID
        }
      case _               => generateNewUUID
    }
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

case object SrsMigration extends GetStandingRequestScheduleFailure {
  override val status: Int = PRECONDITION_FAILED
  override val body: String = "Migration"
  implicit val writes: Writes[SrsMigration.type] = Writes {
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
