/*
 * The project consists of three subprojects: server, client, and common.
 * These are defined in 'project/Subprojects.scala'.
 */

import sbt.*
import sbt.Keys.*

ThisBuild / name         := "project-name"
ThisBuild / organization := "com.alecdorrington"
ThisBuild / version      := "0.1.1-SNAPSHOT"

lazy val root   = Subprojects.root
lazy val server = Subprojects.server
lazy val client = Subprojects.client
lazy val common = Subprojects.common
