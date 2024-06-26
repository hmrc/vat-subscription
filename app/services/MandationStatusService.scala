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

import connectors.{GetVatCustomerInformationConnector, GetVatCustomerInformationFailure}
import models.MandationStatus
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MandationStatusService @Inject()(getVatCustomerInformationConnector: GetVatCustomerInformationConnector)
                                      (implicit ec: ExecutionContext) extends LoggingUtil {

  def getMandationStatus(vatNumber: String)(implicit hc: HeaderCarrier, user: Request[_]): Future[Either[GetVatCustomerInformationFailure, MandationStatus]] = {
    logger.debug(s"[MandationStatusService][getMandationStatus]: retrieving mandation status from connector for vat number - $vatNumber")
      getVatCustomerInformationConnector.getInformation(vatNumber) map {
        case Right(details) =>
          Right(details.mandationStatus)
        case Left(error) =>
          Left(error)
      }
  }
}
