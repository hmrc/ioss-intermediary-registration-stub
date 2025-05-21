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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.{DesAddress, IndividualName, VatCustomerInfo}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.{Clock, LocalDate}
import javax.inject.Inject

class VatInfoController @Inject()(cc: ControllerComponents, clock: Clock) extends BackendController(cc) {

  private val successfulFullResponse = {
    VatCustomerInfo(
      registrationDate = Some(LocalDate.of(2020, 1, 1)),
      desAddress = DesAddress("1 The Street", Some("Some Town"), None, None, None, Some("BT11 1AA"), "GB"),
      partyType = None,
      organisationName = Some("Company Name"),
      individual = None,
      singleMarketIndicator = true,
      deregistrationDecisionDate = None
    )
  }

  private val successfulFullResponseNonNi = {
    VatCustomerInfo(
      registrationDate = Some(LocalDate.of(2020, 1, 1)),
      desAddress = DesAddress("1 The Street", Some("Some Town"), None, None, None, Some("AA11 1AA"), "GB"),
      partyType = None,
      organisationName = Some("Company Name"),
      individual = None,
      singleMarketIndicator = true,
      deregistrationDecisionDate = None
    )
  }

  private val expiredVrnResponse = {
    VatCustomerInfo(
      registrationDate = Some(LocalDate.of(2020, 1, 1)),
      desAddress = DesAddress("1 The Street", Some("Some Town"), None, None, None, Some("BT11 1AA"), "GB"),
      partyType = None,
      organisationName = Some("Company Name"),
      individual = None,
      singleMarketIndicator = true,
      deregistrationDecisionDate = Some(LocalDate.now(clock))
    )
  }

  private val successfulFullIndividualResponse = {
    VatCustomerInfo(
      registrationDate = Some(LocalDate.of(2020, 1, 1)),
      desAddress = DesAddress("1 The Street", Some("Some Town"), None, None, None, Some("BT11 1AA"), "GB"),
      partyType = None,
      organisationName = None,
      individual = Some(IndividualName(Some("first"), Some("middle"), Some("last"))),
      singleMarketIndicator = true,
      deregistrationDecisionDate = None
    )
  }

  private val successfulSparseResponse = {
    VatCustomerInfo(
      registrationDate = None,
      desAddress = DesAddress("1 The Street", Some("Some Town"), None, None, None, Some("BT11 1AA"), "GB"),
      partyType = None,
      organisationName = Some("Company Name"),
      individual = None,
      singleMarketIndicator = true,
      deregistrationDecisionDate = None
    )
  }

  def getInformation(vrn: String): Action[AnyContent] = Action {
    vrn match {
      case "900000001" => NotFound
      case "800000001" => InternalServerError
      case "700000001" => Ok(Json.toJson(successfulSparseResponse))
      case "700000002" => Ok(Json.toJson(successfulFullIndividualResponse))
      case "700000003" => Ok(Json.toJson(successfulFullResponseNonNi))
      case "700000004" => Ok(Json.toJson(expiredVrnResponse))
      case _ => Ok(Json.toJson(successfulFullResponse))
    }
  }
}

