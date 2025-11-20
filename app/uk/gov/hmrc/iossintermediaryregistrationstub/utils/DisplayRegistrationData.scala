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
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.EtmpExclusionReason.{NoLongerSupplies, TransferringMSID}
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
      otherAddress = None,
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

  def minimalSuccessfulDisplayRegistrationResponseOtherAddress(clock: Clock, commencementDate: LocalDate): EtmpDisplayRegistration = {
    EtmpDisplayRegistration(
      customerIdentification = EtmpCustomerIdentification(
        idType = VRN,
        idValue = "700000007"
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
          issuedBy = "GB",
          tradingName = Some("Company name"),
          addressLine1 = "Other Address Line 1",
          addressLine2 = Some("Other Address Line 2"),
          townOrCity = "Other Town or City",
          regionOrState = Some("Other Region or State"),
          postcode = "BT111AH"
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

  def minimalDisplayWithClientsRegistrationResponse(clock: Clock, commencementDate: LocalDate, clientList: Seq[EtmpClientDetails]): EtmpDisplayRegistration = {
    EtmpDisplayRegistration(
      customerIdentification = EtmpCustomerIdentification(
        idType = VRN,
        idValue = "100000001"
      ),
      tradingNames = Seq.empty,
      clientDetails = clientList,
      intermediaryDetails = Some(
        EtmpIntermediaryDetails(
          otherIossIntermediaryRegistrations = Seq.empty
        )
      ),
      otherAddress = None,
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

  def minimalDisplayWithExcludedClientsRegistrationResponse(clock: Clock, commencementDate: LocalDate, clientList: Seq[EtmpClientDetails], exclusion: Seq[EtmpExclusion]): EtmpDisplayRegistration = {
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
      otherAddress = None,
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
      exclusions = exclusion,
      bankDetails = EtmpBankDetails(
        accountName = "Chartoff Winkler and Co.",
        bic = Some(Bic("BARCGB22456").get),
        iban = Iban("GB33BUKB202015555555555").toOption.get
      ),
      adminUse = EtmpAdminUse(Some(LocalDateTime.now(clock)))
    )
  }

  def fullDisplayWithExcludedClientsRegistrationResponse(clock: Clock, commencementDate: LocalDate, clientList: Seq[EtmpClientDetails], exclusion: Seq[EtmpExclusion]): EtmpDisplayRegistration = {
    EtmpDisplayRegistration(
      customerIdentification = EtmpCustomerIdentification(
        idType = VRN,
        idValue = "100000001"
      ),
      tradingNames = Seq(EtmpTradingName("tradingName1"), EtmpTradingName("tradingName2")),
      clientDetails = clientList,
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
      otherAddress = None,
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
      exclusions = exclusion,
      bankDetails = EtmpBankDetails(
        accountName = "Account name",
        bic = Some(Bic("ABCDGB2A").get),
        iban = Iban("GB33BUKB20201555555555").toOption.get
      ),
      adminUse = EtmpAdminUse(Some(LocalDateTime.now(clock)))
    )
  }

  def excludedManualNiAddress(clock: Clock, commencementDate: LocalDate): EtmpDisplayRegistration = {
    EtmpDisplayRegistration(
      customerIdentification = EtmpCustomerIdentification(
        idType = VRN,
        idValue = "700000003"
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
          issuedBy = "GB",
          tradingName = Some("Company name"),
          addressLine1 = "Other Address Line 1",
          addressLine2 = Some("Other Address Line 2"),
          townOrCity = "Other Town or City",
          regionOrState = Some("Other Region or State"),
          postcode = "BT111AH"
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
      exclusions =
        Seq(
          EtmpExclusion(
            exclusionReason = TransferringMSID,
            effectiveDate = LocalDate.of(2025, 1, 1),
            decisionDate = LocalDate.of(2025, 1, 1),
            quarantine = false
          )
        ),
      bankDetails = EtmpBankDetails(
        accountName = "Chartoff Winkler and Co.",
        bic = Some(Bic("BARCGB22456").get),
        iban = Iban("GB33BUKB202015555555555").toOption.get
      ),
      adminUse = EtmpAdminUse(Some(LocalDateTime.now(clock)))
    )
  }

  def fullDisplayWithCustomRejoinCoreValidationResponse(
                                                         clock: Clock,
                                                         commencementDate: LocalDate,
                                                         clientList: Seq[EtmpClientDetails],
                                                         exclusion: Seq[EtmpExclusion],
                                                         activeVrn: Option[String] = None,
                                                         quarantinedVrn: Option[String] = None,
                                                         activeTaxRef: Option[String] = None,
                                                         quarantinedTaxRef: Option[String] = None,
                                                         activeIntermediary: Option[String] = None,
                                                         quarantinedIntermediary: Option[String] = None,
                                                         issuedBy: String
                                                       ): EtmpDisplayRegistration = {
    EtmpDisplayRegistration(
      customerIdentification = EtmpCustomerIdentification(
        idType = VRN,
        idValue = "100000001"
      ),
      tradingNames = Seq(EtmpTradingName("tradingName1"), EtmpTradingName("tradingName2")),
      clientDetails = clientList,
      intermediaryDetails = Some(
        EtmpIntermediaryDetails(
          otherIossIntermediaryRegistrations = Seq(
            EtmpOtherIossIntermediaryRegistrations(
              issuedBy = "DE",
              intermediaryNumber = "IN2761234567"
            )
          ) ++ createOtherIossIntermediaryRegistrations(issuedBy, activeIntermediary, quarantinedIntermediary)
        )
      ),
      otherAddress = None,
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
        ) ++ createEuDisplayRegistration(issuedBy, activeVrn, quarantinedVrn, activeTaxRef, quarantinedTaxRef),
        contactName = "Test name",
        businessTelephoneNumber = "1234567890",
        businessEmailId = "email@test.com",
        unusableStatus = false,
        nonCompliantReturns = None,
        nonCompliantPayments = None
      ),
      exclusions = exclusion,
      bankDetails = EtmpBankDetails(
        accountName = "Account name",
        bic = Some(Bic("ABCDGB2A").get),
        iban = Iban("GB33BUKB20201555555555").toOption.get
      ),
      adminUse = EtmpAdminUse(Some(LocalDateTime.now(clock)))
    )
  }

  private def createEuDisplayRegistration(
                                           issuedBy: String,
                                           activeVrn: Option[String] = None,
                                           quarantinedVrn: Option[String] = None,
                                           activeTaxRef: Option[String] = None,
                                           quarantinedTaxRef: Option[String] = None,
                                         ): Option[EtmpDisplayEuRegistrationDetails] = {
    if (activeVrn.nonEmpty || quarantinedVrn.nonEmpty || activeTaxRef.nonEmpty || quarantinedTaxRef.nonEmpty) {
      Some(EtmpDisplayEuRegistrationDetails(
        issuedBy = issuedBy,
        vatNumber = determineTaxId(activeVrn, quarantinedVrn),
        taxIdentificationNumber = determineTaxId(activeTaxRef, quarantinedTaxRef),
        fixedEstablishmentTradingName = "Some Trading Name",
        fixedEstablishmentAddressLine1 = "Line 1",
        fixedEstablishmentAddressLine2 = Some("Line 2"),
        townOrCity = "Town",
        regionOrState = Some("Region"),
        postcode = Some("AB12 3CD")
      ))
    } else {
      None
    }
  }

  private def createOtherIossIntermediaryRegistrations(
                                                        issuedBy: String,
                                                        activeIntermediary: Option[String] = None,
                                                        quarantinedIntermediary: Option[String] = None
                                                      ): Option[EtmpOtherIossIntermediaryRegistrations] = {
    if (activeIntermediary.nonEmpty || quarantinedIntermediary.nonEmpty) {
      Some(EtmpOtherIossIntermediaryRegistrations(
        issuedBy = issuedBy,
        intermediaryNumber = determineIntermediaryNumber(activeIntermediary, quarantinedIntermediary)
      ))
    } else {
      None
    }
  }

  private def determineTaxId(a: Option[String], b: Option[String]): Option[String] = {
    (a, b) match {
      case (Some(a), _) => Some(a)
      case (_, Some(b)) => Some(b)
      case _ => None
    }
  }

  private def determineIntermediaryNumber(a: Option[String], b: Option[String]): String = {
    (a, b) match {
      case (Some(a), _) => a
      case (_, Some(b)) => b
      case _ => throw Exception("Must have an Intermediary number")
    }
  }
}
