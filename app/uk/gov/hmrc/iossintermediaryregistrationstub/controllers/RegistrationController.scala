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

package uk.gov.hmrc.iossintermediaryregistrationstub.controllers

import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.core.{EisDisplayErrorDetail, EisDisplayErrorResponse}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.*
import uk.gov.hmrc.iossintermediaryregistrationstub.models.response.{EisErrorResponse, EtmpEnrolmentErrorResponse, EtmpEnrolmentResponse}
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.*
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.DisplayRegistrationData.{fullSuccessfulDisplayRegistrationResponse, minimalSuccessfulDisplayRegistrationResponse}
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.RegistrationHeaderHelper.{InvalidHeader, MissingHeader}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.{Clock, LocalDate, LocalDateTime}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future


class RegistrationController @Inject()(
                                        cc: ControllerComponents,
                                        jsonSchemaHelper: JsonSchemaHelper,
                                        randomService: RandomService,
                                        clock: Clock
                                      ) extends BackendController(cc) with Logging {

  def createRegistration(): Action[AnyContent] = Action.async {
    implicit request =>

      val maybeJsonBody: Option[JsValue] = request.body.asJson

      logger.info(s"Payload received on create registration $maybeJsonBody")

      jsonSchemaHelper.applySchemaHeaderValidation(request.headers) {
        jsonSchemaHelper.applySchemaValidation("/resources/schemas/etmp-registration-schema.json", maybeJsonBody) match {
          case SuccessSchema =>

            maybeJsonBody.map { body =>
              logger.info(s"Create registration request received $body")
              body.validate[EtmpRegistrationRequest] match {
                case JsSuccess(etmpRegistrationRequest, _) =>
                  val idValue = etmpRegistrationRequest.customerIdentification.idValue
                  idValue match {
                    case "222222223" =>
                      logger.info("Matched stubbed error - Registration already exists")
                      Future.successful(Conflict)
                    case "222222233" =>
                      logger.info("Matched stubbed error - Error creating enrolment for registration")
                      Future.successful(BadRequest)
                    case "666000000" | "177550000" =>
                      logger.info("Matched stubbed error - Error creating enrolment for registration")
                      Future.successful(UnprocessableEntity(Json.toJson(EtmpEnrolmentErrorResponse(
                        EisErrorResponse(LocalDateTime.now(clock), "007", "Business Partner already has an active OSS Subscription for this regime")
                      ))))
                    case "666000001" =>
                      logger.info("Matched stubbed error - 123 - error")
                      Future.successful(UnprocessableEntity(Json.toJson(EtmpEnrolmentErrorResponse(
                        errorDetail = EisErrorResponse(LocalDateTime.now(clock), "123", "error")))
                      ))
                    case _ =>
                      logger.info("Successfully created a registration")
                      val randomNumber = randomService.randomInt(100000)
                      val random7Digit = randomService.randomInt(minValue = 1111111, maxValue = 9999999)
                      Future.successful(Created(Json.toJson(EtmpEnrolmentResponse(LocalDateTime.now(clock), Some(s"$idValue-id-$randomNumber"), idValue, s"IN900$random7Digit", "A Business Partner"))))
                  }
                case JsError(errors) =>
                  logger.error(s"Error with json $errors")
                  Future.successful(BadRequest(Json.toJson(EtmpEnrolmentErrorResponse(errorDetail = EisErrorResponse(
                    timestamp = LocalDateTime.now(),
                    errorCode = "400",
                    errorMessage = "Bad Request - unknown error"
                  )))))
              }
            }.getOrElse {
              logger.error(s"unable to get json body ${request.body}")
              Future.successful(InternalServerError(s"unable to get json body ${request.body}"))
            }

          case failedResult =>
            logger.error(s"failed create registration request with $failedResult")
            Future.successful(InternalServerError(s"There was an error with the registration schema $failedResult"))
        }
      }
  }

  def getDisplayRegistration(intermediaryNumber: String): Action[AnyContent] = Action {
    implicit request =>

      RegistrationHeaderHelper.validateHeaders(request.headers.headers) match {
        case Right(_) =>
          intermediaryNumber match {
            case "IN9009999999" =>
              val notFoundResponse = EisDisplayErrorResponse(EisDisplayErrorDetail(UUID.randomUUID().toString, "089", "Registration not found", LocalDate.now().toString))
              UnprocessableEntity(Json.toJson(notFoundResponse))

            case "IN9001234567" =>
              Ok(Json.toJson(minimalSuccessfulDisplayRegistrationResponse(clock, LocalDate.of(2025, 1, 1))))
              
            case _ =>
              Ok(Json.toJson(fullSuccessfulDisplayRegistrationResponse(clock, LocalDate.of(2025, 1, 1))))
          }

        case Left(MissingHeader(header)) =>
          logger.error(s"Bad Request - missing $header")
          BadRequest(Json.toJson(s"Bad Request - missing $header"))

        case Left(InvalidHeader(header)) =>
          logger.error(s"Bad Request - invalid $header")
          BadRequest(Json.toJson(s"Bad Request - invalid $header"))

        case headerError =>
          logger.error(s"Bad Request - unknown error $headerError")
          BadRequest(Json.toJson("Bad Request - unknown error"))
      }
  }
}
