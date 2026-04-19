import sbtunidoc.BaseUnidocPlugin.autoImport.*
import sbtunidoc.ScalaUnidocPlugin

ThisBuild / name         := "project-name"
ThisBuild / organization := "com.alecdorrington"
ThisBuild / version      := "0.1.1-SNAPSHOT"

lazy val server = Subprojects.server
lazy val client = Subprojects.client
lazy val common = Subprojects.common

lazy val `scala-website-template`: Project = project
  .in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .aggregate(server, client, common.jvm, common.js)
  .settings(
    Compile / run / skip := true,
    run                  := (server / Compile / run).evaluated,
    ScalaUnidoc / unidoc / scalacOptions ++=
      Seq("-project", "Scala Website Template"),
  )
