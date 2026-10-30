import sbt.*
import sbt.Keys.*

/** Treats warnings as errors where the environment variable `WERROR` is set,
  * as it is by CI's build integrity check, but not elsewhere, such as when
  * publishing, where a warning from Scaladoc must not fail a release.
  *
  * Only the build which sbt was started in is affected, not one included in it
  * by reference, which is held to this by its own repository instead.
  */
object Werror extends AutoPlugin:

  override def trigger = allRequirements

  /** Whether warnings are errors in this build. It is a setting, read as the
    * build loads, so that sbt counts it among the inputs of `scalacOptions`
    * when caching that task, which it would not do for the environment itself.
    */
  lazy val werror = settingKey[Boolean]("Whether warnings are errors.")

  override lazy val buildSettings = Seq(
    werror := sys.env.contains("WERROR") &&
      baseDirectory.value.getCanonicalFile ==
      file(sys.props("user.dir")).getCanonicalFile,
    scalacOptions ++= (if werror.value then Seq("-Werror") else Nil),
  )
