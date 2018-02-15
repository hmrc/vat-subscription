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

package uk.gov.hmrc.vatsubscription.repositories

import java.util.UUID

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest
import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionRequestRepositorySpec extends UnitSpec with GuiceOneAppPerSuite {
  val repo: SubscriptionRequestRepository = app.injector.instanceOf[SubscriptionRequestRepository]

  "insert" should {
    "successfully insert and retrieve a SubscriptionRequest model" in {
      val testCredential = UUID.randomUUID().toString
      val testVrn = UUID.randomUUID().toString

      val testSubscriptionRequest = SubscriptionRequest(
        credentialId = testCredential,
        vrn = Some(testVrn)
      )

      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        model <- repo.findById(testCredential)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }
  }
}
