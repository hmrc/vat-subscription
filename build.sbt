/*
 * Copyright 2020 HM Revenue & Customs
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

import play.core.PlayVersion
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, defaultSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

name := "vat-subscription"

lazy val root = (project in file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    "Reverse.*",
    ".*standardError*.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    "uk.gov.hmrc.vatsubscription.config.*",
    "testOnlyDoNotUseInAppConf.*",
    "uk.gov.hmrc.vatsubscription.testonly.*")

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimum := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val appDependencies: Seq[ModuleID] = compile ++ test()

val compile = Seq(
  ws,
  "uk.gov.hmrc" %% "bootstrap-play-26" % "1.14.0",
  "org.typelevel" %% "cats-core" % "1.6.0",
  "com.typesafe.play" %% "play-json-joda" % "2.6.14"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.9.0-play-26" % scope,
  "org.scalatest" %% "scalatest" % "3.0.8" % scope,
  "org.pegdown" % "pegdown" % "1.6.0" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % scope,
  "com.github.tomakehurst" % "wiremock-jre8" % "2.25.1" % scope,
  "org.mockito" % "mockito-core" % "3.2.0" % scope
)

coverageSettings
scalaSettings
publishingSettings
defaultSettings()
majorVersion := 0

libraryDependencies ++= appDependencies
retrieveManaged := true
evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)

Keys.fork in Test := true
javaOptions in Test += "-Dlogger.resource=logback-test.xml"
parallelExecution in Test := true

Keys.fork in IntegrationTest := true
unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value
javaOptions in IntegrationTest += "-Dlogger.resource=logback-test.xml"
addTestReportOption(IntegrationTest, "int-test-reports")
parallelExecution in IntegrationTest := false
PlayKeys.playDefaultPort := 9567

resolvers ++= Seq(
  Resolver.bintrayRepo("hmrc", "releases"),
  Resolver.jcenterRepo
)

scalacOptions += "-Ypartial-unification"
