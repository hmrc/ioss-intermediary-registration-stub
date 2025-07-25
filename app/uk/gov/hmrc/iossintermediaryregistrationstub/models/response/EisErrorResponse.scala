/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.models.response

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class EisErrorResponse(timestamp: LocalDateTime, errorCode: String, errorMessage: String)

object EisErrorResponse {

  implicit val format: OFormat[EisErrorResponse] = Json.format[EisErrorResponse]
}
