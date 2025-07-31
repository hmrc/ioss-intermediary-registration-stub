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
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.*
import uk.gov.hmrc.iossintermediaryregistrationstub.models.{Bic, Iban}

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

  val registrationRequest: EtmpRegistrationRequest = EtmpRegistrationRequest(
    administration = EtmpAdministration(EtmpMessageType.IOSSIntCreate),
    customerIdentification = EtmpCustomerIdentification(EtmpIdType.VRN, vrn),
    tradingNames = Seq(
      EtmpTradingName(tradingName = "Some Trading Name"),
      EtmpTradingName(tradingName = "Some Other Trading Name")
    ),
    intermediaryDetails = Some(EtmpIntermediaryDetails(Seq(EtmpOtherIossIntermediaryRegistrations("FR", "IN1021234567")))),
    otherAddress = None,
    schemeDetails = EtmpSchemeDetails(
      commencementDate = LocalDate.now(stubClock),
      euRegistrationDetails = Seq(
        EtmpEuRegistrationDetails(
          countryOfRegistration = "DE",
          traderId = VatNumberTraderId(vatNumber = "DE123456789"),
          tradingName = "Some Trading Name",
          fixedEstablishmentAddressLine1 = "Line 1",
          fixedEstablishmentAddressLine2 = Some("Line 2"),
          townOrCity = "Town",
          regionOrState = Some("Region"),
          postcode = Some("AB12 3CD")
        )
      ),
      previousEURegistrationDetails = Seq(
        EtmpPreviousEuRegistrationDetails(
          issuedBy = "DE",
          registrationNumber = "DE123",
          schemeType = SchemeType.IOSSWithIntermediary,
          intermediaryNumber = Some("IM123456789")
        )
      ),
      websites = None,
      contactName = "Mr Test",
      businessTelephoneNumber = "0123 456789",
      businessEmailId = "mrtest@example.co.uk",
      nonCompliantReturns = Some("1"),
      nonCompliantPayments = Some("2")
    ),
    bankDetails = EtmpBankDetails(
      accountName = "Mr Test",
      bic = Some(Bic("ABCDEF2A").get),
      iban = Iban("GB33BUKB20201555555555").toOption.get
    )
  )
}
