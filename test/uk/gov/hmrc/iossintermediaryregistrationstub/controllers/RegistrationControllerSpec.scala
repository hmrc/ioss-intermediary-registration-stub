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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.Headers
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase
import uk.gov.hmrc.iossintermediaryregistrationstub.connectors.RegistrationConnector
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.*
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.display.EtmpDisplayRegistration
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.amend.EtmpAmendCustomerIdentification
import uk.gov.hmrc.iossintermediaryregistrationstub.models.response.{EisErrorResponse, EtmpAmendRegistrationResponse, EtmpEnrolmentErrorResponse, EtmpEnrolmentResponse}
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.DisplayRegistrationData.fullSuccessfulDisplayRegistrationResponse
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.Headers.{invalidHeaders, missingHeaders, validHeaders}
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.RandomService

import java.time.{Clock, LocalDate, LocalDateTime}

class RegistrationControllerSpec extends SpecBase {

  private val validFakeHeaders = new Headers(validHeaders)
  private val missingFakeHeaders = new Headers(missingHeaders)
  private val invalidFakeHeaders = new Headers(invalidHeaders)

  private val mockRegistrationConnector: RegistrationConnector = mock[RegistrationConnector]
  private val mockRandomService: RandomService = mock[RandomService]

  when(mockRandomService.randomInt(any(), any())) thenReturn 1234567

  ".createRegistration" - {

    "must return CREATED and a response with the correct response body when submission is successful" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .overrides(bind[RandomService].toInstance(mockRandomService))
        .build()

      running(app) {

        val request = FakeRequest(POST, routes.RegistrationController.createRegistration().url)
          .withJsonBody(Json.toJson(registrationRequest))
          .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustBe CREATED
        contentAsJson(result) mustBe Json.toJson(EtmpEnrolmentResponse(
          processingDateTime = LocalDateTime.now(stubClock),
          formBundleNumber = Some(s"$vrn-id-1234567"),
          vrn = vrn,
          intRef = "IN9001234567",
          businessPartner = "A Business Partner"
        ))
      }
    }

    "must return UnprocessableEntity and an error response with error code 007 when vrn is 666000000" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      val vrn = "666000000"

