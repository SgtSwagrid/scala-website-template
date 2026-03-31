import sbt._
import sbt.Keys._

/** A collection of custom SBT commands. */
object Commands extends AutoPlugin {

  override def trigger = allRequirements

  override lazy val globalSettings = Seq(
    // sbt build : Compile all subprojects (without running anything).
    commands += Command.command("build")(state => "compile" :: state),

    // sbt dev : Run a development server (with hot reloading).
    commands += Command.command("dev")(state => "~server/reStart" :: state),

    // sbt prod : Run a production server.
    commands += Command.command("prod") { state =>
      "server/moveFullJsTaskKey" :: "server/Compile/run" :: state
    },

    // sbt assemble : Build a self-contained fat JAR at app.jar.
    commands += Command.command("assemble") { state =>
      "server/moveFullJsTaskKey" :: "server/copyAssetsTaskKey" ::
        "server/assembly" :: state
    },

    // sbt lint : Reformat all code according to project standards.
    commands += Command.command("lint")(state => "scalafmtAll" :: state),

    // sbt lint-check : Check that all code is formatted according to project standards.
    commands +=
      Command.command("lint-check")(state => "scalafmtCheckAll" :: state),
  )
}
