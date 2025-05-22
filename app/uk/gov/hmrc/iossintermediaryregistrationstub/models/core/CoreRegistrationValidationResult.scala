/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.models.core

import play.api.libs.json.{Json, OFormat}

case class CoreRegistrationValidationResult(searchId: String,
                                            searchIntermediary: Option[String],
                                            searchIdIssuedBy: String,
                                            traderFound: Boolean,
                                            matches: Seq[Match]
                                           )

object CoreRegistrationValidationResult {

  implicit val format: OFormat[CoreRegistrationValidationResult] = Json.format[CoreRegistrationValidationResult]
}

case class Match(matchType: MatchType,
                 traderId: String,
                 memberState: String,
                 exclusionStatusCode: Option[Int],
                 exclusionDecisionDate: Option[String],
                 exclusionEffectiveDate: Option[String],
                 nonCompliantReturns: Option[Int],
                 nonCompliantPayments: Option[Int]
                )

object Match {

  implicit val format: OFormat[Match] = Json.format[Match]
}

