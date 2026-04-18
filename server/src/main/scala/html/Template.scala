package com.alecdorrington.server
package html

import com.alecdorrington.server.config.Env

/**
  * A simple HTML template common to every page of this application. Loads the
  * necessary style sheets and scripts. Different pages differ only in that a
  * different view initialisation function is called. This is injected by name
  * as a string, and the corresponding view is loaded from the `client`
  * subproject.
  */
object Template:

  /**
    * @param viewName
    *   The name of the view to load. Include only the name, with no package
    *   prefix and no parentheses (e.g. `IndexView`). In order to be visible
    *   here, a view must be exported from the `client` subproject using the
    *   `@JSExportTopLevel` annotation, and the name used in that annotation
    *   must match the name passed here.
    *
    * @param pageTitle
    *   The title of the page, which is displayed in the browser tab.
    *
    * @param hotReload
    *   Determines whether hot reload is enabled on the client, which allows the
    *   client to automatically restart when the server does.
    */
  def apply
    (
      viewName: String,
      pageTitle: String = "",
      hotReload: Boolean = Env.DEV_MODE,
    )
    : String = s"""
    <!DOCTYPE HTML>

    <HTML lang="en">
      <HEAD>
        <TITLE>$pageTitle</TITLE>
        <META charset="UTF-8">
        <META name="viewport" content="width=device-width, initial-scale=1.0">
        <SCRIPT type="text/javascript" src="/assets/scripts/main.js"></SCRIPT>
        <LINK rel="icon" type="image/x-icon" href="/assets/img/icon.png">
      </HEAD>
      <BODY style="height: 100vh">
        <DIV id="root" style="width: 100%; height: 100%; position: absolute;"></DIV>
        <SCRIPT type="text/javascript">
          $viewName.show("root");
          ${ if hotReload then "HotReload.enable();" else "" }
        </SCRIPT>
      </BODY>
    </HTML>
  """
