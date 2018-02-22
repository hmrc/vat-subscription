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
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._

import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionRequestRepositoryISpec extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val repo: SubscriptionRequestRepository = app.injector.instanceOf[SubscriptionRequestRepository]

  private val testSubscriptionRequest = SubscriptionRequest(
    vatNumber = testVatNumber,
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
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }
  }

  "insertVatNumber" should {
    val testSubscriptionRequest = SubscriptionRequest(
      vatNumber = testVatNumber
    )

    "insert the subscription request where there is not already one" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "fail the request when one already exists" in {
      val res = for {
        _ <- repo.insert(SubscriptionRequest(testVatNumber, Some(testCompanyNumber)))
        _ <- repo.insertVatNumber(testVatNumber)
      } yield Unit

      intercept[DatabaseException] {
        await(res)
      }
    }
  }


  "upsertCompanyNumber" should {
    val testSubscriptionRequest = SubscriptionRequest(
      vatNumber = testVatNumber,
      companyNumber = Some(testCompanyNumber)
    )

    "insert the subscription request where the vat number doesn't exist" in {
      val res = for {
        _ <- repo.upsertCompanyNumber(testVatNumber, testCompanyNumber)
        model <- repo.findById(testVatNumber)
      } yield model

      intercept[NoSuchElementException] {
        await(res)
      }
    }

    "update the subscription request where there isn't already a company number stored" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        _ <- repo.upsertCompanyNumber(testVatNumber, testCompanyNumber)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "replace an existing stored company number" in {
      val newCompanyNumber = UUID.randomUUID().toString
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        _ <- repo.upsertCompanyNumber(testVatNumber, newCompanyNumber)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(SubscriptionRequest(testVatNumber, companyNumber = Some(newCompanyNumber)))
    }
  }

  "upsertEmail" should {
    val testSubscriptionRequest = SubscriptionRequest(
      vatNumber = testVatNumber,
      email = Some(testEmail)
    )

    "insert the subscription request where the vat number doesn't exist" in {
      val res = for {
        _ <- repo.upsertEmail(testVatNumber, testEmail)
        model <- repo.findById(testVatNumber)
      } yield model

      intercept[NoSuchElementException] {
        await(res)
      }
    }

    "update the subscription request where there isn't already an email stored" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        _ <- repo.upsertEmail(testVatNumber, testEmail)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "replace an existing stored email" in {
      val newEmail = UUID.randomUUID().toString
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        _ <- repo.upsertEmail(testVatNumber, newEmail)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(SubscriptionRequest(testVatNumber, email = Some(newEmail)))
    }
  }

}
