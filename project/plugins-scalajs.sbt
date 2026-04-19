// SBT plugins common to all of my Scala.js projects.
// Automatically synchronised from 'https://github.com/SgtSwagrid/scala-website-config/'.

// For transpilation into JavaScript.
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.21.0")

// For cross-compilation into JVM/JS from the same subproject.
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

// For hot reloading.
addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")

// For packaging the server as a self-contained fat JAR.
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.1")
