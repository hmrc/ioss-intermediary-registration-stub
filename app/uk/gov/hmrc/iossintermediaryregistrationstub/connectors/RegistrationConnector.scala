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

package uk.gov.hmrc.iossintermediaryregistrationstub.connectors

import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.iossintermediaryregistrationstub.models.enrolments.TaxEnrolmentResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationConnector @Inject()(
                                       httpClient: HttpClientV2
                                     )(implicit ec: ExecutionContext) {

  def doCallback(subscriptionId: String, callbackUrl: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val enrolmentResponse = TaxEnrolmentResponse("SUCCEEDED")

    httpClient.post(url"$callbackUrl").withBody(Json.toJson(enrolmentResponse)).execute[HttpResponse]
  }
}