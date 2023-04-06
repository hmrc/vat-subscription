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

package helpers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Writes
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

trait MockHttpClient extends AnyWordSpecLike with Matchers with MockitoSugar with BeforeAndAfterEach {

  val mockHttpClient: HttpClient = mock[HttpClient]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpClient)
  }

  def mockHttpGet[I](response: Future[I]): Unit =
    when(mockHttpClient.GET[I]
      (any[String](), any[Seq[(String, String)]], any[Seq[(String, String)]])
      (any[HttpReads[I]], any[HeaderCarrier], any[ExecutionContext]))
      .thenReturn(response)

  def mockHttpPut[I, O](response: Future[O]): Unit =
    when(mockHttpClient.PUT[I, O]
      (any[String](), any[I](), any[Seq[(String, String)]]())
      (any[Writes[I]](), any[HttpReads[O]](), any[HeaderCarrier](), any[ExecutionContext]()))
      .thenReturn(response)
}
