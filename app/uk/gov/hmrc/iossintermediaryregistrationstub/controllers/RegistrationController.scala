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
import play.api.libs.json.{JsError, Json, JsSuccess, JsValue}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.core.{EisDisplayErrorDetail, EisDisplayErrorResponse}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.*
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.EtmpExclusionReason.{FailsToComply, NoLongerSupplies, Reversal, TransferringMSID, VoluntarilyLeaves}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.etmp.amend.EtmpAmendRegistrationRequest
import uk.gov.hmrc.iossintermediaryregistrationstub.models.response.{EisErrorResponse, EtmpAmendRegistrationResponse, EtmpEnrolmentErrorResponse, EtmpEnrolmentResponse}
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.*
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.DisplayRegistrationData.*
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

            case "IN9008888888" =>
              //              No active and no previous clients
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq.empty
              )))

            case "IN9008888887" =>
              //                Multiple active clients and no previous clients
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("Active Client 1", "IM9001144881", false),
                  EtmpClientDetails("Active Client 2", "IM9001144882", false),
                  EtmpClientDetails("Active Client 3", "IM9001144883", false)
                )
              )))

            case "IN9008888886" =>
              //                No active clients and multiple previous clients
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("Previous Client 1", "IM9001144884", true),
                  EtmpClientDetails("Previous Client 2", "IM9001144885", true),
                  EtmpClientDetails("Previous Client 3", "IM9001144886", true)
                )
              )))

            case "IN9008888885" =>
              //                Intermediary and NETP all registered today - no returns due yet
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.now(),
                Seq(
                  EtmpClientDetails("Just registered 1", "IM9001144811", false),
                  EtmpClientDetails("Just registered 2", "IM9001144822", false)
                )
              )))

            case "IN9008888884" =>
              //                Some returns due, none overdue
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.now().minusMonths(1).withDayOfMonth(1),
                Seq(
                  EtmpClientDetails("Returns 1", "IM9001144833", false),
                  EtmpClientDetails("Returns 2", "IM9001144844", false),
                  EtmpClientDetails("Returns 3", "IM9001144855", false),
                  EtmpClientDetails("Returns 4", "IM9001144866", false)
                )
              )))

            case "IN9008888883" =>
              //                Some returns due, some but not all overdue
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.now().minusMonths(2).withDayOfMonth(1),
                Seq(
                  EtmpClientDetails("Returns 1", "IM9001144877", false),
                  EtmpClientDetails("Returns 2", "IM9001144888", false),
                  EtmpClientDetails("Returns 3", "IM9001144899", false),
                  EtmpClientDetails("Returns 4", "IM9001144800", false)
                )
              )))

            case "IN9006655333" =>
              //                Return submission failures
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2023, 10, 1),
                Seq(
                  EtmpClientDetails("Failure 1", "IM9007777771", false),
                  EtmpClientDetails("Failure 2", "IM9007777772", false)
                )
              )))

            case "IN9006655444" =>
              //                Multiple saved returns
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("Saved Return 1", "IM9006655441", false),
                  EtmpClientDetails("Saved Return 2", "IM9006655442", false),
                  EtmpClientDetails("Saved Return 3", "IM9006655443", false)
                )
              )))

            case "IN9006655555" =>
              //                One saved return
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("Single Saved Return", "IM9006655551", false),
                  EtmpClientDetails("No Saved Return 1", "IM9006655552", false),
                  EtmpClientDetails("No Saved Return 2", "IM9006655553", false)
                )
              )))

            case "IN9005999997" =>
              //                Intermediary with NETP transferring MSID for returns
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2024, 1, 1),
                Seq(
                  EtmpClientDetails("NETP Partial First Return", "IM9005555551", false),
                  EtmpClientDetails("NETP Partial Final Return", "IM9005555552", true)
                )
              )))

            case "IN900666001" =>
              // Client with amend registration failure
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("Error Client", "IM9002222222", false)
                )
              )))

            case "IN9001234444" =>
              // Non-Ni Other Address scenario
              Ok(Json.toJson(minimalSuccessfulDisplayRegistrationResponseOtherAddress(clock, LocalDate.of(2025, 1, 1))))

            case "IN9001235555" =>
              // Non-Ni Other Address scenario - excluded
              Ok(Json.toJson(excludedManualNiAddress(clock, LocalDate.of(2025, 1, 1))))

            case "IN9001234567" =>
              //              Multiple active and previous clients
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("First Client", "IM9001144771", false),
                  EtmpClientDetails("Second Client", "IM9001144772", true),
                  EtmpClientDetails("Third Client", "IM9001144773", false),
                  EtmpClientDetails("Fourth Client", "IM9001144774", true),
                  EtmpClientDetails("Fifth Client", "IM9001144775", false),
                  EtmpClientDetails("Sixth Client", "IM9001144776", true),
                  EtmpClientDetails("Seventh Client", "IM9001144777", false),
                  EtmpClientDetails("Eighth Client", "IM9001144778", false),
                )
              )))

            case "IN9001001001" =>
              //                Registered last year with one client to be used for returns over multiple years
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2024, 12, 1),
                Seq(
                  EtmpClientDetails("Multiple Years of Returns", "IM9001001001", false)
                )
              )))

            case "IN9002002002" =>
              //                Registered over 6 years ago to test NETP returns over 6 years old
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.now().minusYears(6).minusMonths(2),
                Seq(
                  EtmpClientDetails("Returns over 6 years old", "IM9002002002", false)
                )
              )))

            case "IN9003003003" =>
              //                Has client with exclusion that has been reversed
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("NETP Exclusion Reversed", "IM9003003003", true)
                )
              )))

            case "IN9004004004" =>
              //                Registered over 3 years ago to test excluded NETP returns and payments over 3 years old
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.now().minusYears(3).minusMonths(6),
                Seq(
                  EtmpClientDetails("Returns over 3 years old", "IM9004004004", true)
                )
              )))

            case "IN9002323232" =>
              //              Excluded Intermediary with effective date in the past - minimal registration details
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = TransferringMSID,
                    effectiveDate = LocalDate.of(2025, 1, 1),
                    decisionDate = LocalDate.of(2025, 1, 1),
                    quarantine = false
                  )
                )
              )))

            case "IN9000306831" =>
              //              Excluded intermediary with excluded NETP who has no outstanding returns
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails(clientName = "Testies", clientIossID = "IM9000306831", clientExcluded = true)
                ),
                Seq(
                  EtmpExclusion(
                    exclusionReason = TransferringMSID,
                    effectiveDate = LocalDate.of(2025, 3, 1),
                    decisionDate = LocalDate.of(2025, 3, 1),
                    quarantine = false
                  )
                )
              )))

            case "IN9000306832" =>
              //              Excluded intermediary with excluded NETP who has a mix of outstanding and not outstanding returns
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails(clientName = "Testies", clientIossID = "IM9000306832", clientExcluded = true),
                  EtmpClientDetails(clientName = "Testies 2", clientIossID = "IM9000306833", clientExcluded = true)
                ),
                Seq(
                  EtmpExclusion(
                    exclusionReason = TransferringMSID,
                    effectiveDate = LocalDate.of(2025, 3, 1),
                    decisionDate = LocalDate.of(2025, 3, 1),
                    quarantine = false
                  )
                )
              )))

            case "IN9001113232" =>
              //              Excluded Intermediary with effective date in the past - full registration details
              Ok(Json.toJson(fullDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = TransferringMSID,
                    effectiveDate = LocalDate.of(2025, 1, 1),
                    decisionDate = LocalDate.of(2025, 1, 1),
                    quarantine = false
                  )
                )
              )))


            case "IN9003232323" =>
              //              Excluded Intermediary with effective date in the future
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = TransferringMSID,
                    effectiveDate = LocalDate.now(clock).plusMonths(1),
                    decisionDate = LocalDate.now(clock).plusMonths(1),
                    quarantine = false
                  )
                )
              )))

            case "IN9002323333" =>
              //              Excluded Intermediary with effective date in the past - Reversal
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = Reversal,
                    effectiveDate = LocalDate.of(2025, 1, 1),
                    decisionDate = LocalDate.of(2025, 1, 1),
                    quarantine = false
                  )
                )
              )))

            case "IN9002323334" =>
              //              Quarantined Intermediary with effective date within 2 years
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = FailsToComply,
                    effectiveDate = LocalDate.now.minusMonths(2),
                    decisionDate = LocalDate.now.minusMonths(2),
                    quarantine = true
                  )
                )
              )))

            case "IN9002323335" =>
              //              Quarantined Intermediary with effective date 2 years ago therefore quarantine has expired
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = FailsToComply,
                    effectiveDate = LocalDate.now.minusYears(2).minusDays(1),
                    decisionDate = LocalDate.now.minusYears(2).minusDays(1),
                    quarantine = true
                  )
                )
              )))

            case "IN9003344551" =>
              // Kick out due to active VRN io another country
              Ok(Json.toJson(fullDisplayWithCustomRejoinCoreValidationResponse(
                clock = clock,
                commencementDate = LocalDate.of(2025, 1, 1),
                clientList = Seq.empty,
                exclusion = Seq(
                  EtmpExclusion(
                    exclusionReason = NoLongerSupplies,
                    effectiveDate = LocalDate.now(clock).minusYears(2),
                    decisionDate = LocalDate.now(clock).minusYears(2),
                    quarantine = false
                  )
                ),
                issuedBy = "EE",
                activeVrn = Some("333333333")
              )))

            case "IN9003344552" =>
              // Kick out due to quarantined VRN
              Ok(Json.toJson(fullDisplayWithCustomRejoinCoreValidationResponse(
                clock = clock,
                commencementDate = LocalDate.of(2025, 1, 1),
                clientList = Seq.empty,
                exclusion = Seq(
                  EtmpExclusion(
                    exclusionReason = NoLongerSupplies,
                    effectiveDate = LocalDate.now(clock).minusYears(2),
                    decisionDate = LocalDate.now(clock).minusYears(2),
                    quarantine = false
                  )
                ),
                issuedBy = "EE",
                quarantinedVrn = Some("333333334")
              )))

            case "IN9003344553" =>
              // Kick out due to active Tax Reference in another country
              Ok(Json.toJson(fullDisplayWithCustomRejoinCoreValidationResponse(
                clock = clock,
                commencementDate = LocalDate.of(2025, 1, 1),
                clientList = Seq.empty,
                exclusion = Seq(
                  EtmpExclusion(
                    exclusionReason = NoLongerSupplies,
                    effectiveDate = LocalDate.now(clock).minusYears(2),
                    decisionDate = LocalDate.now(clock).minusYears(2),
                    quarantine = false
                  )
                ),
                issuedBy = "EE",
                activeTaxRef = Some("333333333")
              )))

            case "IN9007230002" =>
              //              Excluded Intermediary - excluded 7 months ago - previous intermediary registration scenarios
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.now().minusMonths(9),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = VoluntarilyLeaves,
                    effectiveDate = LocalDate.now().minusMonths(7),
                    decisionDate = LocalDate.now().minusMonths(7),
                    quarantine = false
                  )
                )
              )))

            case "IN9007230001" | "IN9008230002" =>
              //              Excluded Intermediary - excluded 4 months ago - previous intermediary registration scenarios
              Ok(Json.toJson(minimalDisplayWithExcludedClientsRegistrationResponse(
                clock,
                LocalDate.now().minusMonths(6),
                Seq.empty,
                Seq(
                  EtmpExclusion(
                    exclusionReason = VoluntarilyLeaves,
                    effectiveDate = LocalDate.now().minusMonths(4),
                    decisionDate = LocalDate.now().minusMonths(4),
                    quarantine = false
                  )
                )
              )))

            case "IN9008230001" | "IN9009230002" =>
              //Registered intermediary 3 months ago - previous intermediary registration scenarios
              Ok(Json.toJson(fullSuccessfulDisplayRegistrationResponse(clock, LocalDate.now().minusMonths(3), false)))

            case "IN9003344554" =>
              // Kick out due to quarantined Tax Reference
              Ok(Json.toJson(fullDisplayWithCustomRejoinCoreValidationResponse(
                clock = clock,
                commencementDate = LocalDate.of(2025, 1, 1),
                clientList = Seq.empty,
                exclusion = Seq(
                  EtmpExclusion(
                    exclusionReason = NoLongerSupplies,
                    effectiveDate = LocalDate.now(clock).minusYears(2),
                    decisionDate = LocalDate.now(clock).minusYears(2),
                    quarantine = false
                  )
                ),
                issuedBy = "EE",
                quarantinedTaxRef = Some("333333334")
              )))

            case "IN9003344555" =>
              // Kick out due to active Intermediary in another country
              Ok(Json.toJson(fullDisplayWithCustomRejoinCoreValidationResponse(
                clock = clock,
                commencementDate = LocalDate.of(2025, 1, 1),
                clientList = Seq.empty,
                exclusion = Seq(
                  EtmpExclusion(
                    exclusionReason = NoLongerSupplies,
                    effectiveDate = LocalDate.now(clock).minusYears(2),
                    decisionDate = LocalDate.now(clock).minusYears(2),
                    quarantine = false
                  )
                ),
                issuedBy = "SI",
                activeIntermediary = Some("IN7057777123")
              )))

            case "IN9003344556" =>
              // Kick out due to quarantined Intermediary
              Ok(Json.toJson(fullDisplayWithCustomRejoinCoreValidationResponse(
                clock = clock,
                commencementDate = LocalDate.of(2025, 1, 1),
                clientList = Seq.empty,
                exclusion = Seq(
                  EtmpExclusion(
                    exclusionReason = NoLongerSupplies,
                    effectiveDate = LocalDate.now(clock).minusYears(2),
                    decisionDate = LocalDate.now(clock).minusYears(2),
                    quarantine = false
                  )
                ),
                issuedBy = "LV",
                quarantinedIntermediary = Some("IN4287777123")
              )))

            case "IN9002222222" =>
              Ok(Json.toJson(fullSuccessfulDisplayRegistrationResponse(clock, LocalDate.of(2025, 1, 1), true)))

            case "IN9001144663" =>
              //                One active and one excluded client - no returns
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("Excluded Client One", "IM9001144663", true),
                  EtmpClientDetails("Active Client One", "IM9001144664", false),
                )
              )))

            case "IN9001144665" =>
              //                Two excluded clients - no returns
              Ok(Json.toJson(minimalDisplayWithClientsRegistrationResponse(
                clock,
                LocalDate.of(2025, 1, 1),
                Seq(
                  EtmpClientDetails("Excluded Client One", "IM9001144665", true),
                  EtmpClientDetails("Excluded Client Two", "IM9001144666", true),
                )
              )))

            case _ =>
              Ok(Json.toJson(fullSuccessfulDisplayRegistrationResponse(clock, LocalDate.of(2025, 1, 1), false)))
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

  def amendRegistration: Action[AnyContent] = Action.async {
    implicit request =>

      val maybeJsonBody: Option[JsValue] = request.body.asJson

      logger.info(s"Amend payload: ${request.body}")

      jsonSchemaHelper.applySchemaHeaderValidation(request.headers) {
        jsonSchemaHelper.applySchemaValidation("/resources/schemas/amend-registration-schema.json", maybeJsonBody) match {
          case SuccessSchema =>

            maybeJsonBody.map { body =>
              logger.info(s"Amend registration request received $body")
              body.validate[EtmpAmendRegistrationRequest] match {
                case JsSuccess(etmpAmendRegistrationRequest, _) =>
                  val idValue = etmpAmendRegistrationRequest.customerIdentification.iossNumber
                  if (idValue == "IN9009999966") {
                    logger.info("Registration not found")
                    Future.successful(NotFound)
                  } else {
                    logger.info("Successfully amended a registration")

                    val iossNumber = if (etmpAmendRegistrationRequest.changeLog.reRegistration) {
                      idValue + "-NEW"
                    } else {
                      idValue
                    }

                    val randomNumber = randomService.randomInt(100000)

                    Future.successful(Ok(Json.toJson(EtmpAmendRegistrationResponse(
                      processingDateTime = LocalDateTime.now(clock),
                      formBundleNumber = s"$idValue-id-$randomNumber",
                      vrn = idValue,
                      intReference = iossNumber,
                      businessPartner = "A Business Partner"
                    ))))
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
}
