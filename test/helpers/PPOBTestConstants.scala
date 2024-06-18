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

package helpers

import play.api.libs.json.{JsValue, Json}
import models.ContactDetails
import models.get.{PPOBAddressGet, PPOBGet}
import models.post.{EmailPost, PPOBAddressPost, PPOBPost}

object PPOBTestConstants {

  val addLine1 = "Add Line 1"
  val addLine2 = "Add Line 2"
  val addLine3 = "Add Line 3"
  val addLine4 = "Add Line 4"
  val addLine5 = "Add Line 5"
  val postcode = "TE37 7AD"
  val countryCode = "ES"

  val rlsIndicator = "0001"
  val website = "www.test.com"
  val agentEmail = "agent@emailaddress.com"

  val phoneNumber = "01234 567890"
  val mobileNumber = "07700 123456"
  val faxNumber = "01234 098765"
  val email = "test@test.com"
  val emailVerified = true

  val partyType = "50"

  val ppobAddressModelMax = PPOBAddressGet(
    Some(addLine1),
    Some(addLine2),
    Some(addLine3),
    Some(addLine4),
    Some(addLine5),
    Some(postcode),
    Some(countryCode)
  )

  val ppobAddressModelMin = PPOBAddressGet(
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  val ppobAddressModelMaxPost = PPOBAddressPost(
    Some(addLine1),
    Some(addLine2),
    Some(addLine3),
    Some(addLine4),
    Some(addLine5),
    Some(postcode),
    Some(countryCode)
  )

  val contactDetailsModelMax = ContactDetails(
    Some(phoneNumber),
    Some(mobileNumber),
    Some(faxNumber),
    Some(email),
    Some(emailVerified)
  )

  val contactDetailsModelMin: ContactDetails = ContactDetails(None, None, None, None, None)
  val invalidPhoneDetails: ContactDetails =
    contactDetailsModelMax.copy(phoneNumber = Some("+441613334444"), mobileNumber = Some("+447707707712"))

  val ppobModelMin = PPOBGet(ppobAddressModelMin.copy(line1 = Some(addLine1), countryCode = Some(countryCode)), None, None)
  val ppobModelOverseas = PPOBGet(ppobAddressModelMax.copy(postCode = None), None, None)
  val ppobModelMax = PPOBGet(ppobAddressModelMax, Some(contactDetailsModelMax), Some(website))

  val ppobModelMaxPost = PPOBPost(ppobAddressModelMaxPost, Some(contactDetailsModelMax), Some(website), None)
  val ppobModelMaxPostAgent = PPOBPost(ppobAddressModelMaxPost, Some(contactDetailsModelMax), Some(website), Some(agentEmail))

  val ppobModelEmailMaxPost = EmailPost(ppobAddressModelMaxPost, contactDetailsModelMax, Some(website))

  val ppobAddressGetJson: JsValue = Json.obj("line1" -> "Ronaldini Road", "line3" -> "Pell Way", "postCode" -> "R10 AAA", "countryCode" -> "BRAZIL")
  val ppobAddressGetValue: PPOBAddressGet = PPOBAddressGet(Some("Ronaldini Road"), None, Some("Pell Way"), None, None, Some("R10 AAA"), Some("BRAZIL"))

  val emailPostJson: JsValue =  Json.parse(s"""{
                                              |	"address": {
                                              |		"line1": "$addLine1",
                                              |		"line2": "$addLine2",
                                              |		"line3": "$addLine3",
                                              |		"line4": "$addLine4",
                                              |		"line5": "$addLine5",
                                              |		"postCode": "$postcode",
                                              |		"countryCode": "$countryCode"
                                              |	},
                                              |	"contactDetails": {
                                              |		"primaryPhoneNumber": "$phoneNumber",
                                              |		"mobileNumber": "$mobileNumber",
                                              |		"faxNumber": "$faxNumber",
                                              |		"emailAddress": "$email",
                                              |		"emailVerified": $emailVerified
                                              |	},
                                              | "websiteAddress": "$website"
                                              |
                                             |}""".stripMargin)

  val emailPostValue: EmailPost = EmailPost(ppobAddressModelMaxPost,
    ContactDetails(Some(phoneNumber), Some(mobileNumber), Some(faxNumber), Some(email), Some(emailVerified)), Some(website))

  val emailPostWriteResult: JsValue = Json.parse(s"""{
                                             |	"PPOBAddress": {
                                             |		"line1": "$addLine1",
                                             |		"line2": "$addLine2",
                                             |		"line3": "$addLine3",
                                             |		"line4": "$addLine4",
                                             |		"line5": "$addLine5",
                                             |		"postCode": "$postcode",
                                             |		"countryCode": "$countryCode",
                                             |		"addressValidated": true
                                             |	},
                                             |	"PPOBCommDetails": {
                                             |		"primaryPhoneNumber": "$phoneNumber",
                                             |		"mobileNumber": "$mobileNumber",
                                             |		"faxNumber": "$faxNumber",
                                             |		"emailAddress": "$email",
                                             |		"emailVerified": $emailVerified
                                             |	},
                                             | "websiteAddress": "$website"
                                             |
                                             |}""".stripMargin)


  val ppobAddressPostJson: JsValue = Json.obj(
    "line1" -> "Ronaldini Road",
    "postCode" -> "R10 AAA",
    "countryCode" -> countryCode
  )
  val ppobAddressPostValue: PPOBAddressPost = PPOBAddressPost(
    Some("Ronaldini Road"),
    None, None, None, None,
    Some("R10 AAA"),
    Some(countryCode)
  )
  val ppobAddressPostWritesResult: JsValue = Json.obj(
    "line1" -> "Ronaldini Road",
    "postCode" -> "R10 AAA",
    "countryCode" -> countryCode,
    "addressValidated" -> true
  )

  val ppobPostExample = PPOBPost(
    ppobAddressPostValue,
    Some(contactDetailsModelMax),
    Some(website),
    None
  )

  val ppobPostExampleJson: JsValue = Json.obj(
    "address" -> ppobAddressPostJson,
    "contactDetails" -> Json.toJson(contactDetailsModelMax),
    "websiteAddress" -> website
  )
}
