package your_name.project_name.client
package views

import com.raquo.laminar.api.L.*
import scala.scalajs.js.annotation.JSExportTopLevel

/** The view for the index page of the website, at URL `/`. */
@JSExportTopLevel("IndexView")
object IndexView extends View:

  override protected def content = h1(
    "Hello, World!",
    fontSize("32px"),
    textAlign.center,
  )
