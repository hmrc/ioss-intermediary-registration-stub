/*
 * Copyright 2023 HM Revenue & Customs
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

case class EtmpRegistrationSchemaData() {

  def schemaPath = "/resources/schemas/etmp-registration-schema.json"

  def givenOptionalExample: String =
    """{
    "administration": {
      "messageType": "IOSSSubscriptionCreate",
      "regimeID": "IOSS"
    },
    "customerIdentification": {
      "vrn": "123456789"
    },
    "schemeDetails": {
      "commencementDate": "2023-10-01",
      "websites": [
        {
          "websiteAddress": "website1"
        }
      ],
      "contactName": "Mr Test",
      "businessTelephoneNumber": "123 456789",
      "businessEmailId": "mrtest@example.co.uk"
    },
    "bankDetails": {
      "accountName": "Mr Test",
      "iban": "LM1233Y55P22T9C"
    }
  }""".stripMargin

  def givenFullExample: String = """{
    "administration": {
      "messageType": "IOSSSubscriptionCreate",
      "regimeID": "IOSS"
    },
    "customerIdentification": {
      "vrn": "123456789"
    },
    "tradingNames": [
      {
        "tradingName": "tradingName1"
      },
      {
        "tradingName": "tradingName2"
      }
    ],
    "schemeDetails": {
      "commencementDate": "2023-10-01",
      "euRegistrationDetails": [
        {
          "countryOfRegistration": "DE",
          "traderId": {
            "taxReferenceNumber": "123456789"
          },
          "tradingName": "tradingName1",
          "fixedEstablishmentAddressLine1": "Line 1",
          "townOrCity": "Some Town"
        },
        {
          "countryOfRegistration": "ES",
          "traderId": {
            "vatNumber": "ES123456789"
          },
          "tradingName": "tradingName1",
          "fixedEstablishmentAddressLine1": "Line 1",
          "fixedEstablishmentAddressLine2": "Line 2",
          "townOrCity": "Some Town",
          "regionOrState": "Some State",
          "postcode": "AB12 3CD"
        }
      ],
      "previousEURegistrationDetails": [
        {
          "issuedBy": "DE",
          "registrationNumber": "1234567",
          "schemeType": "OSS Union"
        },
        {
          "issuedBy": "DE",
          "registrationNumber": "1234567",
          "schemeType": "IOSS with intermediary",
          "intermediaryNumber": "IM561234567"
        }
      ],
      "websites": [
        {
          "websiteAddress": "website1"
        },
        {
          "websiteAddress": "website2"
        }
      ],
      "contactName": "Mr Test",
      "businessTelephoneNumber": "123 456789",
      "businessEmailId": "mrtest@example.co.uk",
      "nonCompliantReturns": "1",
      "nonCompliantPayments": "1"
    },
    "bankDetails": {
      "accountName": "Mr Test",
      "bic": "PQSIVTG9Z4P",
      "iban": "LM1233Y55P22T9C"
    }
  }""".stripMargin
}
