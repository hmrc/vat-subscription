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

package services

import javax.inject.{Inject, Singleton}
import cats.data._
import cats.implicits._
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import connectors.{GetVatCustomerInformationConnector, GetVatCustomerInformationFailure}
import models.MandationStatus

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MandationStatusService @Inject()(getVatCustomerInformationConnector: GetVatCustomerInformationConnector)(implicit ec: ExecutionContext) {

  def getMandationStatus(vatNumber: String)(implicit hc: HeaderCarrier): Future[Either[GetVatCustomerInformationFailure, MandationStatus]] = {
    Logger.debug(s"[MandationStatusService][getMandationStatus]: retrieving mandation status from connector for vat number - $vatNumber")
    EitherT(getVatCustomerInformationConnector.getInformation(vatNumber)).map(_.mandationStatus).value
  }
}
