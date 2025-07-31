/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.iossintermediaryregistrationstub.utils

object EtmpCreateRegistrationSchemaData {

  val schemaPath: String = "/resources/schemas/etmp-registration-schema.json"

  val givenExample: String =
    """{
      |  "administration": {
      |    "messageType": "IOSSIntCreate",
      |    "regimeID": "IOSS"
      |  },
      |  "customerIdentification": {
      |    "idType": "VRN",
      |    "idValue": "100000001"
      |  },
      |  "tradingNames": [],
      |  "schemeDetails": {
      |    "commencementDate": "2025-07-29",
      |    "euRegistrationDetails": [],
      |    "previousEURegistrationDetails": [],
      |    "contactName": "a",
      |    "businessTelephoneNumber": "123",
      |    "businessEmailId": "a@a.com"
      |  },
      |  "bankDetails": {
      |    "accountName": "test",
      |    "iban": "GB33BUKB20201555555555"
      |  }
      |}""".stripMargin

}
