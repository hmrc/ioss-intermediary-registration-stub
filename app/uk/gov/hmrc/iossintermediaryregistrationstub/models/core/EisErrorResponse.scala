/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.models.core

import play.api.libs.json.{Json, OFormat}

import java.time.Instant

case class EisErrorResponse(
                             timestamp: Instant,
                             error: String,
                             errorMessage: String
                           )

object EisErrorResponse {

  implicit val format: OFormat[EisErrorResponse] = Json.format[EisErrorResponse]

}

case class EisDisplayErrorResponse(
                                    errorDetail: EisDisplayErrorDetail
                                  )


object EisDisplayErrorResponse {

  val displayErrorCodeNoRegistration = "089"

  implicit val format: OFormat[EisDisplayErrorResponse] = Json.format[EisDisplayErrorResponse]

}

case class EisDisplayErrorDetail(
                                  correlationId: String,
                                  errorCode: String,
                                  errorMessage: String,
                                  timestamp: String
                                )

object EisDisplayErrorDetail {

  val displayErrorCodeNoRegistration = "089"

  implicit val format: OFormat[EisDisplayErrorDetail] = Json.format[EisDisplayErrorDetail]

}

