package uk.gov.hmrc.vatsubscription.connectors

import uk.gov.hmrc.vatsubscription.helpers.ComponentSpecBase
import uk.gov.hmrc.vatsubscription.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsubscription.helpers.servicemocks.SignUpStub._
import uk.gov.hmrc.vatsubscription.models.CustomerSignUpResponseSuccess
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

class CustomerSignUpConnectorISpec extends ComponentSpecBase {

  lazy val connector: CustomerSignUpConnector = app.injector.instanceOf[CustomerSignUpConnector]

  "customerSignUp" should {
    "add the additional headers to des" in {
      implicit val hc = HeaderCarrier()
      stubSignUp(OK)

      val res = connector.signUp(testSafeId, testVatNumber, testEmail, emailVerified = true)

      await(res) shouldBe Right(CustomerSignUpResponseSuccess)
    }
  }

}
