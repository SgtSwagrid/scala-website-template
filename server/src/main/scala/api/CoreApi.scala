package com.alecdorrington.server
package api

import cats.effect.IO
import com.alecdorrington.server.config.Env
import io.github.sgtswagrid.assetloader.Asset
import io.github.sgtswagrid.assetloader.tapir.AssetService
import scala.NamedTuple.DropNames
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.StatusCode
import sttp.tapir.*

/** These are general endpoints which are used across the entire application. */
object CoreApi:

  private val assetService = new AssetService(
    "assets",
    Env.ASSETS_DIR,
    if Env.DEV_MODE then 0 else 3600,
  )

  /** An endpoint that serves static files from the client's resources. */
  val assets
    : PublicEndpoint[
      (List[String], Option[String]),
      StatusCode,
      DropNames[Asset],
      Any,
    ] = assetService.publicEndpoint

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