      running(app) {

        val request = FakeRequest(POST, routes.RegistrationController.createRegistration().url)
          .withJsonBody(Json.toJson(registrationRequest.copy(customerIdentification = registrationRequest.customerIdentification.copy(idValue = vrn))))
          .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustBe UNPROCESSABLE_ENTITY
        contentAsJson(result) mustBe Json.toJson(EtmpEnrolmentErrorResponse(
          errorDetail = EisErrorResponse(
            timestamp = LocalDateTime.now(stubClock),
            errorCode = "007",
            errorMessage = "Business Partner already has an active OSS Subscription for this regime",
          )))
      }
    }

    "must return UnprocessableEntity and an error response with error code 123 when vrn is 666000001" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      val vrn = "666000001"

      running(app) {

        val request = FakeRequest(POST, routes.RegistrationController.createRegistration().url)
          .withJsonBody(Json.toJson(registrationRequest.copy(customerIdentification = registrationRequest.customerIdentification.copy(idValue = vrn))))
          .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustBe UNPROCESSABLE_ENTITY
        contentAsJson(result) mustBe Json.toJson(EtmpEnrolmentErrorResponse(
          errorDetail = EisErrorResponse(
            timestamp = LocalDateTime.now(stubClock),
            errorCode = "123",
            errorMessage = "error",
          )))
      }
    }

    "must return Conflict when submission is already registered" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      val vrn = "222222223"

      running(app) {

        val request = FakeRequest(POST, routes.RegistrationController.createRegistration().url)
          .withJsonBody(Json.toJson(registrationRequest.copy(customerIdentification = registrationRequest.customerIdentification.copy(idValue = vrn))))
          .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustBe CONFLICT
      }
    }

    "must return BadRequest when there's an error creating the enrolment" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      val vrn = "222222233"

      running(app) {

        val request = FakeRequest(POST, routes.RegistrationController.createRegistration().url)
          .withJsonBody(Json.toJson(registrationRequest.copy(customerIdentification = registrationRequest.customerIdentification.copy(idValue = vrn))))
          .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustBe BAD_REQUEST
      }
    }


    "must return Bad Request when a header is invalid" in {
      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(app) {
        val request =
          FakeRequest(POST, routes.RegistrationController.createRegistration().url)
            .withJsonBody(Json.toJson(registrationRequest))
            .withHeaders(invalidFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return Bad Request when a header is missing" in {
      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(app) {
        val request =
          FakeRequest(POST, routes.RegistrationController.createRegistration().url)
            .withJsonBody(Json.toJson(registrationRequest))
            .withHeaders(missingFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return Bad Request when all headers are missing" in {
      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(app) {
        val request =
          FakeRequest(POST, routes.RegistrationController.createRegistration().url)
            .withJsonBody(Json.toJson(registrationRequest))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }
  }

  ".getDisplayRegistration" - {

    val intermediaryNumber: String = "IN9012345678"
    val commencementDate: LocalDate = LocalDate.of(2025, 1, 1)

    "must return OK with a Display Registration payload when requested with a valid Intermediary number" in {

      val application = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.RegistrationController.getDisplayRegistration(intermediaryNumber).url)
          .withHeaders(validFakeHeaders)

        val result = route(application, request).value

        status(result) `mustBe` OK
        contentAsJson(result).validate[EtmpDisplayRegistration] `mustBe`
          JsSuccess(fullSuccessfulDisplayRegistrationResponse(stubClock, commencementDate))
      }
    }

    "must return NotFound when requested registration does not exist" in {

      val notFoundIntermediaryNumber: String = "IN9009999999"

      val application = new GuiceApplicationBuilder()
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.RegistrationController.getDisplayRegistration(notFoundIntermediaryNumber).url)
          .withHeaders(validFakeHeaders)

        val result = route(application, request).value

        status(result) `mustBe` UNPROCESSABLE_ENTITY
      }
    }

    "must return Bad Request when a header is invalid" in {

      val application = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(application) {
        val request =
          FakeRequest(GET, routes.RegistrationController.getDisplayRegistration(intermediaryNumber).url)
            .withHeaders(invalidFakeHeaders)

        val result = route(application, request).value

        status(result) `mustBe` BAD_REQUEST
      }
    }

    "must return Bad Request when a header is missing" in {

      val application = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(application) {
        val request =
          FakeRequest(GET, routes.RegistrationController.getDisplayRegistration(intermediaryNumber).url)
            .withHeaders(missingFakeHeaders)

        val result = route(application, request).value

        status(result) `mustBe` BAD_REQUEST
      }
    }

    "must return Bad Request when all headers are missing" in {

      val application = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(application) {
        val request =
          FakeRequest(GET, routes.RegistrationController.getDisplayRegistration(intermediaryNumber).url)

        val result = route(application, request).value

        status(result) `mustBe` BAD_REQUEST
      }
    }
  }
  ".amendRegistration" - {

    "must return OK and a response with correct response body when submission is successful" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .overrides(bind[RegistrationConnector].toInstance(mockRegistrationConnector))
        .overrides(bind[RandomService].toInstance(mockRandomService))
        .build()

      running(app) {
        val request =
          FakeRequest(PUT, routes.RegistrationController.amendRegistration.url)
            .withJsonBody(Json.toJson(amendRegistrationRequest))
            .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual OK
        contentAsJson(result) mustEqual Json.toJson(EtmpAmendRegistrationResponse(
          processingDateTime = LocalDateTime.now(stubClock),
          formBundleNumber = s"$iossNumber-id-1234567",
          vrn = iossNumber,
          intermediary = iossNumber,
          businessPartner = "A Business Partner"
        ))
      }

    }

    "must return NotFound when registration does not exist" in {

      val iossNumber = "IN9009999966"

      val updatedRegistrationRequest = amendRegistrationRequest.copy(customerIdentification = EtmpAmendCustomerIdentification(iossNumber))

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(app) {

        val request =
          FakeRequest(PUT, routes.RegistrationController.amendRegistration.url)
            .withJsonBody(Json.toJson(updatedRegistrationRequest))
            .withHeaders(validFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual NOT_FOUND
      }

    }

    "must return Bad Request when a header is invalid" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(app) {

        val request =
          FakeRequest(PUT, routes.RegistrationController.amendRegistration.url)
            .withJsonBody(Json.toJson(amendRegistrationRequest))
            .withHeaders(invalidFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return Bad Request when a header is missing" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(app) {

        val request =
          FakeRequest(PUT, routes.RegistrationController.amendRegistration.url)
            .withJsonBody(Json.toJson(amendRegistrationRequest))
            .withHeaders(missingFakeHeaders)

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }

    }

    "must return Bad Request when all headers are missing" in {

      val app = new GuiceApplicationBuilder()
        .overrides(bind[Clock].toInstance(stubClock))
        .build()

      running(app) {

        val request =
          FakeRequest(PUT, routes.RegistrationController.amendRegistration.url)
            .withJsonBody(Json.toJson(amendRegistrationRequest))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }
  }
}
