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

import java.util.UUID.randomUUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StandingRequestScheduleConnector @Inject()(val http: HttpClient,
                                                   val appConfig: AppConfig,
                                                   val httpParser: StandingRequestScheduleHttpParser) extends LoggingUtil {

  import httpParser.StandingRequestScheduleHttpParserResponse

  private def url(vatNumber: String) = s"${appConfig.hipUrl}/vat/standing-requests/vrn/$vatNumber"

  def getSrsInformation(vatNumber: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext, user: Request[_]): Future[StandingRequestScheduleHttpParserResponse] = {
    val headerCarrier = hc.copy(authorization = None)

    logger.debug(s"[StandingRequestScheduleConnector][getSrsInformation] URL: ${url(vatNumber)}")
    logger.debug(s"[StandingRequestScheduleConnector][getSrsInformation] Headers: $buildHeadersV1")

    http.GET[StandingRequestScheduleHttpParserResponse](
      url = url(vatNumber),
      headers = buildHeadersV1
    )(
      httpParser.GetStandingRequestScheduleHttpReads,
      headerCarrier,
      implicitly[ExecutionContext]
    ).recover {
      case ex: HttpException =>
        warnLog(s"[StandingRequestScheduleConnector][getInformation] - HTTP exception received: ${ex.message}")
        Left(UnexpectedStandingRequestScheduleFailure(BAD_GATEWAY, ex.message))
    }
  }


  private val CORRELATION_HEADER: String   = "CorrelationId"
  private val AUTHORIZATION_HEADER: String = "Authorization"
  private val requestIdPattern      = """.*([A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}).*""".r

  private def buildHeadersV1(implicit hc: HeaderCarrier): Seq[(String, String)] = Seq(
    appConfig.hipServiceOriginatorIdKeyV1 -> appConfig.hipServiceOriginatorIdV1,
    CORRELATION_HEADER                  -> getCorrelationId(hc),
    AUTHORIZATION_HEADER                -> s"Basic ${appConfig.hipAuthorisationToken}"
  )

  private def generateNewUUID: String = randomUUID.toString

  private def getCorrelationId(hc: HeaderCarrier): String =
    hc.requestId match {
      case Some(requestId) =>
        requestId.value match {
          case requestIdPattern(prefix) =>
            val UUID_LENGTH = 12
            val lastTwelveChars = generateNewUUID.takeRight(UUID_LENGTH)
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
  implicit val writes: Writes[InvalidVatNumber.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case object SrsVatNumberNotFound extends GetStandingRequestScheduleFailure {
  override val status: Int = NOT_FOUND
  override val body = "Not found"
  implicit val writes: Writes[VatNumberNotFound.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case object SrsForbidden extends GetStandingRequestScheduleFailure {
  override val status: Int = FORBIDDEN
  override val body: String = "Forbidden"
  implicit val writes: Writes[Forbidden.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case object SrsMigration extends GetStandingRequestScheduleFailure {
  override val status: Int = PRECONDITION_FAILED
  override val body: String = "Migration"
  implicit val writes: Writes[Migration.type] = Writes {
    _ => Json.obj("status" -> status.toString, "body" -> body)
  }
}

case class UnexpectedStandingRequestScheduleFailure(override val status: Int, override val body: String)
  extends GetStandingRequestScheduleFailure
