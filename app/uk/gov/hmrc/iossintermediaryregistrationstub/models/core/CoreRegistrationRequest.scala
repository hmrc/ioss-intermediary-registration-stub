/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.models.core

import play.api.libs.json.{Json, OFormat}

case class CoreRegistrationRequest(source: String,
                                   scheme: Option[String],
                                   searchId: String,
                                   searchIntermediary: Option[String],
                                   searchIdIssuedBy: String)

object CoreRegistrationRequest {
  implicit val format: OFormat[CoreRegistrationRequest] = Json.format[CoreRegistrationRequest]
}
