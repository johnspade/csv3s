import Dependencies._

val scala3Version = "3.1.0"

name                     := "csv3s"
ThisBuild / organization := "ru.johnspade"
ThisBuild / version      := "0.1.0"

ThisBuild / scalaVersion := scala3Version

ThisBuild / description := "CSV Library for Scala 3"
ThisBuild / homepage    := Some(url("https://github.com/johnspade/csv3s"))
ThisBuild / licenses    := List(("MIT", url("https://opensource.org/licenses/MIT")))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/johnspade/csv3s"), "git@github.com:johnspade/csv3s.git"))
ThisBuild / developers := List(
  Developer(
    "johnspade",
    "Ivan Lopatin",
    "ivan+csv3s@ilopatin.ru",
    url("https://about.johnspade.ru")
  )
)
ThisBuild / publishMavenStyle      := true
ThisBuild / publishTo              := sonatypePublishToBundle.value
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

lazy val root = project
  .in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      zio,
      zioTest    % Test,
      zioTestSbt % Test,
      zioParser,
      magnolia
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
