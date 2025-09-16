/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.http.HeaderNames.{ACCEPT, AUTHORIZATION, CONTENT_TYPE, DATE}
import play.api.http.MimeTypes
import uk.gov.hmrc.iossintermediaryregistrationstub.format.Format.dateTimeFormatter

import java.time.LocalDateTime
import java.util.UUID

object Headers {

  def validHeaders: Seq[(String, String)] = Seq(
    (AUTHORIZATION, ""),
    (ACCEPT, MimeTypes.JSON),
    ("X-Correlation-Id", UUID.randomUUID().toString),
    ("X-Forwarded-Host", ""),
    (CONTENT_TYPE, MimeTypes.JSON),
    (DATE, dateTimeFormatter.format(LocalDateTime.now())))

  def missingHeaders: Seq[(String, String)] = Seq(
    (ACCEPT, MimeTypes.JSON),
    ("X-Correlation-Id", UUID.randomUUID().toString),
    ("X-Forwarded-Host", ""),
    (CONTENT_TYPE, MimeTypes.JSON),
    (DATE, dateTimeFormatter.format(LocalDateTime.now())))

  def invalidHeaders: Seq[(String, String)] = Seq(
    (AUTHORIZATION, ""),
    (ACCEPT, MimeTypes.JSON),
    ("X-Correlation-Id", "INVALID"),
    ("X-Forwarded-Host", ""),
    (CONTENT_TYPE, MimeTypes.JSON),
    (DATE, dateTimeFormatter.format(LocalDateTime.now())))
}
