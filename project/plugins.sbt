// External plugins for SBT:

// For transpilation into JavaScript.
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.20.2")

// For cross-compilation into JVM/JS from the same subproject.
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")

// For hot reloading.
addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")

// For IntelliJ integration.
addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.2")

// For automatic code reformatting.
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.6")

// For packaging the server as a self-contained fat JAR.
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.1")
