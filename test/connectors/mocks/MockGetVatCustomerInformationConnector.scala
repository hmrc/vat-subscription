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

package connectors.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Suite}
import uk.gov.hmrc.http.HeaderCarrier
import connectors.GetVatCustomerInformationConnector
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext, Future}

trait MockGetVatCustomerInformationConnector extends MockitoSugar with BeforeAndAfterEach {
  this: Suite =>
  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  val mockConnector: GetVatCustomerInformationConnector = mock[GetVatCustomerInformationConnector]

  def mockGetVatCustomerInformationConnector(vatNumber: String)
                                            (response: Future[mockConnector.httpParser.GetVatCustomerInformationHttpParserResponse])
  : Unit = {
    when(mockConnector.getInformation(
      ArgumentMatchers.eq(vatNumber)
    )(ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext],
      ArgumentMatchers.any[Request[_]])) thenReturn response
  }
}
