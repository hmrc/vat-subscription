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

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest

import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionRequestRepositoryISpec extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val repo: SubscriptionRequestRepository = app.injector.instanceOf[SubscriptionRequestRepository]

  private val testCredential: String = UUID.randomUUID().toString
  private val testVrn: String = UUID.randomUUID().toString

  private val testSubscriptionRequest = SubscriptionRequest(
    internalId = testCredential,
    vatNumber = Some(testVrn)
  )

  override def beforeEach: Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  "insert" should {
    "successfully insert and retrieve a SubscriptionRequest model" in {
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        model <- repo.findById(testCredential)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }
  }

  "upsertVatNumber" should {
    "insert the subscription request where there is not already one" in {
      val res = for {
        _ <- repo.upsertVatNumber(testCredential, testVrn)
        model <- repo.findById(testCredential)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "update the subscription request where one already exists" in {
      val res = for {
        _ <- repo.insert(SubscriptionRequest(testCredential, None))
        _ <- repo.upsertVatNumber(testCredential, testVrn)
        model <- repo.findById(testCredential)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "replace an existing stored vrn" in {
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        _ <- repo.upsertVatNumber(testCredential, testVrn)
        model <- repo.findById(testCredential)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }
  }
}
