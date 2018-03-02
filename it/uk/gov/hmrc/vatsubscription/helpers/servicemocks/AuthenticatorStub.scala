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

package uk.gov.hmrc.vatsubscription.helpers.servicemocks

import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers.UserMatchFailureResponseModel
import uk.gov.hmrc.vatsubscription.models.UserDetailsModel

object AuthenticatorStub extends WireMockMethods {

  def stubMatchUser(userDetails: UserDetailsModel)(matched: Boolean): Unit = {
    if (matched)
      when(method = POST, uri = "/authenticator/match", body = userDetails)
        .thenReturn(OK, Map("Content-Type" -> "application/json"), Json.parse(s"""{"nino":"$testNino"}"""))
    else
      when(method = POST, uri = "/authenticator/match")
        .thenReturn(UNAUTHORIZED, Map("Content-Type" -> "application/json"), UserMatchFailureResponseModel("failed"))
  }

}
