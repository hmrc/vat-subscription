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

import cats.data.EitherT
import cats.instances.future._
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import connectors.GetVatCustomerInformationConnector
import connectors.GetVatCustomerInformationFailure
import models._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class VatCustomerDetailsRetrievalService @Inject()(vatCustomerDetailsConnector: GetVatCustomerInformationConnector)
                                                  (implicit ec: ExecutionContext) {

  def retrieveVatCustomerDetails(vatNumber: String)(implicit hc: HeaderCarrier): Future[Either[GetVatCustomerInformationFailure, CustomerDetails]] = {
    Logger.debug(s"[VatCustomerDetailsRetrievalService][retrieveVatCustomerDetails]: retrieving customer details for vat number - $vatNumber")
    (EitherT(vatCustomerDetailsConnector.getInformation(vatNumber)) map {
      vatCustomerInformation =>
        CustomerDetails(
          title = vatCustomerInformation.customerDetails.title,
          firstName = vatCustomerInformation.customerDetails.firstName,
          middleName = vatCustomerInformation.customerDetails.middleName,
          lastName = vatCustomerInformation.customerDetails.lastName,
          organisationName = vatCustomerInformation.customerDetails.organisationName,
          tradingName = vatCustomerInformation.customerDetails.tradingName,
          effectiveRegistrationDate = vatCustomerInformation.customerDetails.effectiveRegistrationDate,
          hasFlatRateScheme = vatCustomerInformation.flatRateScheme.isDefined,
          welshIndicator = vatCustomerInformation.customerDetails.welshIndicator,
          isPartialMigration = vatCustomerInformation.customerDetails.isPartialMigration,
          customerMigratedToETMPDate = vatCustomerInformation.customerDetails.customerMigratedToETMPDate,
          isInsolvent = vatCustomerInformation.customerDetails.isInsolvent,
          insolvencyType = vatCustomerInformation.customerDetails.insolvencyType,
          insolvencyDate = vatCustomerInformation.customerDetails.insolvencyDate,
          continueToTrade = vatCustomerInformation.customerDetails.continueToTrade,
          hybridToFullMigrationDate = vatCustomerInformation.customerDetails.hybridToFullMigrationDate,
          overseasIndicator = vatCustomerInformation.customerDetails.overseasIndicator,
          nameIsReadOnly = vatCustomerInformation.customerDetails.nameIsReadOnly
        )
    }).value
  }

  def extractWelshIndicator(vrn: String)(implicit hc: HeaderCarrier): Future[Either[GetVatCustomerInformationFailure,Boolean]] = {
    retrieveVatCustomerDetails(vrn).map {
      case Right(details) =>
        Right(details.welshIndicator.contains(true))
      case Left(error) =>
        Logger.debug(s"[VatCustomerDetailsRetrievalService][extractWelshIndicator]: " +
          s"vatCustomerDetailsConnector returned an error retrieving welshIndicator - $error")
        Left(error)
    }
  }

  def retrieveCircumstanceInformation(vatNumber: String)
                                     (implicit hc: HeaderCarrier): Future[Either[GetVatCustomerInformationFailure, VatCustomerInformation]] = {
    Logger.debug(s"[VatCustomerDetailsRetrievalService][retrieveVatCustomerDetails]: retrieving customer circumstances for vat number - $vatNumber")
    vatCustomerDetailsConnector.getInformation(vatNumber)
  }

}

