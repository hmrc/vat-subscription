/*
 * Copyright 2023 HM Revenue & Customs
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

import helpers.BaseTestConstants.testUser
import helpers.{MockHttpClient, TestUtil}
import httpparsers.GetVatCustomerInformationHttpParser
import play.api.http.Status.BAD_GATEWAY
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException

import scala.concurrent.Future

class GetVatCustomerInformationConnectorSpec extends TestUtil with MockHttpClient {

  val httpParser = new GetVatCustomerInformationHttpParser(mockAppConfig)
  val connector = new GetVatCustomerInformationConnector(mockHttpClient, mockAppConfig, httpParser)
  import httpParser.GetVatCustomerInformationHttpParserResponse

  "The GetVatCustomerInformationConnector" should {

    "return a UnexpectedGetVatCustomerInformationFailure error model when there is a HTTP exception" in {
      val exception = new RequestTimeoutException("Request timed out!!!")
      mockHttpGet[GetVatCustomerInformationHttpParserResponse](Future.failed(exception))
      val result: Future[GetVatCustomerInformationHttpParserResponse] = connector.getInformation(testUser.vrn)

      await(result) shouldBe Left(UnexpectedGetVatCustomerInformationFailure(BAD_GATEWAY, exception.message))
    }
  }
}
