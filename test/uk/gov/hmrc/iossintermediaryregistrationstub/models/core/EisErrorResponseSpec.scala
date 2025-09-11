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

package uk.gov.hmrc.iossintermediaryregistrationstub.models.core

import org.scalatest.matchers.must.Matchers
import play.api.libs.json.*
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase

import java.time.Instant

class EisErrorResponseSpec extends SpecBase with Matchers {

  "EisErrorResponse" - {

    "serialize and deserialize correctly" in {

      val errorResponse = EisErrorResponse(
        timestamp = Instant.parse("2024-12-16T09:56:54.941361Z"),
        error = "Some error",
        errorMessage = "This is an error message"
      )

      val json = Json.toJson(errorResponse)

      val expectedJson = Json.obj(
        "timestamp" -> "2024-12-16T09:56:54.941361Z",
        "error" -> "Some error",
        "errorMessage" -> "This is an error message"
      )

      json mustBe expectedJson

      json.as[EisErrorResponse] mustBe errorResponse
    }

    "fail deserialization with invalid field types" in {
      val invalidJson = Json.obj(
        "timestamp" -> "2024-12-16T09:56:54.941361Z",
        "error" -> 123,
        "errorMessage" -> "This is an error message"
      )

      invalidJson.validate[EisErrorResponse].isError mustBe true
    }
  }

  "EisDisplayErrorResponse" - {

    "serialize and deserialize correctly" in {

      val errorDetail = EisDisplayErrorDetail(
        correlationId = "abc123",
        errorCode = "089",
        errorMessage = "Display error message",
        timestamp = "2024-12-16T09:56:54"
      )

      val displayErrorResponse = EisDisplayErrorResponse(errorDetail)

      val json = Json.toJson(displayErrorResponse)

      val expectedJson = Json.obj(
        "errorDetail" -> Json.obj(
          "correlationId" -> "abc123",
          "errorCode" -> "089",
          "errorMessage" -> "Display error message",
          "timestamp" -> "2024-12-16T09:56:54"
        )
      )

      json mustBe expectedJson

      json.as[EisDisplayErrorResponse] mustBe displayErrorResponse
    }

    "fail deserialization with invalid field types" in {
      val invalidJson = Json.obj(
        "errorDetail" -> Json.obj(
          "correlationId" -> "abc123",
          "errorCode" -> 123,
          "errorMessage" -> "Display error message",
          "timestamp" -> "2024-12-16T09:56:54"
        )
      )

      invalidJson.validate[EisDisplayErrorResponse].isError mustBe true
    }
  }

  "EisDisplayErrorDetail" - {

    "serialize and deserialize correctly" in {

      val errorDetail = EisDisplayErrorDetail(
        correlationId = "abc123",
        errorCode = "089",
        errorMessage = "Display error message",
        timestamp = "2024-12-16T09:56:54"
      )

      val json = Json.toJson(errorDetail)

      val expectedJson = Json.obj(
        "correlationId" -> "abc123",
        "errorCode" -> "089",
        "errorMessage" -> "Display error message",
        "timestamp" -> "2024-12-16T09:56:54"
      )

      json mustBe expectedJson

      json.as[EisDisplayErrorDetail] mustBe errorDetail
    }

    "fail deserialization with invalid field types" in {
      val invalidJson = Json.obj(
        "correlationId" -> "abc123",
        "errorCode" -> 123,
        "errorMessage" -> "Display error message",
        "timestamp" -> "2024-12-16T09:56:54"
      )

      invalidJson.validate[EisDisplayErrorDetail].isError mustBe true
    }
  }
}
