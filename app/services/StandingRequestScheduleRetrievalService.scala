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

package services

import connectors.{GetStandingRequestScheduleFailure, StandingRequestScheduleConnector}
import models._
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class StandingRequestScheduleRetrievalService @Inject()(standingRequestScheduleConnector: StandingRequestScheduleConnector)
                                                       (implicit ec: ExecutionContext) extends LoggingUtil {

  def retrieveStandingRequestSchedule(vatNumber: String)
                                (implicit hc: HeaderCarrier,
                                 user: User[_]): Future[Either[GetStandingRequestScheduleFailure, StandingRequestSchedule]] = {
    infoLog(s"[StandingRequestScheduleRetrievalService][StandingRequestSchedule]: retrieving StandingRequestSchedule " +
      s"details for vat number - $vatNumber")
    standingRequestScheduleConnector.getSrsInformation(vatNumber).map {
      case Right(details) =>
        infoLog(s"[StandingRequestScheduleRetrievalService] Response received:")
        Right(returnRequestCategory3Only(details))
      case Left(error) =>
        warnLog(s"[StandingRequestScheduleRetrievalService] Error received: $error")
        Left(error)
    }
  }

  private def returnRequestCategory3Only(details: StandingRequestSchedule): StandingRequestSchedule = {
    details.copy(standingRequests = details.standingRequests.filter(_.requestCategory == "3"))
  }

}

