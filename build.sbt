Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.perikov"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / publishMavenStyle := true
ThisBuild / scalacOptions ++= Seq(
  "-Wunused:all",
  "-deprecation",
  "-explain",
  "-rewrite",
  "-source",
  "future",
   "-java-output-version", "8",
  "-new-syntax"
)
ThisBuild / publishLocalConfiguration := publishLocalConfiguration.value
  .withOverwrite(true)


ThisBuild / libraryDependencies ++=
  Seq("dsl", "ember-server").map(s =>
    "org.http4s" %% s"http4s-$s" % "0.23.23"
  ) ++
    Seq(
      "org.osgi" % "org.osgi.service.component" % "1.5.1",
      "org.osgi" % "org.osgi.service.component.annotations" % "1.5.1",
      "org.osgi" % "org.osgi.framework" % "1.10.0", 
    )

val api = project
  .in(file("src-api"))
  .enablePlugins(SbtOsgi)
  .settings(
    version := "1.0.0",
    name := "http4s.whiteboard",
    description := "Http Whiteboard for scala/cats/http4s",
    osgiSettings,
    OsgiKeys.importPackage := Seq(
      """org.http4s;version="[0.23.19,1.0)"""",
      """cats.data;version="[2.9.0,3)"""",
      """cats.effect;version="[3.5.0,4)"""",
      "*"
    ),
    OsgiKeys.exportPackage += "com.perikov.osgi.http4s.whiteboard"
  )

val server = project
  .in(file("src-server"))
  .dependsOn(api)
  .enablePlugins(SbtOsgi)
  .settings(
    version := "0.1.0-SNAPSHOT",
    name := "http4s.whiteboard.server",
    description := "Http whiteboard implementation",
    osgiSettings,
    OsgiKeys.privatePackage += "com.perikov.osgi.http4s.whiteboard.server",
    OsgiKeys.importPackage := Seq(
      """org.http4s;org.http4s.syntax;org.http4s.dsl.impl;org.http4s.server;org.http4s.ember.server;org.http4s.dsl;version="[0.23.19,1.0)"""",
      """fs2.io;fs2.io.net;version="[3.7.0,4)"""",
      """com.comcast.ip4s;version="[3.3.0,4)"""",
      """cats;cats.data;cats.syntax;version="[2.9.0,3)"""",
      """scala.runtime;scala.deriving;version="[3.2,4)"""",
      """org.typelevel.log4cats;org.typelevel.log4cats.slf4j;version="[2.6.0,3)"""",
      """cats.effect;cats.effect.kernel;cats.effect.unsafe;version="[3.5.0,4)"""",
      "*"
    )

  )

val http4s_whiteboard =
  project.aggregate(api, server).settings(
    publish / skip := true,
  )
