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
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.models.SubscriptionRequest

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

    "throw NoSuchElementException where the vat number doesn't exist" in {
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

    "delete the nino if it already exists" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        _ <- repo.upsertNino(testVatNumber, testNino)
        withNino <- repo.findById(testVatNumber)
        _ <- repo.upsertCompanyNumber(testVatNumber, testCompanyNumber)
        withoutNino <- repo.findById(testVatNumber)
      } yield (withNino, withoutNino)

      val (withNino, withoutNino) = await(res)

      withNino should contain(testSubscriptionRequest.copy(nino = Some(testNino), companyNumber = None))
      withoutNino should contain(testSubscriptionRequest)
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

    "throw NoSuchElementException where the vat number doesn't exist" in {
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

  "upsertNino" should {
    val testSubscriptionRequest = SubscriptionRequest(
      vatNumber = testVatNumber,
      nino = Some(testNino)
    )

    "throw NoSuchElementException where the vat number doesn't exist" in {
      val res = for {
        _ <- repo.upsertNino(testVatNumber, testNino)
        model <- repo.findById(testVatNumber)
      } yield model

      intercept[NoSuchElementException] {
        await(res)
      }
    }

    "update the subscription request where there isn't already a nino stored" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        _ <- repo.upsertNino(testVatNumber, testNino)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(testSubscriptionRequest)
    }

    "delete the company number if it already exists" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        _ <- repo.upsertCompanyNumber(testVatNumber, testCompanyNumber)
        withCompanyNumber <- repo.findById(testVatNumber)
        _ <- repo.upsertNino(testVatNumber, testNino)
        withoutCompanyNumber <- repo.findById(testVatNumber)
      } yield (withCompanyNumber, withoutCompanyNumber)

      val (withCompanyNumber, withoutCompanyNumber) = await(res)

      withCompanyNumber should contain(testSubscriptionRequest.copy(companyNumber = Some(testCompanyNumber), nino = None))
      withoutCompanyNumber should contain(testSubscriptionRequest)
    }


    "replace an existing stored nino" in {
      val newNino = UUID.randomUUID().toString
      val res = for {
        _ <- repo.insert(testSubscriptionRequest)
        _ <- repo.upsertNino(testVatNumber, newNino)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(SubscriptionRequest(testVatNumber, nino = Some(newNino)))
    }
  }

  "upsertIdentityVerified" should {
    "throw NoSuchElementException where the vat number doesn't exist" in {
      val res = for {
        _ <- repo.upsertIdentityVerified(testVatNumber)
        model <- repo.findById(testVatNumber)
      } yield model

      intercept[NoSuchElementException] {
        await(res)
      }
    }

    "update the subscription request with IdentityVerified" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        _ <- repo.upsertIdentityVerified(testVatNumber)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(SubscriptionRequest(
        vatNumber = testVatNumber,
        identityVerified = true
      ))
    }
  }

  "deleteRecord" should {
    "delete the entry stored against the vrn" in {
      val res = for {
        _ <- repo.insertVatNumber(testVatNumber)
        inserted <- repo.findById(testVatNumber)
        _ <- repo.deleteRecord(testVatNumber)
        postDelete <- repo.findById(testVatNumber)
      } yield (inserted, postDelete)

      val (inserted, postDelete) = await(res)
      inserted should contain(SubscriptionRequest(testVatNumber))
      postDelete shouldBe None
    }
  }

}
