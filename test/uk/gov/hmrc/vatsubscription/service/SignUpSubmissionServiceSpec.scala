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

package uk.gov.hmrc.vatsubscription.service

import org.scalatest.EitherValues
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscription.connectors.mocks.{MockCustomerSignUpConnector, MockEmailVerificationConnector, MockRegistrationConnector, MockTaxEnrolmentsConnector}
import uk.gov.hmrc.vatsubscription.helpers.TestConstants._
import uk.gov.hmrc.vatsubscription.httpparsers._
import uk.gov.hmrc.vatsubscription.models.{CustomerSignUpResponseFailure, CustomerSignUpResponseSuccess, SubscriptionRequest}
import uk.gov.hmrc.vatsubscription.repositories.mocks.{MockEmailRequestRepository, MockSubscriptionRequestRepository}
import uk.gov.hmrc.vatsubscription.services._
import SignUpSubmissionService._
import play.api.test.FakeRequest
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.vatsubscription.helpers.TestConstants
import uk.gov.hmrc.vatsubscription.models.monitoring.RegisterWithMultipleIDsAuditing.RegisterWithMultipleIDsAuditModel
import uk.gov.hmrc.vatsubscription.models.monitoring.SignUpAuditing.SignUpAuditModel
import uk.gov.hmrc.vatsubscription.service.mocks.monitoring.MockAuditService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SignUpSubmissionServiceSpec extends UnitSpec with EitherValues
  with MockSubscriptionRequestRepository with MockEmailVerificationConnector
  with MockCustomerSignUpConnector with MockRegistrationConnector
  with MockTaxEnrolmentsConnector with MockAuditService with MockEmailRequestRepository {

  object TestSignUpSubmissionService extends SignUpSubmissionService(
    mockSubscriptionRequestRepository,
    mockEmailRequestRepository,
    mockEmailVerificationConnector,
    mockCustomerSignUpConnector,
    mockRegistrationConnector,
    mockTaxEnrolmentsConnector,
    mockAuditService
  )

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  private implicit val request = FakeRequest("POST", "testUrl")

  "submitSignUpRequest" when {
    "the user is a delegate and " when {
      val enrolments = Enrolments(Set(testAgentEnrolment))

      "there is a complete company sign up request in storage" when {
        "the e-mail verification request returns that the e-mail address is verified" when {
          "the registration request is successful" when {
            "the sign up request is successful" when {
              "the enrolment call is successful" should {
                "return a SignUpRequestSubmitted for an individual signup" in {
                  val testSubscriptionRequest = SubscriptionRequest(
                    vatNumber = testVatNumber,
                    nino = Some(testNino),
                    email = Some(testEmail)
                  )

                  mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                  mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                  mockRegisterIndividual(testVatNumber, testNino)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                  mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Right(CustomerSignUpResponseSuccess)))
                  mockRegisterEnrolment(testVatNumber, testSafeId)(Future.successful(Right(SuccessfulTaxEnrolment)))
                  mockDeleteRecord(testVatNumber)(mock[WriteResult])
                  mockUpsertEmailAfterSubscription(testVatNumber, testEmail)(mock[UpdateWriteResult])

                  val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                  res.right.value shouldBe SignUpRequestSubmitted

                  verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber,None, Some(TestConstants.testNino),
                    Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                }
                "return a SignUpRequestSubmitted for a company sign up" in {
                  val testSubscriptionRequest = SubscriptionRequest(
                    vatNumber = testVatNumber,
                    companyNumber = Some(testCompanyNumber),
                    email = Some(testEmail)
                  )

                  mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                  mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                  mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                  mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Right(CustomerSignUpResponseSuccess)))
                  mockRegisterEnrolment(testVatNumber, testSafeId)(Future.successful(Right(SuccessfulTaxEnrolment)))
                  mockDeleteRecord(testVatNumber)(mock[WriteResult])
                  mockUpsertEmailAfterSubscription(testVatNumber, testEmail)(mock[UpdateWriteResult])

                  val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                  res.right.value shouldBe SignUpRequestSubmitted

                  verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None,
                    Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                  verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, true,
                    Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                }
              }
              "the enrolment call fails" should {
                "return an EnrolmentFailure" in {
                  val testSubscriptionRequest = SubscriptionRequest(
                    vatNumber = testVatNumber,
                    companyNumber = Some(testCompanyNumber),
                    email = Some(testEmail)
                  )

                  mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                  mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                  mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                  mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Right(CustomerSignUpResponseSuccess)))
                  mockRegisterEnrolment(testVatNumber, testSafeId)(Future.successful(Left(FailedTaxEnrolment(BAD_REQUEST))))

                  val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                  res.left.value shouldBe EnrolmentFailure

                  verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None,
                    Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                  verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, true,
                    Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                }
              }
            }
            "the sign up request fails" should {
              "return a SignUpFailure" in {
                val testSubscriptionRequest = SubscriptionRequest(
                  vatNumber = testVatNumber,
                  companyNumber = Some(testCompanyNumber),
                  email = Some(testEmail)
                )

                mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Left(CustomerSignUpResponseFailure(BAD_REQUEST))))

                val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                res.left.value shouldBe SignUpFailure

                verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None,
                  Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, true,
                  Some(TestConstants.testAgentReferenceNumber), isSuccess = false))
              }
            }
          }
          "the registration request fails" should {
            "return a RegistrationFailure" in {
              val testSubscriptionRequest = SubscriptionRequest(
                vatNumber = testVatNumber,
                companyNumber = Some(testCompanyNumber),
                email = Some(testEmail)
              )

              mockFindById(testVatNumber)(
                Future.successful(Some(testSubscriptionRequest))
              )
              mockGetEmailVerificationState(testEmail)(
                Future.successful(Right(EmailVerified))
              )
              mockRegisterCompany(testVatNumber, testCompanyNumber)(
                Future.successful(Left(RegisterWithMultipleIdsErrorResponse(BAD_REQUEST, "")))
              )

              val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

              res.left.value shouldBe RegistrationFailure

              verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None,
                Some(TestConstants.testAgentReferenceNumber), isSuccess = false))
            }
          }
        }
        "the email verification request returns that the email is not verified" when {
          "the registration request is successful" when {
            "the sign up request is successful" when {
              "the enrolment call is successful" should {
                "return a SignUpRequestSubmitted" in {
                  val testSubscriptionRequest = SubscriptionRequest(
                    vatNumber = testVatNumber,
                    companyNumber = Some(testCompanyNumber),
                    email = Some(testEmail)
                  )

                  mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                  mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailNotVerified)))
                  mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                  mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = false)(Future.successful(Right(CustomerSignUpResponseSuccess)))
                  mockRegisterEnrolment(testVatNumber, testSafeId)(Future.successful(Right(SuccessfulTaxEnrolment)))
                  mockDeleteRecord(testVatNumber)(mock[WriteResult])
                  mockUpsertEmailAfterSubscription(testVatNumber, testEmail)(mock[UpdateWriteResult])

                  val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                  res.right.value shouldBe SignUpRequestSubmitted

                  verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None,
                    Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                  verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, false,
                    Some(TestConstants.testAgentReferenceNumber), isSuccess = true))
                }
              }
            }
          }
        }
        "the email verification request fails" should {
          "return an EmailVerificationFailure" in {
            val testSubscriptionRequest = SubscriptionRequest(
              vatNumber = testVatNumber,
              companyNumber = Some(testCompanyNumber),
              email = Some(testEmail)
            )

            mockFindById(testVatNumber)(
              Future.successful(Some(testSubscriptionRequest))
            )
            mockGetEmailVerificationState(testEmail)(
              Future.successful(Left(GetEmailVerificationStateErrorResponse(BAD_REQUEST, "")))
            )

            val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

            res.left.value shouldBe EmailVerificationFailure
          }
        }

      }
      "there is insufficient data in storage" should {
        "return an InsufficientData error" in {
          val incompleteSubscriptionRequest = SubscriptionRequest(
            vatNumber = testVatNumber,
            companyNumber = None,
            email = Some(testEmail)
          )

          mockFindById(testVatNumber)(
            Future.successful(Some(incompleteSubscriptionRequest))
          )

          val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

          res.left.value shouldBe InsufficientData
        }
      }

      "there is no data in storage" should {
        "return an InsufficientData error" in {
          mockFindById(testVatNumber)(
            Future.successful(None)
          )

          val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber,enrolments))

          res.left.value shouldBe InsufficientData
        }
      }
    }

    "the user is principal and " when {
      val enrolments = Enrolments(Set(testPrincipalEnrolment))

      "there is a complete company sign up request in storage" when {
        "the e-mail verification request returns that the e-mail address is verified" when {
          "the registration request is successful" when {
            "the sign up request is successful" when {
              "the enrolment call is successful" should {
                "return a SignUpRequestSubmitted for an individual signup" in {
                  val testSubscriptionRequest = SubscriptionRequest(
                    vatNumber = testVatNumber,
                    nino = Some(testNino),
                    email = Some(testEmail),
                    identityVerified = true
                  )

                  mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                  mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                  mockRegisterIndividual(testVatNumber, testNino)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                  mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Right(CustomerSignUpResponseSuccess)))
                  mockRegisterEnrolment(testVatNumber, testSafeId)(Future.successful(Right(SuccessfulTaxEnrolment)))
                  mockDeleteRecord(testVatNumber)(mock[WriteResult])
                  mockUpsertEmailAfterSubscription(testVatNumber, testEmail)(mock[UpdateWriteResult])

                  val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                  res.right.value shouldBe SignUpRequestSubmitted

                  verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber,None, Some(TestConstants.testNino), None, isSuccess = true))
                  verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, true, None, isSuccess = true))
                }
                "return a SignUpRequestSubmitted for a company sign up" in {
                  val testSubscriptionRequest = SubscriptionRequest(
                    vatNumber = testVatNumber,
                    companyNumber = Some(testCompanyNumber),
                    email = Some(testEmail),
                    identityVerified = true
                  )

                  mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                  mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                  mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                  mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Right(CustomerSignUpResponseSuccess)))
                  mockRegisterEnrolment(testVatNumber, testSafeId)(Future.successful(Right(SuccessfulTaxEnrolment)))
                  mockDeleteRecord(testVatNumber)(mock[WriteResult])
                  mockUpsertEmailAfterSubscription(testVatNumber, testEmail)(mock[UpdateWriteResult])

                  val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                  res.right.value shouldBe SignUpRequestSubmitted

                  verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None, None, isSuccess = true))
                  verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, true, None, isSuccess = true))
                }
              }
              "the enrolment call fails" should {
                "return an EnrolmentFailure" in {
                  val testSubscriptionRequest = SubscriptionRequest(
                    vatNumber = testVatNumber,
                    companyNumber = Some(testCompanyNumber),
                    email = Some(testEmail),
                    identityVerified = true
                  )

                  mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                  mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                  mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                  mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Right(CustomerSignUpResponseSuccess)))
                  mockRegisterEnrolment(testVatNumber, testSafeId)(Future.successful(Left(FailedTaxEnrolment(BAD_REQUEST))))

                  val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                  res.left.value shouldBe EnrolmentFailure

                  verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None,  None, isSuccess = true))
                  verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, true, None, isSuccess = true))
                }
              }
            }
            "the sign up request fails" should {
              "return a SignUpFailure" in {
                val testSubscriptionRequest = SubscriptionRequest(
                  vatNumber = testVatNumber,
                  companyNumber = Some(testCompanyNumber),
                  email = Some(testEmail),
                  identityVerified = true
                )

                mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
                mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailVerified)))
                mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))
                mockSignUp(testSafeId, testVatNumber, testEmail, emailVerified = true)(Future.successful(Left(CustomerSignUpResponseFailure(BAD_REQUEST))))

                val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

                res.left.value shouldBe SignUpFailure

                verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None, None, isSuccess = true))
                verifyAudit(SignUpAuditModel(TestConstants.testSafeId, TestConstants.testVatNumber, TestConstants.testEmail, true,  None, isSuccess = false))
              }
            }
          }
          "the registration request fails" should {
            "return a RegistrationFailure" in {
              val testSubscriptionRequest = SubscriptionRequest(
                vatNumber = testVatNumber,
                companyNumber = Some(testCompanyNumber),
                email = Some(testEmail),
                identityVerified = true
              )

              mockFindById(testVatNumber)(
                Future.successful(Some(testSubscriptionRequest))
              )
              mockGetEmailVerificationState(testEmail)(
                Future.successful(Right(EmailVerified))
              )
              mockRegisterCompany(testVatNumber, testCompanyNumber)(
                Future.successful(Left(RegisterWithMultipleIdsErrorResponse(BAD_REQUEST, "")))
              )

              val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

              res.left.value shouldBe RegistrationFailure

              verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None, None, isSuccess = false))
            }
          }
        }
        "the email verification request returns that the email is not verified" should {
          "return a UnVerifiedPrincipalEmailFailure" in {
            val testSubscriptionRequest = SubscriptionRequest(
              vatNumber = testVatNumber,
              companyNumber = Some(testCompanyNumber),
              email = Some(testEmail),
              identityVerified = true
            )

            mockFindById(testVatNumber)(Future.successful(Some(testSubscriptionRequest)))
            mockGetEmailVerificationState(testEmail)(Future.successful(Right(EmailNotVerified)))
            mockRegisterCompany(testVatNumber, testCompanyNumber)(Future.successful(Right(RegisterWithMultipleIdsSuccess(testSafeId))))

            val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

            res.left.value shouldBe UnVerifiedPrincipalEmailFailure

            verifyAudit(RegisterWithMultipleIDsAuditModel(TestConstants.testVatNumber, Some(TestConstants.testCompanyNumber), None,  None, isSuccess = true))
          }
        }
        "the email verification request fails" should {
          "return an EmailVerificationFailure" in {
            val testSubscriptionRequest = SubscriptionRequest(
              vatNumber = testVatNumber,
              companyNumber = Some(testCompanyNumber),
              email = Some(testEmail),
              identityVerified = true
            )

            mockFindById(testVatNumber)(
              Future.successful(Some(testSubscriptionRequest))
            )
            mockGetEmailVerificationState(testEmail)(
              Future.successful(Left(GetEmailVerificationStateErrorResponse(BAD_REQUEST, "")))
            )

            val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

            res.left.value shouldBe EmailVerificationFailure
          }
        }

      }
      "there is insufficient data in storage" should {
        "return an InsufficientData error" in {
          val incompleteSubscriptionRequest = SubscriptionRequest(
            vatNumber = testVatNumber,
            companyNumber = None,
            email = Some(testEmail),
            identityVerified = true
          )

          mockFindById(testVatNumber)(
            Future.successful(Some(incompleteSubscriptionRequest))
          )

          val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

          res.left.value shouldBe InsufficientData
        }
      }

      "identity not verified" should {
        "return an InsufficientData error" in {
          val incompleteSubscriptionRequest = SubscriptionRequest(
            vatNumber = testVatNumber,
            companyNumber = Some(testCompanyNumber),
            email = Some(testEmail),
            identityVerified = false
          )

          mockFindById(testVatNumber)(
            Future.successful(Some(incompleteSubscriptionRequest))
          )

          val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

          res.left.value shouldBe InsufficientData
        }
      }

      "there is no data in storage" should {
        "return an InsufficientData error" in {
          mockFindById(testVatNumber)(
            Future.successful(None)
          )

          val res = await(TestSignUpSubmissionService.submitSignUpRequest(testVatNumber, enrolments))

          res.left.value shouldBe InsufficientData
        }
      }
    }
  }
}