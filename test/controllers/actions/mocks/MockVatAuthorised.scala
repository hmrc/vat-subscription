/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.actions.mocks

import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import config.Constants
import connectors.mocks.MockAuthConnector
import controllers.actions.VatAuthorised
import helpers.BaseTestConstants.testVatNumber
import helpers.TestUtil

trait MockVatAuthorised extends TestUtil with MockAuthConnector {

  val mockVatAuthorised = new VatAuthorised(mockAuthConnector, controllerComponents, mockAppConfig)

  val vatAuthPredicate: Predicate = Enrolment(Constants.MtdVatEnrolmentKey)
    .withIdentifier(Constants.MtdVatReferenceKey, testVatNumber)
    .withDelegatedAuthRule(Constants.MtdVatDelegatedAuth)

  val allEnrolments: Retrieval[Enrolments] = Retrievals.allEnrolments

}