import Dependencies._

val scala3Version = "3.3.3"

name                     := "csv3s"
ThisBuild / organization := "ru.johnspade"
ThisBuild / version      := "0.1.4"

ThisBuild / scalaVersion := scala3Version

ThisBuild / description := "CSV Library for Scala 3"
ThisBuild / homepage    := Some(url("https://github.com/johnspade/csv3s"))
ThisBuild / licenses    := List(("MIT", url("https://opensource.org/licenses/MIT")))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/johnspade/csv3s"), "git@github.com:johnspade/csv3s.git"))
ThisBuild / developers := List(
  Developer(
    "johnspade",
    "Ivan Lopatin",
    "i+csv3s@jspade.dev",
    url("https://github.com/johnspade")
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
    )
  )

addCommandAlias("fmtAll", ";scalafmtAll;scalafmtSbt")
addCommandAlias("validate", ";compile;Test/compile;scalafmtCheckAll;scalafmtSbtCheck;test")
