package uk.gov.hmrc.vatsubscription.helpers.servicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping


object SignUpStub extends WireMockMethods {

  def stubSignUp[T](status: Int): StubMapping =
    when(method = POST, uri = "/customer/signup/VATC",
      headers = Map(
        "Authorization" -> "Bearer dev",
        "Environment" -> "dev",
        "Content-Type" -> "application/json"
      )
    ).thenReturn(status = status)

}
