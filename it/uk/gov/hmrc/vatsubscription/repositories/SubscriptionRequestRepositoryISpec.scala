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
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._

import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionRequestRepositoryISpec extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val repo: SubscriptionRequestRepository = app.injector.instanceOf[SubscriptionRequestRepository]

  private val testSubscriptionRequest = SubscriptionRequest(
    internalId = testInternalId,
    vatNumber = Some(testVatNumber),
    companyNumber = Some(testCompanyNumber)
  )

  override def beforeEach: Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  "insert" should {
    "successfully insert and retrieve a SubscriptionRequest model" in {
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        model <- repo.findById(testInternalId)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }
  }

  "upsertVatNumber" should {
    val testSubscriptionRequest = SubscriptionRequest(
      internalId = testInternalId,
      vatNumber = Some(testVatNumber)
    )

    "insert the subscription request where there is not already one" in {
      val res = for {
        _ <- repo.upsertVatNumber(testInternalId, testVatNumber)
        model <- repo.findById(testInternalId)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "update the subscription request where one already exists" in {
      val res = for {
        _ <- repo.insert(SubscriptionRequest(testInternalId, None))
        _ <- repo.upsertVatNumber(testInternalId, testVatNumber)
        model <- repo.findById(testInternalId)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "replace an existing stored vat number" in {
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        _ <- repo.upsertVatNumber(testInternalId, testVatNumber)
        model <- repo.findById(testInternalId)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }
  }


  "upsertCompanyNumber" should {
    val testSubscriptionRequest = SubscriptionRequest(
      internalId = testInternalId,
      companyNumber = Some(testCompanyNumber)
    )

    "insert the subscription request where there is not already one" in {
      val res = for {
        _ <- repo.upsertCompanyNumber(testInternalId, testCompanyNumber)
        model <- repo.findById(testInternalId)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "update the subscription request where one already exists with an existing vat number" in {
      val expectedSubscriptionRequest = SubscriptionRequest(
        internalId = testInternalId,
        vatNumber = Some(testVatNumber),
        companyNumber = Some(testCompanyNumber)
      )

      val res = for {
        _ <- repo.insert(SubscriptionRequest(
          internalId = testInternalId,
          vatNumber = Some(testVatNumber),
          companyNumber = None
        ))
        _ <- repo.upsertCompanyNumber(testInternalId, testCompanyNumber)
        model <- repo.findById(testInternalId)
      } yield model

      await(res) should contain(expectedSubscriptionRequest)
    }

    "replace an existing stored company number" in {
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        _ <- repo.upsertCompanyNumber(testInternalId, testCompanyNumber)
        model <- repo.findById(testInternalId)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }
  }
}
