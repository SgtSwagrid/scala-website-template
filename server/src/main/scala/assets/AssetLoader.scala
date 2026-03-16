package your_name.project_name.server
package assets

import java.nio.file.Files
import java.security.MessageDigest
import scala.jdk.CollectionConverters.*
import your_name.project_name.server.api.CoreApi.Asset
import your_name.project_name.server.config.Env

/** A utility for loading static assets so that they can be served. */
class AssetLoader:

  /**
    * Retrieves the asset corresponding to the given path, if it exists.
    *
    * @param path
    *   The path to the asset, represented as a list of "/"-separated
    *   components. The path should be given relative to the resources direction
    *   in `client/src/main/resources`.
    *
    * @return
    *   An asset, if it exists, represented as a tuple of the file's raw bytes
    *   and its content type.
    *
    * @note
    *   The results are cached in memory, so subsequent calls won't incur
    *   additional file system overhead.
    *
    * @note
    *   The content type is determined based on the file extension.
    */
  def getAsset(path: List[String]): Option[Asset] =
    assets.get(path.mkString("/"))

  /**
    * A collection of all assets, keyed by relative path. This is loaded lazily
    * on the first call to [[getAsset]], at which point all assets are loaded at
    * once.
    */
  private lazy val assets: Map[String, Asset] = Files
    .walk(Env.ASSETS_DIR)
    .iterator
    .asScala
    .filter(Files.isRegularFile(_))
    .map: file =>
      val bytes = Files.readAllBytes(file)
      val path  = Env.ASSETS_DIR.relativize(file).toString.replace("\\", "/")
      path -> (bytes, contentType(path), etag(bytes))
    .toMap

  /**
    * Computes a strong ETag for the given bytes using an MD5 hash. The result
    * is a quoted hexadecimal string, as required by the HTTP specification.
    *
    * @param bytes
    *   The raw file contents to hash.
    *
    * @return
    *   An ETag value as a quoted hex string (e.g. `"d41d8cd98f00b204..."`).
    */
  private def etag(bytes: Array[Byte]): String =
    val hash = MessageDigest.getInstance("MD5").digest(bytes)
    hash.map("%02x".format(_)).mkString("\"", "", "\"")

  /**
    * Determines the content type based on the file extension. The content type
    * is used to set the "Content-Type" header when serving the asset to the
    * client, which helps the browser understand how to handle the file.
    *
    * @param path
    *   The file name, optionally including the full path.
    *
    * @return
    *   The content type as a string, including the charset for text files. If
    *   the file extension is unrecognised, returns "application/octet-stream"
    *   by default.
    */
  private def contentType(path: String): String =
    path.toLowerCase.split("\\.").lastOption.getOrElse("") match

      // Code files:
      case "html" | "htm" => "text/html; charset=utf-8"
      case "css"          => "text/css; charset=utf-8"
      case "js"           => "text/javascript; charset=utf-8"
      case "map"          => "application/json; charset=utf-8"
      case "scala"        => "text/plain; charset=utf-8"
      case "glsl"         => "text/plain; charset=utf-8"

      // Image files:
      case "svg"          => "image/svg+xml"
      case "png"          => "image/png"
      case "jpg" | "jpeg" => "image/jpeg"
      case "gif"          => "image/gif"

      // Everything else:
      case _ => "application/octet-stream"
