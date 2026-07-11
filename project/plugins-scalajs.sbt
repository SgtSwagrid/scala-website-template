// SBT plugins common to all of my Scala.js projects.
// Automatically synchronised from 'https://github.com/SgtSwagrid/scala-website-config/'.

// For transpilation into JavaScript.
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.22.0")

// Cross-compilation into JVM/JS from the same subproject is provided
// natively by sbt 2 via 'projectMatrix' (no plugin required).

// For hot reloading.
// Fork of 'io.spray:sbt-revolver', which does not support sbt 2.
addSbtPlugin("com.indoorvivants" % "sbt-revolver" % "0.11.2")

// For packaging the server as a self-contained fat JAR.
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.1")
