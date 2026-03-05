package your_name.project_name.server
package services

import cats.effect.IO
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import your_name.project_name.server.config.Env

/**
 * The base trait for all API services in this application. Each service is a
 * thematic grouping of API endpoint implementations.
 *
 * @param serviceName
 *   The name for this service, used in API documentation.
 */
trait Service(serviceName: String):

  /** The kinds of capabilities that API endpoints in this application have. */
  final type Capabilities = WebSockets & Fs2Streams[IO]

  /** The type of all API endpoints in this application. */
  final type Endpoint = ServerEndpoint[Capabilities, IO]

  /** A collection of all endpoints implemented in this service. */
  def api: List[Endpoint]

  /**
   * Endpoints used to browse API documentation, generated from the spec using
   * [Swagger](https://swagger.io/).
   *
   * @note
   *   Only available in development mode.
   */
  final lazy val docs: List[Endpoint] =
    if Env.DEV_MODE then
      SwaggerInterpreter().fromServerEndpoints(
        api,
        s"${ Env.NAME }-$serviceName",
        Env.VERSION,
      )
    else List.empty

  /**
   * An endpoint that serves metrics about this application, in a format that
   * can be scraped by [Prometheus](https://prometheus.io/).
   */
  final lazy val metrics: Endpoint = PrometheusMetrics
    .default[IO]()
    .metricsEndpoint

  /** A combination of the endpoints from [[api]], [[docs]], and [[metrics]]. */
  final lazy val all: List[Endpoint] = api ++
    (if Env.DEV_MODE then docs else List.empty) :+ metrics
