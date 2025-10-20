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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.core.{CoreRegistrationValidationResult, Match, MatchType}
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.{JsonSchemaHelper, SuccessSchema}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

object MatchInfractionIds {
  val activeSearchId = "333333333"
  val quarantinedSearchId = "333333334"
  val activeSearchIdOss = "333333335"
  val quarantinedSearchIdOss = "333333336"
  val activeSearchIdIoss = "333333337"
  val quarantinedSearchIdIoss = "333333338"
  val quarantineExpiredSearchId = "333333339"

}


class CoreRegistrationValidationController @Inject()(
                                                      cc: ControllerComponents,
                                                      jsonSchemaHelper: JsonSchemaHelper
                                                    ) extends BackendController(cc) with Logging {


  private val genericMatch = Match(
    matchType = MatchType.TraderIdQuarantinedNETP,
    traderId = "333333331",
    memberState = "EE",
    exclusionStatusCode = None,
    exclusionDecisionDate = Some(LocalDate.now().withMonth(1).withDayOfMonth(1).toString),
    exclusionEffectiveDate = Some(LocalDate.now().withMonth(1).withDayOfMonth(1).toString),
    nonCompliantReturns = None,
    nonCompliantPayments = None
  )

  private val expiredQuarantineMatch = Match(
    matchType = MatchType.TraderIdQuarantinedNETP,
    traderId = "333333339",
    memberState = "EE",
    exclusionStatusCode = None,
    exclusionDecisionDate = Some(LocalDate.now().minusYears(2).toString),
    exclusionEffectiveDate = Some(LocalDate.now().minusYears(2).toString),
    nonCompliantReturns = None,
    nonCompliantPayments = None
  )

  private val result = CoreRegistrationValidationResult(
    searchId = """333333331""",
    searchIntermediary = None,
    searchIdIssuedBy = "EE",
    traderFound = true,
    matches = Seq(
      genericMatch
    )
  )

  def coreValidateRegistration: Action[AnyContent] = Action.async {
    implicit request =>
      val jsonBody: Option[JsValue] = request.body.asJson
      jsonSchemaHelper.applySchemaHeaderValidation(request.headers) {
        jsonSchemaHelper.applySchemaValidation("/resources/schemas/core-registration-schema.json", jsonBody) match {
          case SuccessSchema =>

            //{"source":"VATNumber","searchId":"100000001","searchIdIssuedBy":"GB"}
            val searchId = jsonBody.map(body => (body \ "searchId").as[String]).get
            val searchIdIssuedBy = jsonBody.map(body => (body \ "searchIdIssuedBy").as[String]).get

            val findMatch = (searchIdIssuedBy, searchId) match {
              case (_, MatchInfractionIds.activeSearchId) =>
                logger.info("Intermediary match found. Active in another MS. GG VRN kickout")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = None, traderId = "IN2467777777"))
              case (_, MatchInfractionIds.`quarantinedSearchId`) =>
                logger.info("Intermediary match found. Quarantined in another MS. GG VRN kickout")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN2467777777"))
              case (_, MatchInfractionIds.`quarantineExpiredSearchId`) =>
                logger.info("Intermediary match found. Quarantined in another MS. GG VRN kickout")
                Seq(expiredQuarantineMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN2467777777"))
              case (_, MatchInfractionIds.activeSearchIdOss) =>
                logger.info("Oss match found. Active in another MS. GG VRN does not kickout")
                Seq(genericMatch.copy(matchType = MatchType.OtherMSNETPActiveNETP, exclusionStatusCode = None, traderId = "333333335"))
              case (_, MatchInfractionIds.`quarantinedSearchIdOss`) =>
                logger.info("Oss match found. Quarantined in another MS. GG VRN does not kickout")
                Seq(genericMatch.copy(matchType = MatchType.OtherMSNETPQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "333333336"))
              case (_, MatchInfractionIds.activeSearchIdIoss) =>
                logger.info("Ioss match found. Active in another MS. GG VRN does not kickout")
                Seq(genericMatch.copy(matchType = MatchType.OtherMSNETPActiveNETP, exclusionStatusCode = None, traderId = "IM3333333333"))
              case (_, MatchInfractionIds.`quarantinedSearchIdIoss`) =>
                logger.info("Ioss match found. Quarantined in another MS. GG VRN does not kickout")
                Seq(genericMatch.copy(matchType = MatchType.OtherMSNETPQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "IM3333333334"))
              case ("SI", "IN7057777123") =>
                logger.info("Intermediary match found. Active in another MS. Previous reg intermediary")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = None, traderId = "IN7057777777"))
              case ("SI", "IN7057777111") =>
                logger.info("Intermediary match found. Excluded in another MS. Previous reg intermediary")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(1), traderId = "IN7057777777"))
              case ("SI", "IN7057777124") =>
                logger.info("Oss match found. Active in another MS. Previous reg Union (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdActiveNETP, exclusionStatusCode = None, traderId = "333333335"))
              case ("SI", "IN7057777125") =>
                logger.info("Ioss match found. Active in another MS. Previous reg IOSS")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdActiveNETP, exclusionStatusCode = None, traderId = "IM3333333333"))
              case ("LV", "IN4287777123") =>
                logger.info("Intermediary match found. Quarantined in another MS. Previous reg intermediary.")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN4287777123"))
              case ("LV", "IN4287777111") =>
                logger.info("Intermediary match found. Quarantined 2 years ago in another MS. Previous reg intermediary.")
                Seq(expiredQuarantineMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN4287777123"))
              case ("LV", "IN4287777124") =>
                logger.info("Oss match found. Quarantined in another MS. Previous reg Union")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "333333335"))
              case ("LV", "IN4287777125") =>
                logger.info("Ioss match found. Quarantined in another MS. Previous reg IOSS")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "IM3333333333"))
              case ("PT", "111222333") =>
                logger.info("Intermediary match found. Active in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = None, traderId = "IN4287777123"))
              case ("PT", "111222334") =>
                logger.info("Oss match found. Active in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentActiveNETP, exclusionStatusCode = None, traderId = "333333335"))
              case ("PT", "111222335") =>
                logger.info("Ioss match found. Active in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentActiveNETP, exclusionStatusCode = None, traderId = "IM3333333333"))
              case ("PT", "123LIS123") =>
                logger.info("Intermediary match found. Active in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = None, traderId = "IN4287777123"))
              case ("PT", "123LIS124") =>
                logger.info("Oss match found. Active in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentActiveNETP, exclusionStatusCode = None, traderId = "333333335"))
              case ("PT", "123LIS125") =>
                logger.info("Ioss match found. Active in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentActiveNETP, exclusionStatusCode = None, traderId = "IM3333333333"))
              case ("LT", "999888777") =>
                logger.info("Intermediary match found. Quarantined in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN4287777123"))
              case ("LT", "999888111") =>
                logger.info("Intermediary match found. Quarantined 2 years ago in another MS. EU details (EU VAT number)")
                Seq(expiredQuarantineMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN4287777123"))
              case ("LT", "999888778") =>
                logger.info("Oss match found. Quarantined in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "333333335"))
              case ("LT", "999888779") =>
                logger.info("Ioss match found. Quarantined in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "IM3333333333"))
              case ("LT", "ABC123123") =>
                logger.info("Intermediary match found. Quarantined in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN4287777123"))
              case ("LT", "ABC123111") =>
                logger.info("Intermediary match found. Quarantined 2 years ago in another MS. EU details (Tax ID number)")
                Seq(expiredQuarantineMatch.copy(matchType = MatchType.PreviousRegistrationFound, exclusionStatusCode = Some(4), traderId = "IN4287777123"))
              case ("LT", "ABC123124") =>
                logger.info("Oss match found. Quarantined in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "333333335"))
              case ("LT", "ABC123125") =>
                logger.info("Ioss match found. Quarantined in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentQuarantinedNETP, exclusionStatusCode = Some(4), traderId = "IM3333333333"))
              case ("AT", "IN0401234567") =>
                logger.info("Match found. TransferringMSID with Non Compliant details.")
                Seq(genericMatch.copy(matchType = MatchType.TransferringMSID, exclusionStatusCode = Some(6), traderId = "IN0401234567", nonCompliantReturns = Some(2), nonCompliantPayments = Some(1)))
              case ("BE", "IN0561234567") =>
                logger.info("Match found. TransferringMSID with Non Compliant details.")
                Seq(genericMatch.copy(matchType = MatchType.TransferringMSID, exclusionStatusCode = Some(6), traderId = "IN0561234567", nonCompliantReturns = Some(1), nonCompliantPayments = Some(2)))
              case _ =>
                Seq.empty
            }

            Future.successful(Ok(Json.toJson(result.copy(searchId = searchId, traderFound = findMatch.nonEmpty, matches = findMatch))))

          case failedResult =>
            logger.error(s"failed core validation request with $failedResult")
            Future.successful(InternalServerError(s"There was an error with the schema $failedResult"))
        }
      }

  }
}
