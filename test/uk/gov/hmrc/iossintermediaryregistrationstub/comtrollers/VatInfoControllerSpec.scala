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

package uk.gov.hmrc.iossintermediaryregistrationstub.comtrollers

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsSuccess
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.iossintermediaryregistrationstub.base.SpecBase
import uk.gov.hmrc.iossintermediaryregistrationstub.controllers.routes
import uk.gov.hmrc.iossintermediaryregistrationstub.models.{DesAddress, VatCustomerInfo}

import java.time.LocalDate

class VatInfoControllerSpec extends SpecBase {

  "getInformation" - {

    "must return Ok and a payload when asked for info with a VRN" in {

      val app = new GuiceApplicationBuilder().build()

      running(app) {
        val request = FakeRequest(GET, routes.VatInfoController.getInformation(vrn).url)

        val result = route(app, request).value

        val successfulResponse =
          VatCustomerInfo(
            registrationDate = Some(LocalDate.of(2020, 1, 1)),
            desAddress = DesAddress("1 The Street", Some("Some Town"), None, None, None, Some("AA11 1AA"), "GB"),
            partyType = None,
            organisationName = Some("Company Name"),
            individual = None,
            singleMarketIndicator = true,
            deregistrationDecisionDate = None
          )

        status(result) `mustBe` OK
        contentAsJson(result).validate[VatCustomerInfo] `mustBe` JsSuccess(successfulResponse)
      }
    }
  }
}
