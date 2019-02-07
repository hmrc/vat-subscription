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

package assets

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.config.AppConfig
import uk.gov.hmrc.vatsubscription.config.featureSwitch.{Api1365R7, Api1363R7}

import scala.concurrent.ExecutionContext

class TestUtil extends UnitSpec with GuiceOneAppPerSuite with MockitoSugar with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockAppConfig.features.stubDes(false)
    mockAppConfig.features.latestApi1363Version(true)
    mockAppConfig.features.api1365Version(Api1365R7)
    mockAppConfig.features.api1363Version(Api1363R7)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    mockAppConfig.features.stubDes(false)
    mockAppConfig.features.latestApi1363Version(true)
    mockAppConfig.features.api1365Version(Api1365R7)
    mockAppConfig.features.api1363Version(Api1363R7)
  }

  lazy val injector: Injector = app.injector
  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = app.materializer
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  implicit lazy val mockAppConfig: AppConfig = injector.instanceOf[AppConfig]
  implicit lazy val mockHttp: HttpClient = mock[HttpClient]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

}
