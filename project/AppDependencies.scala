import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.18.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "domain-play-30" % "10.0.0",
    "com.github.fge" % "json-schema-validator" % "2.2.14"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % "test"
  )

}
