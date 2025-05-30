/*
 * Copyright 2023 HM Revenue & Customs
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

  val etmpRegistrationSchemaPath: String = EtmpRegistrationSchemaData().schemaPath
  val etmpRegistrationGivenOptionalExample: String = EtmpRegistrationSchemaData().givenOptionalExample
  val etmpRegistrationGivenFullExample: String = EtmpRegistrationSchemaData().givenFullExample

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

  "EtmpRegistration SchemaHelper.applySchemaValidation" - {

    "must return NoJsBodyProvided if no jsBody parameter specified" in {
      helper.applySchemaValidation(etmpRegistrationSchemaPath, None) mustBe NoJsBodyProvided
    }

    "must return FailedToFindSchema if there is no schema found in the given path" in {
      helper.applySchemaValidation("path", None) mustBe FailedToFindSchema
    }

    "must return SuccessSchema for a optional given example" in {
      helper.applySchemaValidation(etmpRegistrationSchemaPath, Some(Json.parse(etmpRegistrationGivenOptionalExample))) mustBe SuccessSchema
    }

    "must return SuccessSchema for a full given example" in {
      helper.applySchemaValidation(etmpRegistrationSchemaPath, Some(Json.parse(etmpRegistrationGivenFullExample))) mustBe SuccessSchema
    }
  }
}
