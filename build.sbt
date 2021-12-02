/*
 * Copyright 2021 HM Revenue & Customs
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
import play.sbt.routes.RoutesKeys
import sbt.Keys.{javaOptions, retrieveManaged, scalaVersion}
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

RoutesKeys.routesImport := Seq.empty

val appName = "vat-subscription"

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    ".*Reverse.*",
    ".*standardError*.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    "config.*",
    "testOnlyDoNotUseInAppConf.*",
    "testonly.*"
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val appDependencies: Seq[ModuleID] = compile ++ test()

val compile = Seq(
  ws,
  "uk.gov.hmrc"       %% "bootstrap-backend-play-28"  % "5.16.0",
  "org.typelevel"     %% "cats-core"                  % "1.6.0",
  "com.typesafe.play" %% "play-json-joda"             % "2.9.2"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "org.scalatest"           %% "scalatest"                  % "3.1.4"             % scope,
  "org.pegdown"             % "pegdown"                     % "1.6.0"             % scope,
  "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0"             % scope,
  "com.typesafe.play"       %% "play-test"                  % PlayVersion.current % scope,
  "com.github.tomakehurst"  % "wiremock-jre8"               % "2.26.3"            % scope,
  "com.vladsch.flexmark"    % "flexmark-all"                % "0.36.8"            % scope,
  "org.scalatestplus"       %% "mockito-3-3"                % "3.1.2.0"           % scope
)

lazy val root = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(coverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    scalaVersion := "2.12.14",
    majorVersion := 0,
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    PlayKeys.playDefaultPort := 9567
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := true,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value,
    javaOptions in IntegrationTest += "-Dlogger.resource=logback-test.xml",
    addTestReportOption(IntegrationTest, "int-test-reports"),
    parallelExecution in IntegrationTest := false
  )
