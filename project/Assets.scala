import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbt.io.Path

/** Tasks for copying Scala.js output and client resources into server assets. */
object Assets {

  /** Task for copying the (unoptimised) compiled client into the server's build. */
  val copyFastJsTask = Def.task {
    (Subprojects.client / Compile / fastLinkJS).value
    copyFiles(
      from =
        (Subprojects.client / Compile / fastLinkJS /
          scalaJSLinkerOutputDirectory).value,
      to = (Compile / resourceManaged).value / "assets" / "scripts",
      cache = streams.value.cacheDirectory / "scalajs-fastopt",
    )
  }

  /** Sbt task key for [[copyFastJsTask]]. */
  lazy val copyFastJsTaskKey =
    taskKey[Set[File]]("Copy unoptimised Scala.js output into server assets.")

  /** Task for copying the (optimised) compiled client into the server's build. */
  val copyFullJsTask = Def.task {
    (Subprojects.client / Compile / fullLinkJS).value
    copyFiles(
      from =
        (Subprojects.client / Compile / fullLinkJS /
          scalaJSLinkerOutputDirectory).value,
      to = (Compile / resourceManaged).value / "assets" / "scripts",
      cache = streams.value.cacheDirectory / "scalajs-fullopt",
    )
  }

  /** Sbt task key for [[copyFullJsTask]]. */
  lazy val copyFullJsTaskKey =
    taskKey[Set[File]]("Copy optimised Scala.js output into server assets.")

  /** Task for copying client assets into the server's build. */
  val copyAssetsTask = Def.task {
    copyFiles(
      from = (Subprojects.client / Compile / resourceDirectory).value,
      to = (Compile / resourceManaged).value / "assets",
      cache = streams.value.cacheDirectory / "assets",
    )
  }

  /** Sbt task key for [[copyAssetsTask]]. */
  lazy val copyAssetsTaskKey =
    taskKey[Set[File]]("Copy client resources into server assets.")

  /**
   * Task for copying Scala source files alongside the compiled JS output so
   * that the browser can fetch them when resolving source maps in dev mode.
   * Sources are placed under `assets/scripts/` to match the relative paths
   * embedded in the source map (which are relativised to the project root).
   */
  val copySourcesTask = Def.task {
    val base = (ThisBuild / baseDirectory).value
    val dest = (Compile / resourceManaged).value / "assets" / "scripts"
    copyFiles(
      from = base / "client" / "src" / "main" / "scala",
      to = dest / "client" / "src" / "main" / "scala",
      cache = streams.value.cacheDirectory / "sources-client",
    ) ++ copyFiles(
      from = base / "common" / "src" / "main" / "scala",
      to = dest / "common" / "src" / "main" / "scala",
      cache = streams.value.cacheDirectory / "sources-common",
    )
  }

  /** Sbt task key for [[copySourcesTask]]. */
  lazy val copySourcesTaskKey = taskKey[Set[File]](
    "Copy Scala sources into server dev assets for source maps.",
  )

  /**
   * Task for deleting any Scala source files that may have been copied into the
   * server's managed assets during a prior dev build. This ensures that source
   * files are never included in a production build.
   */
  val deleteSourcesTask = Def.task {
    val dest = (Compile / resourceManaged).value / "assets" / "scripts"
    IO.delete(dest / "client")
    IO.delete(dest / "common")
  }

  /**
   * Copy files from one directory to another, only copying files that have
   * changed.
   *
   * @param from
   *   The directory to copy files from. All files in this directory and its
   *   subdirectories will be copied.
   *
   * @param to
   *   The directory to copy files to. The directory structure of the source
   *   will be preserved in the destination.
   *
   * @param cache
   *   A directory to use for caching file hashes, so that only changed files
   *   are copied. By convention, a subdirectory of
   *   `streams.value.cacheDirectory` should be used, although the exact
   *   location is not important so long as it is consistent and unique.
   */
  def copyFiles(from: File, to: File, cache: File): Set[File] = {

    // The collection of files to copy.
    val files = (from ** "*").get.filter(_.isFile).toSet

    // Only copy files that haven't changed.
    FileFunction.cached(cache, FilesInfo.hash) { files =>

      val (pairs, copies) = files
        .map { file =>
          val copy = to / Path.relativeTo(from)(file).get
          IO.createDirectory(copy.getParentFile)
          (file -> copy, copy)
        }
        .unzip

      IO.delete(copies)
      IO.copy(pairs)
      copies
    }(files)
  }
}
