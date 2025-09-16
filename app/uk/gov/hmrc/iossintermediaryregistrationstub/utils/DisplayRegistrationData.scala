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

package uk.gov.hmrc.iossintermediaryregistrationstub.utils

import uk.gov.hmrc.iossintermediaryregistrationstub.format.Format.dateFormatter
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.*
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.EtmpIdType.VRN
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.display.{EtmpDisplayEuRegistrationDetails, EtmpDisplayRegistration, EtmpDisplaySchemeDetails}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.{Bic, Iban}

import java.time.{Clock, LocalDate, LocalDateTime}

object DisplayRegistrationData {

  def fullSuccessfulDisplayRegistrationResponse(clock: Clock, commencementDate: LocalDate): EtmpDisplayRegistration = {
    EtmpDisplayRegistration(
      customerIdentification = EtmpCustomerIdentification(
        idType = VRN,
        idValue = "100000001"
      ),
      tradingNames = Seq(EtmpTradingName("tradingName1"), EtmpTradingName("tradingName2")),
      clientDetails = Seq(
        EtmpClientDetails(
          clientName = "clientName1",
          clientIossID = "100000001",
          clientExcluded = false
        ),
        EtmpClientDetails(
          clientName = "clientName2",
          clientIossID = "100000003",
          clientExcluded = true
        ),
      ),
      intermediaryDetails = Some(
        EtmpIntermediaryDetails(
          otherIossIntermediaryRegistrations = Seq(
            EtmpOtherIossIntermediaryRegistrations(
              issuedBy = "DE",
              intermediaryNumber = "IN2761234567"
            )
          )
        )
      ),
      otherAddress = Some(
        EtmpOtherAddress(
          issuedBy = "ES",
          tradingName = Some("Other Trading Name 1"),
          addressLine1 = "Other Address Line 1",
          addressLine2 = Some("Other Address Line 2"),
          townOrCity = "Other Town or City",
          regionOrState = Some("Other Region or State"),
          postcode = "NE11HM"
        )
      ),
      schemeDetails = EtmpDisplaySchemeDetails(
        commencementDate = commencementDate.format(dateFormatter),
        euRegistrationDetails = Seq(
          EtmpDisplayEuRegistrationDetails(
            issuedBy = "DE",
            vatNumber = Some("123456789"),
            taxIdentificationNumber = None,
            fixedEstablishmentTradingName = "Some Trading Name",
            fixedEstablishmentAddressLine1 = "Line 1",
            fixedEstablishmentAddressLine2 = Some("Line 2"),
            townOrCity = "Town",
            regionOrState = Some("Region"),
            postcode = Some("AB12 3CD")
          ),
          EtmpDisplayEuRegistrationDetails(
            issuedBy = "FR",
            vatNumber = Some("XX123456789"),
            taxIdentificationNumber = None,
            fixedEstablishmentTradingName = "Some Trading Name",
            fixedEstablishmentAddressLine1 = "Line 1",
            fixedEstablishmentAddressLine2 = Some("Line 2"),
            townOrCity = "Town",
            regionOrState = Some("Region"),
            postcode = Some("AB12 3CD")
          )
        ),
        contactName = "Test name",
        businessTelephoneNumber = "1234567890",
        businessEmailId = "email@test.com",
        unusableStatus = false,
        nonCompliantReturns = None,
        nonCompliantPayments = None
      ),
      exclusions = Seq.empty,
      bankDetails = EtmpBankDetails(
        accountName = "Account name",
        bic = Some(Bic("ABCDGB2A").get),
        iban = Iban("GB33BUKB20201555555555").toOption.get
      ),
      adminUse = EtmpAdminUse(Some(LocalDateTime.now(clock)))
    )
  }

  def minimalSuccessfulDisplayRegistrationResponse(clock: Clock, commencementDate: LocalDate): EtmpDisplayRegistration = {
    EtmpDisplayRegistration(
      customerIdentification = EtmpCustomerIdentification(
        idType = VRN,
        idValue = "100000001"
      ),
      tradingNames = Seq.empty,
      clientDetails = Seq.empty,
      intermediaryDetails = Some(
        EtmpIntermediaryDetails(
          otherIossIntermediaryRegistrations = Seq.empty
        )
      ),
      otherAddress = Some(
        EtmpOtherAddress(
          issuedBy = "ES",
          tradingName = Some("Other Trading Name 1"),
          addressLine1 = "Other Address Line 1",
          addressLine2 = Some("Other Address Line 2"),
          townOrCity = "Other Town or City",
          regionOrState = Some("Other Region or State"),
          postcode = "NE11HM"
        )
      ),
      schemeDetails = EtmpDisplaySchemeDetails(
        commencementDate = commencementDate.format(dateFormatter),
        euRegistrationDetails = Seq.empty,
        contactName = "Rocky Balboa",
        businessTelephoneNumber = "028 123 4567",
        businessEmailId = "rocky.balboa@chartoffwinkler.co.uk",
        unusableStatus = false,
        nonCompliantReturns = None,
        nonCompliantPayments = None
      ),
      exclusions = Seq.empty,
      bankDetails = EtmpBankDetails(
        accountName = "Chartoff Winkler and Co.",
        bic = Some(Bic("BARCGB22456").get),
        iban = Iban("GB33BUKB202015555555555").toOption.get
      ),
      adminUse = EtmpAdminUse(Some(LocalDateTime.now(clock)))
    )
  }
}
