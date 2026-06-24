import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.7.0"

  val compile = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"                   %% "domain-play-30" % "13.0.0",
    "com.networknt"                 %  "json-schema-validator"      % "1.5.9",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"      % "2.21.1"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % "test"
  )

}
