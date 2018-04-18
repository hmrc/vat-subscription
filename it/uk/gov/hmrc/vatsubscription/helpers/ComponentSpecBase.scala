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

package uk.gov.hmrc.vatsubscription.helpers

import helpers.WiremockHelper
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Environment, Mode}
import play.api.libs.json.Writes
import play.api.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.config.featureswitch.{FeatureSwitch, FeatureSwitching}

trait ComponentSpecBase extends UnitSpec with GuiceOneServerPerSuite with WiremockHelper
  with BeforeAndAfterAll with BeforeAndAfterEach with FeatureSwitching {
  lazy val ws = app.injector.instanceOf[WSClient]

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build
  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.agent-client-relationships.host" -> mockHost,
    "microservice.services.agent-client-relationships.port" -> mockPort,
    "microservice.services.tax-enrolments.host" -> mockHost,
    "microservice.services.tax-enrolments.port" -> mockPort,
    "microservice.services.email-verification.host" -> mockHost,
    "microservice.services.email-verification.port" -> mockPort,
    "microservice.services.des.url" -> mockUrl,
    "microservice.services.authenticator.host" -> mockHost,
    "microservice.services.authenticator.port" -> mockPort,
    "microservice.services.identity-verification-frontend.host" -> mockHost,
    "microservice.services.identity-verification-frontend.port" -> mockPort,
    "microservice.services.vat-subscription.host" -> mockHost,
    "microservice.services.vat-subscription.port" -> mockPort
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    FeatureSwitch.switches foreach disable
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  def get[T](uri: String): WSResponse = {
    await(
      buildClient(uri).get
    )
  }

  def post[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHeaders(
          "Content-Type" -> "application/json"
        )
        .post(writes.writes(body).toString())
    )
  }

  def put[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHeaders(
          "Content-Type" -> "application/json"
        )
        .put(writes.writes(body).toString())
    )
  }

  def buildClient(path: String) = ws.url(s"http://localhost:$port/vat-subscription$path").withFollowRedirects(false)

}
