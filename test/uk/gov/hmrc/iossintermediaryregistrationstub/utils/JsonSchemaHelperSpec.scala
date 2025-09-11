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

import play.api.libs.json.Json
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase

class JsonSchemaHelperSpec extends SpecBase {

  val coreSchemaPath: String = CoreRegistrationSchemaData().schemaPath
  val coreGivenExample: String = CoreRegistrationSchemaData().givenExample
  val example2: String = """{
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
                           |    "websites": [],
                           |    "contactName": "a",
                           |    "businessTelephoneNumber": "123",
                           |    "businessEmailId": "a@a.com"
                           |  },
                           |  "bankDetails": {
                           |    "accountName": "test",
                           |    "iban": "GB33BUKB20201555555555"
                           |  }
                           |}""".stripMargin
  
  val helper = new JsonSchemaHelper()


  "CoreRegistration SchemaHelper.applySchemaValidation" - {

    "must return NoJsBodyProvided if no jsBody parameter specified" in {
      helper.applySchemaValidation(coreSchemaPath, None) mustBe NoJsBodyProvided
    }

    "must return FailedToFindSchema if there is no schema found in the given path" in {
      helper.applySchemaValidation("path", None) mustBe FailedToFindSchema
    }

    "must return SuccessSchema for given example" in {
      helper.applySchemaValidation(coreSchemaPath, Some(Json.parse(coreGivenExample))) mustBe SuccessSchema
    }
  }

  "EtmpCreateRegistration SchemaHelper.applySchemaValidation" - {

    val etmpSchemaPath: String = EtmpCreateRegistrationSchemaData.schemaPath
    val etmpGivenExample: String = EtmpCreateRegistrationSchemaData.givenExample

    "must return NoJsBodyProvided if no jsBody parameter specified" in {
      helper.applySchemaValidation(etmpSchemaPath, None) mustBe NoJsBodyProvided
    }

    "must return FailedToFindSchema if there is no schema found in the given path" in {
      helper.applySchemaValidation("path", None) mustBe FailedToFindSchema
    }

    "must return SuccessSchema for given example" in {
      helper.applySchemaValidation(etmpSchemaPath, Some(Json.parse(etmpGivenExample))) mustBe SuccessSchema
    }
  }
}
