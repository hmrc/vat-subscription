package uk.gov.hmrc.vatsubscription.controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.GetVatCustomerInformationStub._
import uk.gov.hmrc.vatsubscription.helpers.{ComponentSpecBase, CustomMatchers}
import uk.gov.hmrc.vatsubscription.models.MTDfBMandated

class GetMandationStatusControllerISpec extends ComponentSpecBase with BeforeAndAfterEach with CustomMatchers {

  val testSuccessResponse = Json.obj(
    "approvedInformation" -> Json.obj(
      "customerDetails" -> Json.obj("mandationStatus" -> "1")
    )
  )

  "/:vatNumber/mandation-status " when {
    "calls to DES is successful" should {
      "return OK with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(OK, testSuccessResponse)

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(OK),
          jsonBodyAs(Json.obj("mandationStatus" -> MTDfBMandated))
        )
      }
    }

    "calls to DES returend BAD_REQUEST" should {
      "return BAD_REQUEST with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(BAD_REQUEST, Json.obj())

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(BAD_REQUEST)
        )
      }
    }

    "calls to DES returend NOT_FOUND" should {
      "return NOT_FOUND with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(NOT_FOUND, Json.obj())

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "calls to DES returend anything else" should {
      "return INTERNAL_SERVER_ERROR with the status" in {
        stubAuth(OK, successfulAuthResponse())
        stubGetInformation(testVatNumber)(INTERNAL_SERVER_ERROR, Json.obj())

        val res = await(get(s"/$testVatNumber/mandation-status"))

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }

  }

}
