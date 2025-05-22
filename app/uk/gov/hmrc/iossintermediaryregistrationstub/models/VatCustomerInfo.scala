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

package uk.gov.hmrc.iossintermediaryregistrationstub.models

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.*

import java.time.LocalDate

case class VatCustomerInfo(
                            desAddress: DesAddress,
                            registrationDate: Option[LocalDate],
                            partyType: Option[String],
                            organisationName: Option[String],
                            individual: Option[IndividualName],
                            singleMarketIndicator: Boolean,
                            deregistrationDecisionDate: Option[LocalDate]
                          )

object VatCustomerInfo {

  implicit val reads: Reads[VatCustomerInfo] =
    (
      (__ \ "approvedInformation" \ "PPOB" \ "address").read[DesAddress] and
        (__ \ "approvedInformation" \ "customerDetails" \ "effectiveRegistrationDate").readNullable[LocalDate] and
        (__ \ "approvedInformation" \ "customerDetails" \ "partyType").readNullable[String] and
        (__ \ "approvedInformation" \ "customerDetails" \ "organisationName").readNullable[String] and
        (__ \ "approvedInformation" \ "customerDetails" \ "individual").readNullable[IndividualName] and
        (__ \ "approvedInformation" \ "customerDetails" \ "singleMarketIndicator").read[Boolean] and
        (__ \ "approvedInformation" \ "deregistration" \ "effectDateOfCancellation").readNullable[LocalDate]
      )(VatCustomerInfo.apply)

  implicit val writes: OWrites[VatCustomerInfo] =
    (
      (__ \ "approvedInformation" \ "PPOB" \ "address").write[DesAddress] and
        (__ \ "approvedInformation" \ "customerDetails" \ "effectiveRegistrationDate").writeNullable[LocalDate] and
        (__ \ "approvedInformation" \ "customerDetails" \ "partyType").writeNullable[String] and
        (__ \ "approvedInformation" \ "customerDetails" \ "organisationName").writeNullable[String] and
        (__ \ "approvedInformation" \ "customerDetails" \ "individual").writeNullable[IndividualName] and
        (__ \ "approvedInformation" \ "customerDetails" \ "singleMarketIndicator").write[Boolean] and
        (__ \ "approvedInformation" \ "deregistration" \ "effectDateOfCancellation").writeNullable[LocalDate]
      )(vatCustomerInfo => Tuple.fromProductTyped(vatCustomerInfo))
}

case class DesAddress(
                       line1: String,
                       line2: Option[String],
                       line3: Option[String],
                       line4: Option[String],
                       line5: Option[String],
                       postCode: Option[String],
                       countryCode: String
                     )

object DesAddress {

  implicit val format: OFormat[DesAddress] = Json.format[DesAddress]
}

case class IndividualName(
                           firstName: Option[String],
                           middleName: Option[String],
                           lastName: Option[String]
                         )

object IndividualName {

  implicit val reads: Reads[IndividualName] =
    (
      (__ \ "firstName").readNullable[String] and
        (__ \ "middleName").readNullable[String] and
        (__ \ "lastName").readNullable[String]
      )(IndividualName.apply)

  implicit val writes: OWrites[IndividualName] =
    (
      (__ \ "firstName").writeNullable[String] and
        (__ \ "middleName").writeNullable[String] and
        (__ \ "lastName").writeNullable[String]
      )(individualName => Tuple.fromProductTyped(individualName))
}
