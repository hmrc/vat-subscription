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

package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

case class BankDetails(accountHolderName: Option[String],
                       bankAccountNumber: Option[String],
                       sortCode: Option[String])

object BankDetails {


  private val accountHolderNamePath = JsPath \ "accountHolderName"
  private val bankAccountNumberPath = JsPath \ "bankAccountNumber"
  private val sortCodePath = JsPath \ "sortCode"

  implicit val bankReader: Reads[BankDetails] = for {
    accountHolderName <- accountHolderNamePath.readNullable[String]
    bankAccountNumber <- bankAccountNumberPath.readNullable[String]
    sortCode <- sortCodePath.readNullable[String]
  } yield BankDetails(accountHolderName, bankAccountNumber, sortCode)

  implicit val bankWriter: Writes[BankDetails] = (
    accountHolderNamePath.writeNullable[String] and
      bankAccountNumberPath.writeNullable[String] and
      sortCodePath.writeNullable[String]
    )(unlift(BankDetails.unapply))

}
