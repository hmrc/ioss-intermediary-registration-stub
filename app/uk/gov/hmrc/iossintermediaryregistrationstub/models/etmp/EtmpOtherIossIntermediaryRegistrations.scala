package uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp

import play.api.libs.json.{Json, OFormat}

case class EtmpOtherIossIntermediaryRegistrations(issuedBy: String, intermediaryNumber: String)

object EtmpOtherIossIntermediaryRegistrations {
  implicit val format: OFormat[EtmpOtherIossIntermediaryRegistrations] = Json.format[EtmpOtherIossIntermediaryRegistrations]
}
