package your_name.project_name.server
package config

import java.nio.file.{Path, Paths}

/**
  * The application environment, populated from JVM system properties and
  * environment variables alike.
  */
object Env:

  /** The name of the application. */
  val NAME: String = jvm("app.name").get

  /** The version of the application. */
  val VERSION: String = jvm("app.version").get

  /** The root directory from which static assets are served. */
  val ASSETS_DIR: Path = jvm("assets.dir").map(Paths.get(_)).get

  /** Whether the server is running in development mode. */
  val DEV_MODE: Boolean = jvm("dev.mode")
    .flatMap(_.toBooleanOption)
    .getOrElse(false)

  /** The host the server binds to. */
  val HOST: String = env("HOST").getOrElse("localhost")

  /** The port the server listens on. */
  val PORT: Int = env("PORT").flatMap(_.toIntOption).getOrElse(8080)

  /**
    * Retrieves the value of a JVM system property, if it exists and is
    * non-empty.
    */
  private def jvm(name: String): Option[String] =
    Option(System.getProperty(name)).filter(_.nonEmpty)

  /**
    * Retrieves the value of an environment variable, if it exists and is
    * non-empty.
    */
  private def env(name: String): Option[String] = sys
    .env
    .get(name)
    .filter(_.nonEmpty)
