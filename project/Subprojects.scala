import Assets.*
import IdeSettings.packagePrefix
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.*
import sbt.*
import sbt.Keys.*
import sbtassembly.AssemblyPlugin.autoImport.*
import sbtcrossproject.CrossPlugin.autoImport.*
import sbtcrossproject.CrossProject
import scalajscrossproject.ScalaJSCrossPlugin.autoImport.*
import spray.revolver.RevolverPlugin.autoImport.*
import sbtunidoc.BaseUnidocPlugin.autoImport.*
import sbtunidoc.ScalaUnidocPlugin

/** The collection of all subprojects that make up this project. */
object Subprojects {

  /** The base package prefix shared across all subprojects. */
  private val projectRoot = "com.alecdorrington"

  /** The root subproject encapsulates all other subprojects. */
  lazy val root: Project = project
    .in(file("."))
    .enablePlugins(ScalaUnidocPlugin)
    .aggregate(server, client, common.jvm, common.js)
    .settings(
      packagePrefix        := projectRoot,
      Compile / run / skip := true,
      run                  := (server / Compile / run).evaluated,
    )

  /** The server subproject, responsible for persistence and HTTP requests. */
  lazy val server: Project = project
    .in(file("server"))
    .dependsOn(client, common.jvm)
    .settings(
      name                 := s"${ (ThisBuild / name).value }-server",
      packagePrefix        := s"$projectRoot.server",
      Compile / mainClass  := Some(s"$projectRoot.server.Main"),
      Compile / run / fork := true,

      // Server dependencies:
      Dependencies.tapirCommon,
      Dependencies.tapirServer,
      Dependencies.assetLoader,
      Dependencies.logging,
      Dependencies.fs2,
      Dependencies.circe,
      Dependencies.cats,
      Dependencies.munitCatsEffect,

      // Fat JAR assembly settings (used by `sbt assemble`):
      assembly / assemblyOutputPath    := file("app.jar"),
      assembly / assemblyMergeStrategy := {
        case PathList("META-INF", "services", _*) => MergeStrategy.concat
        case PathList("META-INF", _*)             => MergeStrategy.discard
        case "module-info.class"                  => MergeStrategy.discard
        case "reference.conf"                     => MergeStrategy.concat
        case _                                    => MergeStrategy.first
      },

      // Copy Scala.js output and client resources into server managed resources:
      copyFastJsTaskKey := copyFastJsTask.value,
      copyFullJsTaskKey := {
        deleteSourcesTask.value
        copyFullJsTask.value
      },
      copyAssetsTaskKey  := copyAssetsTask.value,
      copySourcesTaskKey := copySourcesTask.value,

      Compile / run := (Compile / run).dependsOn(copyAssetsTaskKey).evaluated,

      javaOptions ++= Seq(
        setProperty("app.name", (ThisBuild / name).value),
        setProperty(
          "app.version",
          (ThisBuild / version).value,
        ),
        setProperty(
          "assets.dir",
          (Compile / resourceManaged).value / "assets",
        ),
      ),

      // Enable hot reload:
      reStart :=
        (reStart dependsOn
          (copyFastJsTaskKey, copyAssetsTaskKey, copySourcesTaskKey)).evaluated,
      reStart / javaOptions ++= Seq(setProperty("dev.mode", true)),

      // Watch for source changes in all subprojects:
      Compile / watchSources ++= (client / Compile / sources).value,
      Compile / watchSources ++= (common.jvm / Compile / sources).value,
      Compile / watchSources ++= (common.js / Compile / sources).value,
    )

  /** The client subproject, responsible for rendering and user input. */
  lazy val client: Project = project
    .in(file("client"))
    .dependsOn(common.js)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name          := s"${ (ThisBuild / name).value }-client",
      packagePrefix := s"$projectRoot.client",

      // Client dependencies:
      Dependencies.scalajs,
      Dependencies.laminar,
      Dependencies.circe,
      Dependencies.cats,
      Dependencies.munitCatsEffect,

      // Enable source maps in dev builds, with paths relativised to the
      // project root so the browser can fetch them from the dev server:
      Compile / fastLinkJS / scalaJSLinkerConfig :=
        scalaJSLinkerConfig
          .value
          .withSourceMap(true)
          .withRelativizeSourceMapBase(Some(
            (ThisBuild / baseDirectory).value.toURI,
          )),
    )

  /** The common subproject, with code that is shared between client and server. */
  lazy val common: CrossProject = crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("common"))
    .settings(
      name          := s"${ (ThisBuild / name).value }-common",
      packagePrefix := s"$projectRoot.common",

      // Common dependencies:
      Dependencies.circe,
      Dependencies.cats,
      Dependencies.munitCatsEffect,
    )

  /**
    * Helper method for setting a Java system property from an SBT setting.
    *
    * @param key
    *   The name of the system property to set.
    *
    * @param obj
    *   The value to set the system property to (converted to a string).
    *
    * @return
    *   A JVM command-line argument that sets the system property.
    */
  private def setProperty(key: String, obj: Any): String =
    s"-D$key=${ obj.toString }"
}
