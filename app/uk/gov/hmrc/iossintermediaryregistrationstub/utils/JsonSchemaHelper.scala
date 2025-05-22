/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.iossintermediaryregistrationstub.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import play.api.Logging
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.Results.BadRequest
import play.api.mvc.{Headers, Result}
import uk.gov.hmrc.iossintermediaryregistrationstub.models.core.EisErrorResponse
import uk.gov.hmrc.iossintermediaryregistrationstub.utils.RegistrationHeaderHelper.{InvalidHeader, MissingHeader}

import java.time.Instant
import javax.inject.Inject
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Try}

class JsonSchemaHelper @Inject()() extends Logging {

  private final lazy val jsonMapper = new ObjectMapper()
  private final lazy val jsonFactory = jsonMapper.getFactory

  private def loadRequestSchema(requestSchema: JsValue): JsonSchema = {
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory
    val schemaParser: JsonParser = factory.createParser(requestSchema.toString)
    val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
    JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
  }

  private def validRequest(jsonSchema: JsValue, json: Option[JsValue]): Option[ProcessingReport] = {
    json.map { response =>
      val jsonParser = jsonFactory.createParser(response.toString())
      val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
      loadRequestSchema(jsonSchema).validate(jsonNode)
    }
  }

  def applySchemaValidation(schemaPath: String, jsonBody: Option[JsValue]): SchemaValidationResult = {
    retrieveJsonSchema(schemaPath) match {
      case Success(schema) =>
        val validationResult = validRequest(schema, jsonBody)
        validationResult match {
            case Some(res) if(res.isSuccess) => SuccessSchema
            case Some(res) =>
              logger.error(s"Failed json schema ${res.getExceptionThreshold}")
              res.forEach { test =>
                logger.error(test.getMessage)
              }
              FailedSchema
            case None => NoJsBodyProvided
        }
      case Failure(_) => FailedToFindSchema
    }
  }

  private def retrieveJsonSchema(schemaPath: String): Try[JsValue] = {
    val jsonSchema = Try(Source.fromInputStream(getClass.getResourceAsStream(schemaPath)).mkString)
    jsonSchema.map(Json.parse)
  }

  def applySchemaHeaderValidation(headers: Headers)(f: => Future[Result]): Future[Result] = {

    RegistrationHeaderHelper.validateHeaders(headers.headers) match {
      case Right(_) => f
      case Left(MissingHeader(header)) => Future.successful(BadRequest(Json.toJson(EisErrorResponse(Instant.now(), "OSS_001", s"Bad Request - missing $header"))))
      case Left(InvalidHeader(header)) => Future.successful(BadRequest(Json.toJson(EisErrorResponse(Instant.now(), "OSS_001", s"Bad Request - invalid $header"))))
      case _ => Future.successful(BadRequest(Json.toJson(EisErrorResponse(Instant.now(), "OSS_001", "Bad Request - unknown error"))))

    }
  }

}

sealed trait SchemaValidationResult
case object SuccessSchema extends SchemaValidationResult
case object FailedSchema extends SchemaValidationResult
case object FailedToFindSchema extends SchemaValidationResult
case object NoJsBodyProvided extends SchemaValidationResult