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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Environment, Mode}
import play.api.libs.json.Writes
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}

trait ComponentSpecBase extends AnyWordSpecLike with Matchers with GuiceOneServerPerSuite with WiremockHelper
  with BeforeAndAfterAll with BeforeAndAfterEach {
  lazy val ws = app.injector.instanceOf[WSClient]

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build()
  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.des.url" -> mockUrl,
    "microservice.services.hip.url" -> mockUrl
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  def get[T](uri: String): WSResponse = {
    await(
      buildClient(uri).get()
    )
  }

  def post[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHttpHeaders(
          "Content-Type" -> "application/json",
          "X-Session-ID" -> "123456-session"
        )
        .post(writes.writes(body).toString())
    )
  }

  def put[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .put(writes.writes(body).toString())
    )
  }

  def buildClient(path: String): WSRequest =
    ws.url(s"http://localhost:$port/vat-subscription$path")
      .withHttpHeaders(
        "Content-Type" -> "application/json",
        "X-Session-ID" -> "123456-session",
        "Authorization" -> "localToken"
      )
      .withFollowRedirects(false)

}
