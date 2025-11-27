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

package uk.gov.hmrc.iossintermediaryregistrationstub.controllers

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsSuccess, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase
import uk.gov.hmrc.iossintermediaryregistrationstub.models.{DesAddress, VatCustomerInfo}

import java.time.LocalDate

class ChannelPreferenceStubControllerSpec extends SpecBase {

  "updatePreferences" - {

    "must return Ok and a payload" in {

      val app = new GuiceApplicationBuilder().build()
      val jsonBody = Json.obj(
        "identifierType" -> "INT",
        "identifier" -> "IN9001234567",
        "emailAddress" -> "test@example.com",
        "unusableStatus" -> true
      )

      running(app) {
        val request = FakeRequest(PUT, routes.ChannelPreferenceStubController.updatePreferences().url)
          .withJsonBody(jsonBody)

        val result = route(app, request).value

        status(result) `mustBe` OK
        contentAsJson(result) mustBe Json.obj("message" -> "Preferences updated successfully")
      }
    }
  }
}
