/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.iossintermediaryregistrationstub.controllers

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.Action
import uk.gov.hmrc.iossintermediaryregistrationstub.connectors.RegistrationConnector
import uk.gov.hmrc.iossintermediaryregistrationstub.controllers.actions.DefaultAuthenticatedControllerComponents
import uk.gov.hmrc.iossintermediaryregistrationstub.models.enrolments.SubscriberRequest
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class EnrolmentsController @Inject()(
                                      cc: DefaultAuthenticatedControllerComponents,
                                      registrationConnector: RegistrationConnector
                                    ) extends BackendController(cc) with Logging {

  def confirm(subscriptionId: String): Action[SubscriberRequest] = Action(parse.json[SubscriberRequest]) {
    implicit request =>
      if (request.headers.headers.exists(_._1.equalsIgnoreCase(AUTHORIZATION))) {
        logger.info(s"Call back URL: ${request.body.callback}")
        registrationConnector.doCallback(subscriptionId, request.body.callback)
        NoContent
      }
      else {
        BadRequest(Json.toJson(s"Bad Request - missing $AUTHORIZATION"))
      }
  }

}
