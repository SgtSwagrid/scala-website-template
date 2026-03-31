import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._
import sbt.Keys._

/** External library dependencies. */
object Dependencies {

  /** The version to use for each dependency. */
  lazy val V = new {
    val tapir           = "1.13.14"
    val slf4j           = "2.0.17"
    val logback         = "1.5.32"
    val fs2             = "3.13.0"
    val assetLoader     = "0.1.6"
    val scalajs         = "2.8.1"
    val laminar         = "17.0.0"
    val laminext        = "0.17.0"
    val circe           = "0.14.15"
    val cats            = "2.13.0"
    val catsEffect      = "3.7.0"
    val catsMtl         = "1.6.0"
    val catsCollections = "0.9.10"
    val munit           = "1.0.3"
    val munitCatsEffect = "2.0.0"
  }

  /**
    * Core Tapir dependencies for defining API endpoints. Cross-compiled for
    * both JVM and Scala.js; safe to use in the common cross-project.
    */
  lazy val tapirCommon = libraryDependencies ++= Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"       % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % V.tapir,
  )

  /**
    * Server-side Tapir dependencies that require JVM capabilities. Must not be
    * added to the common cross-project as they have no Scala.js artifacts.
    */
  lazy val tapirServer = libraryDependencies ++= Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-netty-server-cats"  % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-client4"       % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server"   % V.tapir % Test,
  )

  lazy val assetLoader = libraryDependencies ++= Seq(
    "io.github.sgtswagrid" %% "asset-loader"       % V.assetLoader,
    "io.github.sgtswagrid" %% "asset-loader-tapir" % V.assetLoader,
  )

  /** Library dependencies for logging. */
  lazy val logging = libraryDependencies ++= Seq(
    "org.slf4j"      % "slf4j-api"       % V.slf4j,
    "ch.qos.logback" % "logback-classic" % V.logback,
  )

  /** Library dependencies associated with FS2, for stream processing. */
  lazy val fs2 = libraryDependencies ++= Seq(
    "co.fs2" %% "fs2-core" % V.fs2,
    "co.fs2" %% "fs2-io"   % V.fs2,
  )

  /** Library dependencies associated with Scala.js, for JS interop. */
  lazy val scalajs = libraryDependencies ++=
    Seq("org.scala-js" %%% "scalajs-dom" % V.scalajs)

  /** Library dependencies associated with Laminar, for client-side rendering. */
  lazy val laminar = libraryDependencies ++= Seq(
    "com.raquo"   %%% "laminar"         % V.laminar,
    "com.raquo"   %%% "airstream"       % V.laminar,
    "io.laminext" %%% "core"            % V.laminext,
    "io.laminext" %%% "fetch"           % V.laminext,
    "io.laminext" %%% "fetch-circe"     % V.laminext,
    "io.laminext" %%% "websocket"       % V.laminext,
    "io.laminext" %%% "websocket-circe" % V.laminext,
  )

  /** Library dependencies associated with Circe, for JSON parsing. */
  lazy val circe = libraryDependencies ++= Seq(
    "io.circe" %% "circe-core"    % V.circe,
    "io.circe" %% "circe-generic" % V.circe,
    "io.circe" %% "circe-parser"  % V.circe,
  )

  /** Library dependencies associated with cats, for FP abstractions. */
  lazy val cats = libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-kernel"           % V.cats,
    "org.typelevel" %% "cats-core"             % V.cats,
    "org.typelevel" %% "algebra"               % V.cats,
    "org.typelevel" %% "cats-effect"           % V.catsEffect,
    "org.typelevel" %% "cats-mtl"              % V.catsMtl,
    "org.typelevel" %% "cats-collections-core" % V.catsCollections,
  )

  /** Library dependencies for testing with MUnit and Cats Effect. */
  lazy val munitCatsEffect = libraryDependencies ++=
    Seq("org.typelevel" %%% "munit-cats-effect" % V.munitCatsEffect % Test)
}
