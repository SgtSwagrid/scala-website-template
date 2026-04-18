import sbt._
import sbt.Keys._
import sbt.nio.Keys._

/** Project-wide Scala and Java compiler flags. */
object CompilerSettings extends AutoPlugin {

  override def trigger = allRequirements

  override lazy val buildSettings = Seq(
    javaOptions ++= Seq(
      // Suppress illegal reflective access warnings in the Scala compiler:
      "--enable-native-access=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED",
    ),

    scalacOptions ++= Seq(
       // Give more detailed error messages:
      "-explain",
      "-explain-types",
      "-explain-cyclic",

      // Enable new experimental features:
      "-language:experimental.subCases",
      "-language:experimental.relaxedLambdaSyntax",
      "-language:experimental.multiSpreads",
      "-language:experimental.strictEqualityPatternMatching",
      "-language:experimental.erasedDefinitions",
    ),

    // The Scala version used across all subprojects:
    ThisBuild / scalaVersion := "3.8.3",

    // Automatically reload the server when source changes are detected:
    Global / onChangedBuildSource := ReloadOnSourceChanges,

    // Custom watch messages for hot reloading:
    Global / watchStartMessage     := { (_, _, _) => None },
    Global / watchTriggeredMessage := { (_, file, _) =>
      Some(s"Source changes detected in ${ file.getFileName }. Recompiling...")
    },
  )
}
