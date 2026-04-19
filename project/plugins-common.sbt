// SBT plugins common to all of my Scala projects.
// Automatically synchronised from 'https://github.com/SgtSwagrid/scala-config/'.

// Scalafmt is a linter for Scala.
// See 'https://scalameta.org/scalafmt'.
// Configured by '.scalafmt.conf'.
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.0")

// For aggregating Scaladoc across all subprojects into a single site.
addSbtPlugin("com.github.sbt" % "sbt-unidoc" % "0.6.1")

// For allowing SBT to configure IntelliJ project settings.
addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.4")
