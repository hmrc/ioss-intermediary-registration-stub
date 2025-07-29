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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.HeaderNames.{AUTHORIZATION, CONTENT_TYPE}
import play.api.http.MimeTypes
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT, UNAUTHORIZED}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Headers
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, route, running, status, PUT}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase
import uk.gov.hmrc.iossintermediaryregistrationstub.connectors.RegistrationConnector
import uk.gov.hmrc.iossintermediaryregistrationstub.models.enrolments.SubscriberRequest

import scala.concurrent.Future

class EnrolmentsControllerSpec extends SpecBase {

  private val subscriptionId = "1234567-id"
  private val validHeaders: Seq[(String, String)] = Seq(
    (AUTHORIZATION, ""), (CONTENT_TYPE, MimeTypes.JSON))

  private val invalidHeaders: Seq[(String, String)] = Seq(
     (CONTENT_TYPE, MimeTypes.JSON))

  val validFakeHeaders = new Headers(validHeaders)
  val invalidFakeHeaders = new Headers(invalidHeaders)

  private val callbackUrl = "http://localhost:8100/one-stop-shop-registration/authorised/accept/confirm/100000001-id"

  ".confirm" - {

    "must return NoContent" in {

      val registrationConnector = mock[RegistrationConnector]
      when(registrationConnector.doCallback(any(), any()) (any())) thenReturn Future.successful(HttpResponse(204, ""))
      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val request = FakeRequest(PUT, routes.EnrolmentsController.confirm(subscriptionId).url)
          .withBody(Json.stringify(Json.toJson(SubscriberRequest("HMRC-IOSS-ORG", callbackUrl, "123"))))
          .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual NO_CONTENT
      }
    }

    "must return BadRequest when headers missing" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val request = FakeRequest(PUT, routes.EnrolmentsController.confirm(subscriptionId).url)
          .withBody(Json.stringify(Json.toJson(SubscriberRequest("HMRC-IOSS-ORG", "/test", "123"))))
          .withHeaders(invalidFakeHeaders)
        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }
  }
}
