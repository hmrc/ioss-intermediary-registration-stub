/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.models.response

import play.api.libs.json.{Json, OFormat}

case class EtmpEnrolmentErrorResponse(errorDetail: EisErrorResponse)

object EtmpEnrolmentErrorResponse {

  implicit val format: OFormat[EtmpEnrolmentErrorResponse] = Json.format[EtmpEnrolmentErrorResponse]
}
