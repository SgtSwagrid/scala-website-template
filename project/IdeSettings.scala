import sbt._
import sbt.Keys._
import sbtide.Keys._

/** Configuration for IntelliJ via SBT. */
object IdeSettings extends AutoPlugin {

  override def trigger = allRequirements

  /** Setting key for defining an implicit package prefix. */
  lazy val packagePrefix =
    settingKey[String]("For defining an implicit package prefix.")

  override lazy val buildSettings = Seq(
    // Exclude build output and other metadata from IDE indexing and project view:
    ideExcludedDirectories := Seq(
      // Binary output directories:
      file("META-INF"),
      file("target"),
      file(".jvm"),
      file(".js"),
      file("project") / "target",
      file("project") / "project",
      file("server") / "target",
      file("client") / "target",
      file("common") / "target",
      file("common") / ".jvm",
      file("common") / ".js",

      // Library output directories:
      file("sttp"),

      // Build tools:
      file(".metals"),
      file(".bsp"),
      file(".bloop"),

      // Agents:
      file(".playwright-mcp"),
      file(".screenshots"),

      // IDEs:
      file(".idea"),
      file(".vscode"),
    ),
  )

  override lazy val projectSettings = Seq(
    // Apply package prefix to IDE settings for both main and test sources:
    idePackagePrefix := Some(packagePrefix.value),
    ideBasePackages  := Seq(packagePrefix.value),
  )

  override lazy val globalSettings = Seq(
    // Prevent a warning about unknown settings when running in a non-IDE context:
    excludeLintKeys ++= Set(
      idePackagePrefix,
      ideBasePackages,
      ideExcludedDirectories,
    ),
  )
}
