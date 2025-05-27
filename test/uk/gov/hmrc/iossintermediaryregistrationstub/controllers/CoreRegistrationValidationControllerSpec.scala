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

package uk.gov.hmrc.iossintermediaryregistrationstub.controllers

import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Headers
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase
import uk.gov.hmrc.iossintermediaryregistrationstub.models.core.*
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.Headers.{invalidHeaders, missingHeaders, validHeaders}

import java.time.Clock

class CoreRegistrationValidationControllerSpec extends SpecBase {

  val validFakeHeaders = new Headers(validHeaders)
  val missingFakeHeaders = new Headers(missingHeaders)
  val invalidFakeHeaders = new Headers(invalidHeaders)

  private val genericMatch = Match(
    MatchType.TraderIdQuarantinedNETP,
    "IN333333331",
    "EE",
    None,
    Some("2022-12-11"),
    Some("2023-01-01"),
    None,
    None
  )

  private val coreValidationResponses: CoreRegistrationValidationResult =
    CoreRegistrationValidationResult(
      "IN333333331",
      None,
      "EE",
      traderFound = true,
      Seq(
        genericMatch
      ))

  ".coreValidateRegistration" - {

    "must return 200 and a response with correct response body when submitted" - {

      "with source = VATNumber for MatchType = TraderIdQuarantinedNETP (002)" in {

        val app = new GuiceApplicationBuilder()
          .overrides(inject.bind[Clock].toInstance(stubClock))
          .build()

        running(app) {
          val request = FakeRequest(POST, routes.CoreRegistrationValidationController.coreValidateRegistration().url)
            .withJsonBody(Json.toJson(coreRegistrationRequest.copy(
              source = SourceType.VATNumber.toString, searchId = "IN333333333")))
            .withHeaders(validFakeHeaders)

          val result = route(app, request).value
          
          status(result) mustEqual 200

          contentAsJson(result) mustEqual Json.toJson(coreValidationResponses.copy(
            searchId = "IN333333333", matches = Seq(
              genericMatch.copy(matchType = MatchType.OtherMSNETPActiveNETP,
                traderId = SourceType.VATNumber.toString
              ))))
        }
      }
    }

    "must return traderFound = false and empty matches when requested with invalid search id" in {
      val app = new GuiceApplicationBuilder()
        .overrides(inject.bind[Clock].toInstance(stubClock))
        .build()
      val coreRegistrationRequest = CoreRegistrationRequest(
        "VATNumber",
        Some("IOSS"),
        "233333331",
        Some("IN4747493822"),
        "PR"
      )
      running(app) {
        val request = FakeRequest(POST, routes.CoreRegistrationValidationController.coreValidateRegistration().url)
          .withJsonBody(Json.toJson(coreRegistrationRequest))
          .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual 200

        contentAsJson(result) mustEqual Json.toJson(coreValidationResponses.copy(traderFound = false, searchId = "233333331", matches = Seq.empty))
      }
    }

    "must return Bad Request when a header is invalid" in {

      val app = new GuiceApplicationBuilder()
        .overrides(inject.bind[Clock].toInstance(stubClock))
        .build()

      running(app) {
        val request = FakeRequest(POST, routes.CoreRegistrationValidationController.coreValidateRegistration().url)
          .withJsonBody(Json.toJson(coreRegistrationRequest))
          .withHeaders(invalidFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return Bad Request when a header is missing" in {

      val app = new GuiceApplicationBuilder()
        .overrides(inject.bind[Clock].toInstance(stubClock))
        .build()

      running(app) {
        val request = FakeRequest(POST, routes.CoreRegistrationValidationController.coreValidateRegistration().url)
          .withJsonBody(Json.toJson(coreRegistrationRequest))
          .withHeaders(missingFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return Bad Request when all headers are missing" in {

      val app = new GuiceApplicationBuilder()
        .overrides(inject.bind[Clock].toInstance(stubClock))
        .build()

      running(app) {
        val request = FakeRequest(POST, routes.CoreRegistrationValidationController.coreValidateRegistration().url)
          .withJsonBody(Json.toJson(coreRegistrationRequest))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }
  }
}
