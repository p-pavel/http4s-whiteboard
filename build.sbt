Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.perikov"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / publishMavenStyle := true
ThisBuild / scalacOptions ++= Seq(
  "-Wunused:all",
  "-deprecation",
  "-explain"
)

val http4s_whiteboard =
  project
    .in(file("."))
    .enablePlugins(SbtOsgi)
    .settings(
      name := "http4s.whiteboard",
      version := "0.1.0-SNAPSHOT",
      publishLocalConfiguration := publishLocalConfiguration.value
        .withOverwrite(true),
      publishConfiguration := publishConfiguration.value.withOverwrite(true),
      osgiSettings,
      // OsgiKeys.bundleSymbolicName := "http4s.whiteboard",
      OsgiKeys.failOnUndecidedPackage := true,
      OsgiKeys.exportPackage := Seq("com.perikov.osgi.http4s.whiteboard"),
      OsgiKeys.privatePackage := Seq(
        "com.perikov.osgi.http4s.whiteboard.server"
      ),
      OsgiKeys.importPackage := Seq(
        """org.http4s;org.http4s.syntax;org.http4s.dsl.impl;org.http4s.server;org.http4s.ember.server;org.http4s.dsl;version="[0.23.19,1.0)"""",
        """fs2.io;fs2.io.net;version="[3.7.0,4)"""",
        """com.comcast.ip4s;version="[3.3.0,4)"""",
        """cats;cats.data;cats.syntax;version="[2.9.0,3)"""",
        """scala.runtime;version="[3.2,4)"""",
        """org.typelevel.log4cats;org.typelevel.log4cats.slf4j;version="[2.6.0,3)"""",
        """cats.effect;cats.effect.kernel;cats.effect.unsafe;version="[3.5.0,4)"""",
        "*"
      ),
      libraryDependencies ++= Seq("dsl", "ember-server")
        .map(s => "org.http4s" %% s"http4s-$s" % "0.23.23"),
      libraryDependencies += "com.outr" %% "scribe-slf4j" % "3.12.2",
      libraryDependencies ++= Seq(
        "org.osgi" % "org.osgi.service.component" % "1.5.1",
        "org.osgi" % "org.osgi.service.log" % "1.4.0",
        "org.osgi" % "org.osgi.service.component.annotations" % "1.5.1"
      )
    )
