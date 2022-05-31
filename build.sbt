import Dependencies._

val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name              := "csv3s",
    organization      := "ru.johnspade",
    version           := "0.1.0-SNAPSHOT",
    scalaVersion      := scala3Version,
    publishMavenStyle := true,
    libraryDependencies ++= Seq(
      zio,
      zioTest    % Test,
      zioTestSbt % Test,
      zioParser,
      magnolia
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
