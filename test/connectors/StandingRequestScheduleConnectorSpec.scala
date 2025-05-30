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

package connectors

import helpers.BaseTestConstants.testUser
import helpers.{MockHttpClient, TestUtil}
import httpparsers.StandingRequestScheduleHttpParser
import play.api.http.Status.BAD_GATEWAY
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException
import helpers.StandingRequestScheduleConstants._
import scala.concurrent.Future

class StandingRequestScheduleConnectorSpec
    extends TestUtil
    with MockHttpClient {

  val httpParser = new StandingRequestScheduleHttpParser(mockAppConfig)
  val connector = new StandingRequestScheduleConnector(
    mockHttpClient,
    mockAppConfig,
    httpParser
  )
  import httpParser.StandingRequestScheduleHttpParserResponse

  "The StandingRequestScheduleConnector" should {

    "return a UnexpectedStandingRequestScheduleFailure error model when there is a HTTP exception" in {
      val exception = new RequestTimeoutException("Request timed out!!!")
      mockHttpGet[StandingRequestScheduleHttpParserResponse](
        Future.failed(exception)
      )
      val result: Future[StandingRequestScheduleHttpParserResponse] =
        connector.getSrsInformation(testUser.vrn)

      await(result) shouldBe Left(
        UnexpectedStandingRequestScheduleFailure(BAD_GATEWAY, exception.message)
      )
    }

    "return SrsInactiveUser when 422 with code 004 (Customer Is Not On VAT Scheme) is returned" in {
      mockHttpGet[StandingRequestScheduleHttpParserResponse](
        Future.successful(
          Left(SrsInactiveUser)
        )
      )

      val result: Future[StandingRequestScheduleHttpParserResponse] =
        connector.getSrsInformation(testUser.vrn)

      await(result) shouldBe Left(SrsInactiveUser)
    }

    "return UnexpectedStandingRequestScheduleFailure when 422 with unknown error code is returned" in {
      val unexpected422Body =
        """
      |{
      |  "errors": {
      |    "processingDate": "2025-04-25T15:21:39Z",
      |    "code": "999",
      |    "text": "Some unexpected error"
      |  }
      |}
    """.stripMargin

      mockHttpGet[StandingRequestScheduleHttpParserResponse](
        Future.successful(
          Left(UnexpectedStandingRequestScheduleFailure(422, unexpected422Body))
        )
      )

      val result: Future[StandingRequestScheduleHttpParserResponse] =
        connector.getSrsInformation(testUser.vrn)

      await(result) shouldBe Left(
        UnexpectedStandingRequestScheduleFailure(422, unexpected422Body)
      )
    }

    "return a UnexpectedGetVatCustomerInformationFailure error model when there is a HTTP exception" in {
      mockHttpGet[StandingRequestScheduleHttpParserResponse](
        Future.successful(
          Right(standingRequestScheduleModel)
        )
      )
      val result: Future[StandingRequestScheduleHttpParserResponse] =
        connector.getSrsInformation(testUser.vrn)

      await(result) shouldBe Right(standingRequestScheduleModel)
    }
  }
}
