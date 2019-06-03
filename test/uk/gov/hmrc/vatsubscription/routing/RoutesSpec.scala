/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.routing

import assets.TestUtil
import uk.gov.hmrc.vatsubscription.controllers.routes._
import uk.gov.hmrc.vatsubscription.helpers.BaseTestConstants.testVatNumber

class RoutesSpec extends TestUtil {

  s"The RetrieveVatCustomerDetilsController.retrieveVatInformation reverse route should be '/vat-subscription/$testVatNumber/full-information'" in {
    RetrieveVatCustomerDetailsController.retrieveVatInformation(testVatNumber).url shouldBe s"/vat-subscription/$testVatNumber/full-information"
  }

  s"The RetrieveVatCustomerDetilsController.retrieveVatCustomerDetails reverse route should be '/vat-subscription/$testVatNumber/customer-details'" in {
    RetrieveVatCustomerDetailsController.retrieveVatCustomerDetails(testVatNumber).url shouldBe s"/vat-subscription/$testVatNumber/customer-details"
  }

  s"The MandationStatusController.getMandationStatus reverse route should be '/vat-subscription/$testVatNumber/mandation-status'" in {
    MandationStatusController.getMandationStatus(testVatNumber).url shouldBe s"/vat-subscription/$testVatNumber/mandation-status"
  }

  s"The UpdateMandationStatusController.updateMandationStatus reverse route should be '/vat-subscription/$testVatNumber/mandation-status'" in {
    UpdateMandationStatusController.updateMandationStatus(testVatNumber).url shouldBe s"/vat-subscription/$testVatNumber/mandation-status"
  }

  s"The UpdateReturnPeriodController.updateVatReturnPeriod reverse route should be '/vat-subscription/$testVatNumber/return-period'" in {
    UpdateReturnPeriodController.updateVatReturnPeriod(testVatNumber).url shouldBe s"/vat-subscription/$testVatNumber/return-period"
  }

  s"The UpdatePPOBController.updatePPOB reverse route should be '/vat-subscription/$testVatNumber/ppob'" in {
    UpdatePPOBController.updatePPOB(testVatNumber).url shouldBe s"/vat-subscription/$testVatNumber/ppob"
  }

  s"The RequestDeregistrationController.deregister route should be a PUT to '/vat-subscription/$testVatNumber/deregister'" in {
    val route = RequestDeregistrationController.deregister(testVatNumber)
    route.url shouldBe s"/vat-subscription/$testVatNumber/deregister"
    route.method shouldBe "PUT"
  }

}
