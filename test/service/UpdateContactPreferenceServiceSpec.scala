/*
 * Copyright 2020 HM Revenue & Customs
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

package service

import assets.TestUtil
import connectors.mocks.MockUpdateVatSubscriptionConnector
import helpers.BaseTestConstants.testUser
import httpparsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.{ContactDetails, DigitalPreference, PaperPreference}
import helpers.PPOBTestConstants._
import helpers.CustomerInformationTestConstants._
import models.get.PPOBGet
import models.post.{CommsPreferencePost, PPOBAddressPost, PPOBPost}
import models.updateVatSubscription.request._
import models.updateVatSubscription.response.{ErrorModel, SuccessModel}
import services.UpdateContactPreferenceService

class UpdateContactPreferenceServiceSpec extends TestUtil with MockUpdateVatSubscriptionConnector {

  def setup(response: UpdateVatSubscriptionResponse): UpdateContactPreferenceService = {
    mockUpdateVatSubscriptionResponse(response)
    new UpdateContactPreferenceService(mockUpdateVatSubscriptionConnector)
  }

  "Calling .updateContactPreference" when {

    "connector call is successful" should {

      "return successful UpdateVatSubscriptionResponse model" in {
        val service = setup(Right(SuccessModel("12345")))
          val result =
            service.updateContactPreference(CommsPreferencePost(PaperPreference), welshIndicator = false)(testUser, hc, ec)
          await(result) shouldEqual Right(SuccessModel("12345"))
      }
    }

    "connector call is unsuccessful" should {

      "return ErrorModel" in {
        val service = setup(Left(ErrorModel("ERROR", "Error")))
        val result =
          service.updateContactPreference(CommsPreferencePost(PaperPreference), welshIndicator = false)(testUser, hc, ec)
        await(result) shouldEqual Left(ErrorModel("ERROR", "Error"))
      }
    }
  }

  "Calling .constructUpdateContactPreferenceModel" when {

    val service = new UpdateContactPreferenceService(mockUpdateVatSubscriptionConnector)

    "user does not have a welshIndicator" should {

      val result =
        service.constructContactPreferenceModel(CommsPreferencePost(PaperPreference), welshIndicator = false)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangeCommsPreference,
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = Some(PaperPreference)
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }

    "user has a welshIndicator" should {

      val result =
        service.constructContactPreferenceModel(CommsPreferencePost(PaperPreference), welshIndicator = true)

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = true),
        requestedChanges = ChangeCommsPreference,
        updatedPPOB = None,
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = Some(PaperPreference)
      )

      "return a correct UpdateVatSubscription model" in {
        result shouldEqual expectedResult
      }
    }
  }

  "Calling .constructContactPreferenceAndEmailModel" when {

    val service = new UpdateContactPreferenceService(mockUpdateVatSubscriptionConnector)

    "all optional fields in the 'PPOBGet' model are present" should {

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = true),
        requestedChanges = ChangeCommsPreferenceAndEmail,
        updatedPPOB = Some(UpdatedPPOB(PPOBPost(
          address = PPOBAddressPost(
            ppobAddressModelMax.line1,
            ppobAddressModelMax.line2,
            ppobAddressModelMax.line3,
            ppobAddressModelMax.line4,
            ppobAddressModelMax.line5,
            ppobAddressModelMax.postCode,
            ppobAddressModelMax.countryCode
          ),
          contactDetails = Some(ContactDetails(
            contactDetailsModelMax.phoneNumber,
            contactDetailsModelMax.mobileNumber,
            contactDetailsModelMax.faxNumber,
            Some("newemail@email.com"),
            emailVerified = Some(true)
          )),
          websiteAddress = Some(website),
          transactorOrCapacitorEmail = None
        ))),
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = Some(DigitalPreference)
      )

      val result = service.constructContactPreferenceAndEmailModel(
        newEmail = "newemail@email.com",
        welshIndicator = Some(true),
        currentContactDetails = ppobModelMax
      )

      "return the correct UpdateVatSubscription model" in {
        result shouldBe expectedResult
      }
    }

    "all optional fields in the 'PPOBGet' model are not present" should {

      val expectedResult = UpdateVatSubscription(
        controlInformation = ControlInformation(welshIndicator = false),
        requestedChanges = ChangeCommsPreferenceAndEmail,
        updatedPPOB = Some(UpdatedPPOB(PPOBPost(
          address = PPOBAddressPost(None, None, None, None, None, None, None),
          contactDetails = Some(ContactDetails(
            None,
            None,
            None,
            Some("newemail@email.com"),
            emailVerified = Some(true)
          )),
          websiteAddress = None,
          transactorOrCapacitorEmail = None
        ))),
        updatedReturnPeriod = None,
        updateDeregistrationInfo = None,
        declaration = Declaration(None, Signing()),
        commsPreference = Some(DigitalPreference)
      )

      val result = service.constructContactPreferenceAndEmailModel(
        newEmail = "newemail@email.com",
        welshIndicator = None,
        currentContactDetails = PPOBGet(ppobAddressModelMin, None, None)
      )

      "return the correct UpdateVatSubscription model" in {
        result shouldBe expectedResult
      }
    }
  }

  "Calling .updatePreferenceAndEmail" when {

    "connector call is successful" should {

      lazy val service = setup(Right(SuccessModel("12345")))
      lazy val result = service.updatePreferenceAndEmail("newemail@email.com", customerInformationModelMax)(testUser, hc, ec)

      "return successful UpdateVatSubscriptionResponse model" in {
        await(result) shouldEqual Right(SuccessModel("12345"))
      }
    }

    "connector call is unsuccessful" should {

      lazy val service = setup(Left(ErrorModel("ERROR", "Error")))
      lazy val result = service.updatePreferenceAndEmail("newemail@email.com", customerInformationModelMax)(testUser, hc, ec)

      "return ErrorModel" in {
        await(result) shouldEqual Left(ErrorModel("ERROR", "Error"))
      }
    }
  }
}
