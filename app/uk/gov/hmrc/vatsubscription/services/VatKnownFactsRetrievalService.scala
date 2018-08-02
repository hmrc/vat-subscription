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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatsubscription.connectors.GetVatCustomerInformationConnector
import uk.gov.hmrc.vatsubscription.httpparsers
import uk.gov.hmrc.vatsubscription.models._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class VatKnownFactsRetrievalService @Inject()(vatCustomerDetailsConnector: GetVatCustomerInformationConnector)
                                             (implicit ec: ExecutionContext) {

  import uk.gov.hmrc.vatsubscription.services.VatKnownFactsRetrievalService._

  def retrieveVatKnownFacts(vatNumber: String)(implicit hc: HeaderCarrier): Future[Either[GetVatKnownFactsFailures, VatKnownFacts]] = {
    vatCustomerDetailsConnector.getInformation(vatNumber) map {
      case Right(vatCustomerInformation) =>
        (vatCustomerInformation.customerDetails.vatRegistrationDate, vatCustomerInformation.ppob.flatMap(_.address.postCode)) match {
          case (Some(date), Some(postcode)) => Right(
            VatKnownFacts(
              vatRegistrationDate = date,
              businessPostCode = postcode
            )
          )
          case _ => Left(InvalidVatKnownFacts)
        }
      case Left(httpparsers.InvalidVatNumber) => Left(InvalidVatNumber)
      case Left(httpparsers.VatNumberNotFound) => Left(VatNumberNotFound)
      case Left(httpparsers.UnexpectedGetVatCustomerInformationFailure(status, body)) => Left(UnexpectedGetVatCustomerInformationFailure(status, body))
    }
  }

}

object VatKnownFactsRetrievalService {

  sealed trait GetVatKnownFactsFailures

  case object InvalidVatKnownFacts extends GetVatKnownFactsFailures

  sealed trait GetVatCustomerInformationFailure extends GetVatKnownFactsFailures

  case object InvalidVatNumber extends GetVatCustomerInformationFailure

  case object VatNumberNotFound extends GetVatCustomerInformationFailure

  case class UnexpectedGetVatCustomerInformationFailure(status: Int, body: String) extends GetVatCustomerInformationFailure

}