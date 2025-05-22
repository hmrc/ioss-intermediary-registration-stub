package uk.gov.hmrc.iossintermediaryregistrationstub.format

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Format {
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    .withLocale(Locale.UK)
    .withZone(ZoneId.of("GMT"))
}
