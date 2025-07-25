/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.models.response

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class EtmpEnrolmentResponse(
                                  processingDateTime: LocalDateTime,
                                  formBundleNumber: Option[String],
                                  vrn: String,
                                  iossReference: String,
                                  businessPartner: String
                                )

object EtmpEnrolmentResponse {

  implicit val format: OFormat[EtmpEnrolmentResponse] = Json.format[EtmpEnrolmentResponse]
}
