/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.models.response

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class EtmpAmendRegistrationResponse(
                                          processingDateTime: LocalDateTime,
                                          formBundleNumber: String,
                                          vrn: String,
                                          iossReference: String,
                                          businessPartner: String
                                        )

object EtmpAmendRegistrationResponse {

  implicit val format: OFormat[EtmpAmendRegistrationResponse] = Json.format[EtmpAmendRegistrationResponse]

}
