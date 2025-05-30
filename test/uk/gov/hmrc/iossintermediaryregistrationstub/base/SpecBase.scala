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

package uk.gov.hmrc.iossintermediaryregistrationstub.base

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.iossintermediaryregistrationstub.models.core.CoreRegistrationRequest

import java.time.{Clock, LocalDate, ZoneId}

class SpecBase extends AnyFreeSpec
  with Matchers
  with MockitoSugar
  with OptionValues {

  val stubClock: Clock = Clock.fixed(LocalDate.now.atStartOfDay(ZoneId.systemDefault).toInstant, ZoneId.systemDefault)

  val vrn = "100000001"

  val coreRegistrationRequest: CoreRegistrationRequest = CoreRegistrationRequest(
    "VATNumber",
    Some("IOSS"),
    "333333331",
    Some("IN4747493822"),
    "PR"
  )
}
