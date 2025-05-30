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

package uk.gov.hmrc.iossintermediaryregistrationstub.models.core

import org.scalatest.matchers.must.Matchers
import play.api.libs.json.*
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase

class CoreRegistrationValidationResultSpec extends SpecBase with Matchers {

  "CoreRegistrationValidationResult" - {

    "serialize and deserialize correctly" in {

      val coreRegistrationValidationResult = CoreRegistrationValidationResult(
        searchId = "search123",
        searchIntermediary = Some("intermediaryXYZ"),
        searchIdIssuedBy = "issuedByABC",
        traderFound = true,
        matches = Seq(
          Match(
            matchType = MatchType.TraderIdActiveNETP,
            traderId = "12345",
            memberState = "GB",
            exclusionStatusCode = Some(1),
            exclusionDecisionDate = Some("2024-12-16"),
            exclusionEffectiveDate = Some("2024-12-17"),
            nonCompliantReturns = Some(2),
            nonCompliantPayments = Some(3)
          )
        )
      )

      val json = Json.toJson(coreRegistrationValidationResult)

      val expectedJson = Json.obj(
        "searchId" -> "search123",
        "searchIntermediary" -> "intermediaryXYZ",
        "searchIdIssuedBy" -> "issuedByABC",
        "traderFound" -> true,
        "matches" -> Json.arr(
          Json.obj(
            "matchType" -> "001", // MatchType.TraderIdActiveNETP serializes to "001"
            "traderId" -> "12345",
            "memberState" -> "GB",
            "exclusionStatusCode" -> 1,
            "exclusionDecisionDate" -> "2024-12-16",
            "exclusionEffectiveDate" -> "2024-12-17",
            "nonCompliantReturns" -> 2,
            "nonCompliantPayments" -> 3
          )
        )
      )

      json mustBe expectedJson

      json.as[CoreRegistrationValidationResult] mustBe coreRegistrationValidationResult
    }

    "deserialize with missing optional fields" in {

      val matchInstance = Match(
        matchType = MatchType.TraderIdActiveNETP,
        traderId = "12345",
        memberState = "GB",
        exclusionStatusCode = None,
        exclusionDecisionDate = None,
        exclusionEffectiveDate = None,
        nonCompliantReturns = None,
        nonCompliantPayments = None
      )

      val coreRegistrationValidationResult = CoreRegistrationValidationResult(
        searchId = "search123",
        searchIntermediary = Some("intermediaryXYZ"),
        searchIdIssuedBy = "issuedByABC",
        traderFound = true,
        matches = Seq(matchInstance)
      )

      val expectedJson = Json.obj(
        "searchId" -> "search123",
        "searchIntermediary" -> "intermediaryXYZ",
        "searchIdIssuedBy" -> "issuedByABC",
        "traderFound" -> true,
        "matches" -> Json.arr(
          Json.obj(
            "matchType" -> "001",
            "traderId" -> "12345",
            "memberState" -> "GB"
          )
        )
      )

      Json.toJson(coreRegistrationValidationResult) mustBe expectedJson

      expectedJson.as[CoreRegistrationValidationResult] mustBe coreRegistrationValidationResult
    }

    "fail deserialization with incorrect field types" in {
      val invalidJson = Json.obj(
        "matchType" -> "001",
        "traderId" -> "12345",
        "memberState" -> "GB",
        "exclusionStatusCode" -> "invalid",
        "exclusionDecisionDate" -> "2024-12-16",
        "exclusionEffectiveDate" -> "2024-12-17",
        "nonCompliantReturns" -> 2,
        "nonCompliantPayments" -> 3
      )

      invalidJson.validate[Match].isError mustBe true
    }

    "fail deserialization with unknown match type" in {
      val invalidJson = Json.obj(
        "matchType" -> "999",
        "traderId" -> "12345",
        "memberState" -> "GB",
        "exclusionStatusCode" -> JsNull,
        "exclusionDecisionDate" -> JsNull,
        "exclusionEffectiveDate" -> JsNull,
        "nonCompliantReturns" -> JsNull,
        "nonCompliantPayments" -> JsNull
      )

      invalidJson.validate[Match] mustBe a[JsError]
    }
  }

  "Match" - {

    "serialize and deserialize correctly" in {

      val matchInstance = Match(
        matchType = MatchType.TraderIdActiveNETP,
        traderId = "12345",
        memberState = "GB",
        exclusionStatusCode = Some(1),
        exclusionDecisionDate = Some("2024-12-16"),
        exclusionEffectiveDate = Some("2024-12-17"),
        nonCompliantReturns = Some(2),
        nonCompliantPayments = Some(3)
      )

      val json = Json.toJson(matchInstance)

      val expectedJson = Json.obj(
        "matchType" -> "001",
        "traderId" -> "12345",
        "memberState" -> "GB",
        "exclusionStatusCode" -> 1,
        "exclusionDecisionDate" -> "2024-12-16",
        "exclusionEffectiveDate" -> "2024-12-17",
        "nonCompliantReturns" -> 2,
        "nonCompliantPayments" -> 3
      )

      json mustBe expectedJson

      json.as[Match] mustBe matchInstance
    }

    "deserialize with missing optional fields" in {

      val matchInstance = Match(
        matchType = MatchType.TraderIdActiveNETP,
        traderId = "12345",
        memberState = "GB",
        exclusionStatusCode = None,
        exclusionDecisionDate = None,
        exclusionEffectiveDate = None,
        nonCompliantReturns = None,
        nonCompliantPayments = None
      )

      val expectedJson = Json.obj(
        "matchType" -> "001",
        "traderId" -> "12345",
        "memberState" -> "GB"
      )

      Json.toJson(matchInstance) mustBe expectedJson

      expectedJson.as[Match] mustBe matchInstance
    }

    "fail deserialization with incorrect field types" in {
      val invalidJson = Json.obj(
        "matchType" -> "001",
        "traderId" -> "12345",
        "memberState" -> "GB",
        "exclusionStatusCode" -> "invalid",
        "exclusionDecisionDate" -> "2024-12-16",
        "exclusionEffectiveDate" -> "2024-12-17",
        "nonCompliantReturns" -> 2,
        "nonCompliantPayments" -> 3
      )

      invalidJson.validate[Match].isError mustBe true
    }
  }
}
