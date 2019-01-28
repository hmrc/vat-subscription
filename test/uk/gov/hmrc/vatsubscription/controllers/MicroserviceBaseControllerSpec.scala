/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.vatsubscription.controllers

import assets.TestUtil
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.PPOBTestConstants.{ppobPostExample, ppobPostExampleJson}
import uk.gov.hmrc.vatsubscription.models.User
import uk.gov.hmrc.vatsubscription.models.post.PPOBPost
import uk.gov.hmrc.vatsubscription.models.updateVatSubscription.response.ErrorModel

class MicroserviceBaseControllerSpec extends TestUtil {

  object TestMicroserviceBaseController
    extends MicroserviceBaseController

  "the parseJsonBody() method" when {

    "called with a type of PPOBPost" should {

      "return a corresponding model given matching Json in the body" in {

        val goodUser = User("123", None, "")(fakeRequest.withJsonBody(ppobPostExampleJson))

        val res = await(TestMicroserviceBaseController.parseJsonBody[PPOBPost](goodUser, Json.format[PPOBPost]))
        res shouldBe Right(ppobPostExample)
      }

      "state invalid json received when invalid Json is in the user body" in {

        val badUser = User("123", None, "")(fakeRequest.withJsonBody(Json.obj()))

        val res = await(TestMicroserviceBaseController.parseJsonBody[PPOBPost](badUser, Json.format[PPOBPost]))
        res shouldBe Left(ErrorModel("INVALID_JSON", s"Json received, but did not validate"))
      }

      "state that the user body is not json when the user body isn't Json" in {

        val badUser = User("123", None, "")(fakeRequest.withBody("badThings"))

        val res = await(TestMicroserviceBaseController.parseJsonBody[PPOBPost](badUser, Json.format[PPOBPost]))
        res shouldBe Left(ErrorModel("INVALID_JSON", s"Body of request was not JSON, badThings"))
      }
    }
  }

}
