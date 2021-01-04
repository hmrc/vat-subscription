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

import connectors.GetVatCustomerInformationConnector
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatKnownFactsRetrievalService @Inject()(vatCustomerDetailsConnector: GetVatCustomerInformationConnector)
                                             (implicit ec: ExecutionContext) {

  import services.VatKnownFactsRetrievalService._

  def retrieveVatKnownFacts(vatNumber: String)(implicit hc: HeaderCarrier): Future[VatKnownFactRetrievalServiceResponse] = {
    Logger.debug(s"[VatKnownFactsRetrievalService][retrieveVatKnownFacts]: retrieving Vat known facts for vat number - $vatNumber")
    vatCustomerDetailsConnector.getInformation(vatNumber) map {
      case Right(vatCustomerInformation) if vatCustomerInformation.deregistration.isDefined =>
        Right(DeregisteredUser)
      case Right(vatCustomerInformation) =>
        (vatCustomerInformation.customerDetails.vatRegistrationDate, vatCustomerInformation.ppob.address.postCode) match {
          case (Some(date), optPostcode) => Right(
            VatKnownFacts(
              vatRegistrationDate = date,
              businessPostCode = optPostcode,
              isOverseas = vatCustomerInformation.customerDetails.overseasIndicator
            )
          )
          case _ => Left(InvalidVatKnownFacts)
        }
      case Left(connectors.InvalidVatNumber) => Left(InvalidVatNumber)
      case Left(connectors.VatNumberNotFound) => Left(VatNumberNotFound)
      case Left(connectors.Migration) => Left(Migration)
      case Left(connectors.Forbidden) => Left(Forbidden)
      case Left(connectors.UnexpectedGetVatCustomerInformationFailure(status, body)) => Left(UnexpectedGetVatCustomerInformationFailure(status, body))
    }
  }

}

object VatKnownFactsRetrievalService {

  type VatKnownFactRetrievalServiceResponse = Either[GetVatKnownFactsFailures, GetVatKnownFactsSuccess]

  sealed trait GetVatKnownFactsSuccess

  case object DeregisteredUser extends GetVatKnownFactsSuccess

  case class VatKnownFacts(vatRegistrationDate: String, businessPostCode: Option[String], isOverseas: Boolean) extends GetVatKnownFactsSuccess

  object VatKnownFacts {
    implicit val format: OFormat[VatKnownFacts] = Json.format[VatKnownFacts]
  }

  sealed trait GetVatKnownFactsFailures

  case object InvalidVatKnownFacts extends GetVatKnownFactsFailures

  sealed trait GetVatCustomerInformationFailure extends GetVatKnownFactsFailures

  case object InvalidVatNumber extends GetVatCustomerInformationFailure

  case object Migration extends GetVatCustomerInformationFailure

  case object Forbidden extends GetVatCustomerInformationFailure

  case object VatNumberNotFound extends GetVatCustomerInformationFailure

  case class UnexpectedGetVatCustomerInformationFailure(status: Int, body: String) extends GetVatCustomerInformationFailure

}
