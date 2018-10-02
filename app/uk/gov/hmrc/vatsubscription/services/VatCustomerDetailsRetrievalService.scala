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

package uk.gov.hmrc.vatsubscription.services

import javax.inject.{Inject, Singleton}

import cats.data.EitherT
import cats.instances.future._
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.GetVatCustomerInformationConnector
import uk.gov.hmrc.vatsubscription.connectors.GetVatCustomerInformationFailure
import uk.gov.hmrc.vatsubscription.models._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class VatCustomerDetailsRetrievalService @Inject()(vatCustomerDetailsConnector: GetVatCustomerInformationConnector)
                                                  (implicit ec: ExecutionContext) {

  def retrieveVatCustomerDetails(vatNumber: String)(implicit hc: HeaderCarrier): Future[Either[GetVatCustomerInformationFailure, CustomerDetails]] = {
    Logger.debug(s"[VatCustomerDetailsRetrievalService][retrieveVatCustomerDetails]: retrieving customer details for vat number - $vatNumber")
    (EitherT(vatCustomerDetailsConnector.getInformation(vatNumber)) map {
      vatCustomerInformation =>
        CustomerDetails(
          firstName = vatCustomerInformation.customerDetails.firstName,
          lastName = vatCustomerInformation.customerDetails.lastName,
          organisationName = vatCustomerInformation.customerDetails.organisationName,
          tradingName = vatCustomerInformation.customerDetails.tradingName,
          vatRegistrationDate = vatCustomerInformation.customerDetails.vatRegistrationDate,
          hasFlatRateScheme = vatCustomerInformation.flatRateScheme.isDefined,
          isPartialMigration = vatCustomerInformation.customerDetails.isPartialMigration)
    }).value
  }

  def retrieveCircumstanceInformation(vatNumber: String)
                                     (implicit hc: HeaderCarrier): Future[Either[GetVatCustomerInformationFailure, VatCustomerInformation]] = {
    Logger.debug(s"[VatCustomerDetailsRetrievalService][retrieveVatCustomerDetails]: retrieving customer circumstances for vat number - $vatNumber")
    vatCustomerDetailsConnector.getInformation(vatNumber)
  }

}

