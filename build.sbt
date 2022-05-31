import Dependencies._

val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "csv3s",
    organization := "ru.johnspade",
    version      := "0.1.0",
    description  := "CSV Library for Scala 3",
    homepage     := Some(url("https://github.com/johnspade/csv3s")),
    licenses     := List(("MIT", url("https://opensource.org/licenses/MIT"))),
    scmInfo      := Some(ScmInfo(url("https://github.com/johnspade/csv3s"), "git@github.com:johnspade/csv3s.git")),
    developers := List(
      Developer(
        "johnspade",
        "Ivan Lopatin",
        "ivan+csv3s@ilopatin.ru",
        url("https://about.johnspade.ru")
      )
    ),
    scalaVersion           := scala3Version,
    publishMavenStyle      := true,
    publishTo              := sonatypePublishToBundle.value,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    libraryDependencies ++= Seq(
      zio,
      zioTest    % Test,
      zioTestSbt % Test,
      zioParser,
      magnolia
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
