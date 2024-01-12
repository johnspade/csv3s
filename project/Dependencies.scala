import sbt._

object Dependencies {
  object V {
    val zio       = "2.0.21"
    val zioParser = "0.1.9"
    val magnolia  = "1.3.4"
  }

  val zio        = "dev.zio"                      %% "zio"          % V.zio
  val zioTest    = "dev.zio"                      %% "zio-test"     % V.zio
  val zioTestSbt = "dev.zio"                      %% "zio-test-sbt" % V.zio
  val zioParser  = "dev.zio"                      %% "zio-parser"   % V.zioParser
  val magnolia   = "com.softwaremill.magnolia1_3" %% "magnolia"     % V.magnolia
}
