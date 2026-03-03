package your_name.project_name.client
package dev

import org.scalajs.dom
import org.scalajs.dom.WebSocket
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("HotReload")
object HotReload:

  /**
   * Initialise automatic reloading. When the server is restarted (detected
   * through WebSocket disconnection), the client will repeatedly attempt to
   * reconnect until it is successful. For use during development.
   */
  @JSExport
  def enable(): Unit =

    val loc      = dom.window.location
    val protocol = if loc.protocol == "https:" then "wss" else "ws"
    val url      = s"$protocol://${ loc.host }/hot-reload"

    def listen(): Unit = WebSocket(url).onclose = _ =>
      dom.window.setTimeout(() => listen(), 2000)
      loc.reload()

    listen()
