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

package models.updateVatSubscription.request

import helpers.PPOBTestConstants.{contactDetailsModelMax, invalidPhoneDetails, ppobModelMaxPost}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json

class UpdatedPPOBSpec extends AnyWordSpecLike with Matchers {

  "UpdatedPPOB" should {

    "write to JSON" in {

      val expected = Json.obj(
        "PPOBDetails" -> Json.obj(
          "PPOBAddress" -> ppobModelMaxPost.address,
          "PPOBCommDetails" -> ppobModelMaxPost.contactDetails,
          "websiteAddress" -> ppobModelMaxPost.websiteAddress
        )
      )

      Json.toJson(UpdatedPPOB(ppobModelMaxPost)) shouldBe expected
    }
  }

  "The convertUKCountryCodes function" should {

    "modify phone numbers to convert the UK country code (+44)" when {

      "landline number contains +44" in {
        val invalidLandline = Some(contactDetailsModelMax.copy(phoneNumber = Some("+441613334444")))
        val ppobPost = ppobModelMaxPost.copy(contactDetails = invalidLandline)
        val model = UpdatedPPOB(ppobPost).convertUKCountryCodes

        model.updatedPPOB.contactDetails.flatMap(_.phoneNumber) shouldBe Some("01613334444")
      }

      "mobile number contains +44" in {
        val invalidMobile = Some(contactDetailsModelMax.copy(mobileNumber = Some("+447707707712")))
        val ppobPost = ppobModelMaxPost.copy(contactDetails = invalidMobile)
        val model = UpdatedPPOB(ppobPost).convertUKCountryCodes

        model.updatedPPOB.contactDetails.flatMap(_.mobileNumber) shouldBe Some("07707707712")
      }

      "both numbers contain +44" in {
        val ppobPost = ppobModelMaxPost.copy(contactDetails = Some(invalidPhoneDetails))
        val model = UpdatedPPOB(ppobPost).convertUKCountryCodes

        model.updatedPPOB.contactDetails.flatMap(_.phoneNumber) shouldBe Some("01613334444")
        model.updatedPPOB.contactDetails.flatMap(_.mobileNumber) shouldBe Some("07707707712")
      }
    }

    "not modify contact details" when {

      "neither phone number has a country code" in {
        val model = UpdatedPPOB(ppobModelMaxPost).convertUKCountryCodes

        model.updatedPPOB.contactDetails shouldBe ppobModelMaxPost.contactDetails
      }

      "phone numbers have non-UK country codes" in {
        val nonUKPhoneDetails =
          Some(contactDetailsModelMax.copy(phoneNumber = Some("+11613334444"), mobileNumber = Some("+5547707707712")))
        val ppobPost = ppobModelMaxPost.copy(contactDetails = nonUKPhoneDetails)
        val model = UpdatedPPOB(ppobPost).convertUKCountryCodes

        model.updatedPPOB.contactDetails shouldBe ppobPost.contactDetails
      }

      "there are no phone numbers" in {
        val noPhoneDetails = Some(contactDetailsModelMax.copy(phoneNumber = None, mobileNumber = None))
        val ppobPost = ppobModelMaxPost.copy(contactDetails = noPhoneDetails)
        val model = UpdatedPPOB(ppobPost).convertUKCountryCodes

        model.updatedPPOB.contactDetails shouldBe ppobPost.contactDetails
      }

      "there are no contact details" in {
        val ppobPost = ppobModelMaxPost.copy(contactDetails = None)
        val model = UpdatedPPOB(ppobPost).convertUKCountryCodes

        model.updatedPPOB.contactDetails shouldBe ppobPost.contactDetails
      }
    }
  }
}
