package com.alecdorrington.client
package views

import com.raquo.laminar.api.L.render
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExport

/**
  * Base class for all views, using [Laminar](https://laminar.dev/Laminar). Each
  * view corresponds to a distinct page (i.e. at a unique URL) of the website.
  *
  * Every view must be explicitly exported with [[JSExportTopLevel]] so that it
  * appears in `main.js` and can be served to the user. For example:
  * ```scala
  * import scala.scalajs.js.annotation.JSExportTopLevel
  *
  * @JSExportTopLevel("IndexView")
  * object IndexView extends View:
  *   // ...
  * ```
  * The appropriate view for each page is selected based on the name injected
  * into the page template by the server.
  *
  * Override [[content]] to specify the contents of the page. Reactive DOM
  * elements can be imported with:
  * ```scala
  * import com.raquo.laminar.api.L.{*, given}
  * ```
  */
abstract class View:

  /**
    * The contents of this page, specified with reactive DOM elements from
    * Laminar.
    *
    * @example
    *   ```scala
    *   import com.raquo.laminar.api.L.{*, given}
    *
    *   def content = div(
    *     h1("Welcome to my website!"),
    *     p("We're still getting set up here... Stay tuned!"),
    *   )
    *   ```
    */
  protected def content: ReactiveElement.Base

  /**
    * An instruction to render this view inside the root [[div]] of the given
    * [[name]]. This method is side-effecting, and should only be called once
    * per page during initialisation.
    *
    * @param rootName
    *   The name of the root element (default = `"root"`). The contents of this
    *   view will be inserted here.
    */
  @JSExport("show")
  final def show(rootName: String = "root"): Unit =

    val root = document.getElementById("root")
    render(root, content)
