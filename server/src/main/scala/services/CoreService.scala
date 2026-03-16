package your_name.project_name.server
package services

import cats.effect.IO
import sttp.model.StatusCode
import your_name.project_name.server.api.CoreApi
import your_name.project_name.server.api.CoreApi.health
import your_name.project_name.server.assets.AssetLoader
import your_name.project_name.server.config.Env
import your_name.project_name.server.html.Template

/**
  * The implementation of the API endpoints specified in [[CoreApi]]. These are
  * general endpoints which are used across the entire application.
  */
object CoreService extends Service("core"):

  private val assetLoader = new AssetLoader

  /**
    * The `Cache-Control` header value used when serving static assets. In
    * development mode, `no-cache` is used so that the browser always
    * revalidates against the server's ETag, ensuring updated assets are fetched
    * immediately. In production, `public, max-age=3600` allows browsers and
    * intermediary caches to serve assets directly for up to one hour before
    * revalidating.
    */
  private val cacheControl: String =
    if Env.DEV_MODE then "no-cache" else "public, max-age=3600"

  /**
    * An endpoint that serves static files from the client's "resources"
    * directory. Returns `304 Not Modified` if the client's cached ETag matches.
    */
  lazy val assets: Endpoint = CoreApi
    .assets
    .serverLogicPure: (path, ifNoneMatch) =>
      assetLoader.getAsset(path) match
        case None                             => Left(StatusCode.NotFound)
        case Some((bytes, contentType, etag)) =>
          if ifNoneMatch.contains(etag) then Left(StatusCode.NotModified)
          else Right((bytes, contentType, etag, cacheControl))

  /**
    * An endpoint that establishes a websocket connection so that the client is
    * able to detect when the server has been restarted, at which time the
    * client will proceed by reloading the page.
    *
    * @note
    *   Only available in development mode.
    */
  lazy val hotReload: Endpoint = CoreApi
    .hotReload
    .serverLogicSuccessPure: _ =>
      in => in.map(_ => "ok")

  /**
    * A health check endpoint that confirms the server is running and able to
    * accept requests.
    */
  lazy val health: Endpoint = CoreApi
    .health
    .serverLogicSuccessPure: _ =>
      "OK"

  /** Serves the index page of the website. */
  lazy val index: Endpoint = CoreApi
    .index
    .serverLogicSuccessPure: _ =>
      Template(viewName = "IndexView", pageTitle = "My Website")

  lazy val api: List[Endpoint] = List(assets, health, index) ++
    Option.when(Env.DEV_MODE)(hotReload)
