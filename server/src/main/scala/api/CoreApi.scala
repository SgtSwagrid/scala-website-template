package your_name.project_name.server
package api

import cats.effect.IO
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.StatusCode
import sttp.tapir.*

/** These are general endpoints which are used across the entire application. */
object CoreApi:

  /**
    * An asset is a static file that is served to the client, such as a script
    * or image. It is represented as a tuple of the file's contents as raw
    * bytes, its content type, and its ETag for cache validation.
    */
  type Asset = (Array[Byte], String, String)

  /**
    * An endpoint that serves static files from the client's "resources"
    * directory. Supports conditional GET via the `If-None-Match` request
    * header: if the asset's ETag matches, a `304 Not Modified` response is
    * returned with no body.
    */
  val assets
    : PublicEndpoint[(List[String], Option[String]), StatusCode, (Array[Byte], String, String, String), Any] =
    endpoint
      .get
      .in("assets")
      .in(paths)
      .in(header[Option[String]]("If-None-Match"))
      .errorOut(statusCode)
      .out(byteArrayBody)
      .out(header[String]("Content-Type"))
      .out(header[String]("ETag"))
      .out(header[String]("Cache-Control"))

  /**
    * An endpoint that establishes a websocket connection so that the client is
    * able to detect when the server has been restarted, at which time the
    * client will proceed by reloading the page.
    *
    * @note
    *   Only available in development mode.
    */
  val hotReload = endpoint
    .get
    .in("hot-reload")
    .out(
      webSocketBody[
        String,
        CodecFormat.TextPlain,
        String,
        CodecFormat.TextPlain,
      ](Fs2Streams[IO]),
    )

  /**
    * A health check endpoint that confirms the server is running and able to
    * accept requests.
    */
  val health = endpoint.get.in("health").out(stringBody)

  /** Serves the index page of the website. */
  val index = endpoint.get.in("").out(htmlBodyUtf8)
