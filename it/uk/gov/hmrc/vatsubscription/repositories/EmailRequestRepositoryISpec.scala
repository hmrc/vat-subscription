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

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.models.EmailRequest

import scala.concurrent.ExecutionContext.Implicits.global

class EmailRequestRepositoryISpec extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val repo: EmailRequestRepository = app.injector.instanceOf[EmailRequestRepository]

  private val testEmailRequest = EmailRequest(
    vatNumber = testVatNumber,
    email = testEmail
  )

  override def beforeEach: Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  "insertEmail" should {
    "insert the Email request where there is not already one" in {
      val res = for {
        _ <- repo.insertEmail(testVatNumber, testEmail)
        model <- repo.findById(testVatNumber)
      } yield model

      await(res) should contain(testEmailRequest)
    }

    "throw an exception when email already stored for vat user" in {
      intercept[DatabaseException] {
        await(for {
          _ <- repo.insert(EmailRequest(testVatNumber, testEmail))
          res <- repo.insertEmail(testVatNumber, testEmail)
        } yield res)
      }
    }
  }

  "deleteRecord" should {
    "delete the entry stored against the vrn" in {
      val res = for {
        _ <- repo.insertEmail(testVatNumber, testEmail)
        inserted <- repo.findById(testVatNumber)
        _ <- repo.deleteRecord(testVatNumber)
        postDelete <- repo.findById(testVatNumber)
      } yield (inserted, postDelete)

      val (inserted, postDelete) = await(res)
      inserted should contain(EmailRequest(testVatNumber, testEmail))
      postDelete shouldBe None
    }
  }

}
