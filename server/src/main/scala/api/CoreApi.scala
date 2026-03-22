package your_name.project_name.server
package api

import cats.effect.IO
import io.github.sgtswagrid.assetloader.Asset
import scala.NamedTuple.DropNames
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.StatusCode
import sttp.tapir.*

/** These are general endpoints which are used across the entire application. */
object CoreApi:

  /** An endpoint that serves static files from `client/src/main/resources`. */
  val assets
    : PublicEndpoint[(List[String], Option[String]), StatusCode, DropNames[Asset], Any] =
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
   * able to detect when the server has been restarted, at which time the client
   * will proceed by reloading the page.
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
