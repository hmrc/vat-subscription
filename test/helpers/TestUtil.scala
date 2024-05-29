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

package helpers

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import config.AppConfig
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import scala.concurrent.ExecutionContext

trait TestUtil extends AnyWordSpecLike with Matchers with GuiceOneAppPerSuite with MockitoSugar with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  lazy val injector: Injector = app.injector
  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = app.materializer
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  implicit lazy val mockAppConfig: AppConfig = injector.instanceOf[AppConfig]
  implicit lazy val mockHttp: HttpClient = mock[HttpClient]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  implicit lazy val controllerComponents: ControllerComponents = injector.instanceOf[ControllerComponents]

}
