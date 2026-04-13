import sbt._
import sbt.Keys._
import sbtide.Keys._

/** Configuration for IntelliJ via SBT. */
object IdeSettings extends AutoPlugin {

  override def trigger = allRequirements

  /** Setting key for defining an implicit IDE package prefix. */
  lazy val packagePrefix =
    settingKey[String]("For defining an implicit IDE package prefix.")

  override lazy val buildSettings = Seq(
    packagePrefix := "",

    // Exclude build output and other metadata from IDE indexing and project view.
    ideExcludedDirectories := {

      // Excluded in any subdirectory:
      val recursive = Seq("target", ".js", ".jvm").flatMap(name =>
        (baseDirectory.value ** name).get,
      )

      // Excluded at the root level only:
      val rootOnly = Seq(
        // Build tools
        file(".metals"),
        file(".bsp"),
        file(".bloop"),
        file(".jvmopts"),
        file("project/project"),

        // IDEs
        file(".idea"),
        file(".vscode"),
        file(".devcontainer"),
        file(".editorconfig"),
        file(".scalafmt.conf"),

        // Git
        file(".gitignore"),
        file(".gitattributes"),
        file(".github"),
        file("LICENSE.md"),

        // Agents
        file("CLAUDE.md"),
        file(".claude"),
        file(".mcp.json"),
        file(".playwright.mcp"),
        file(".scala-steward.conf"),
      )

      recursive ++ rootOnly
    },
  )

  override lazy val projectSettings = Seq(
    // Apply package prefix to IDE settings for both main and test sources:
    idePackagePrefix := Some(packagePrefix.value).filter(_.nonEmpty),
    ideBasePackages  := Seq(packagePrefix.value).filter(_.nonEmpty),
  )

  override lazy val globalSettings = Seq(
    // Prevent a warning about unused setting keys:
    excludeLintKeys ++= Set(
      packagePrefix,
      idePackagePrefix,
      ideBasePackages,
      ideExcludedDirectories,
    ),
  )
}
