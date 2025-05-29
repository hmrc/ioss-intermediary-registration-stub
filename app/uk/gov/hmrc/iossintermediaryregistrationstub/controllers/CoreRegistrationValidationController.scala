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
import uk.gov.hmrc.iossintermediaryregistrationstub.format.Format.dateFormatter
import uk.gov.hmrc.iossintermediaryregistrationstub.models.core.{CoreRegistrationValidationResult, Match, MatchType, SourceType}
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.{JsonSchemaHelper, SuccessSchema}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

object MatchInfractionIds {
  val activeSearchId = "333333333"
  val quarantinedSearchId = "333333334"
  val transferingMsidId = "IM0123123187"
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
    exclusionDecisionDate = Some("2022-12-11"),
    exclusionEffectiveDate = Some("2023-01-01"),
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
                logger.info("Match found. Active in another MS. GG VRN kickout")
                Seq(genericMatch.copy(matchType = MatchType.OtherMSNETPActiveNETP, traderId = "IN2467777777"))
              case (_, MatchInfractionIds.`quarantinedSearchId`) =>
                logger.info("Match found. Quarantined in another MS. GG VRN kickout")
                Seq(genericMatch.copy(matchType = MatchType.OtherMSNETPQuarantinedNETP, traderId = SourceType.VATNumber.toString))
              case ("SI", "11223344") =>
                logger.info("Match found. Active in another MS. Previous reg Union (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdActiveNETP, traderId = SourceType.EUTraderId.toString))
              case ("SI", "IM7051122334") => // Slovenia
                logger.info("Match found. Active in another MS. Previous reg IOSS")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdActiveNETP, traderId = SourceType.TraderId.toString, memberState = searchIdIssuedBy))
              case ("LV", "11111222222") =>
                logger.info("Match found. Quarantined in another MS. Previous reg Union (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdQuarantinedNETP, traderId = SourceType.EUTraderId.toString))
              case ("LV", "IM4281122334") => // Latvia
                logger.info("Match found. Quarantined in another MS. Previous reg IOSS")
                Seq(genericMatch.copy(matchType = MatchType.TraderIdQuarantinedNETP, traderId = SourceType.TraderId.toString))
              case ("PT", "111222333") =>
                logger.info("Match found. Active in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentActiveNETP, traderId = SourceType.EUTraderId.toString))
              case ("PT", "123LIS123") =>
                logger.info("Match found. Active in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentActiveNETP, traderId = SourceType.EUTraderId.toString))
              case ("LT", "999888777") =>
                logger.info("Match found. Quarantined in another MS. EU details (EU VAT number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentQuarantinedNETP, traderId = SourceType.EUTraderId.toString))
              case ("LT", "ABC123123") =>
                logger.info("Match found. Quarantined in another MS. EU details (Tax ID number)")
                Seq(genericMatch.copy(matchType = MatchType.FixedEstablishmentQuarantinedNETP, traderId = SourceType.EUTraderId.toString))
              case (_, MatchInfractionIds.transferingMsidId) =>
                logger.info("Match found. Transferring from another MSID. Previous reg IOSS")
                Seq(genericMatch.copy(matchType = MatchType.TransferringMSID, traderId = SourceType.TraderId.toString, exclusionEffectiveDate = Some(LocalDate.of(2024, 1, 15).format(dateFormatter))))
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
