# Client

The client subproject is compiled to JavaScript by [Scala.js](https://www.scala-js.org/) and delivered to the user's browser.
It uses [Laminar](https://laminar.dev/) for reactive, declarative UI construction.

The entire subproject is bundled into a single file, `main.js`, which is served by the server at `/assets/scripts/main.js`.

## Structure

| Package | Description |
|---------|-------------|
| `views/` | One object per page, each extending [`View`](src/main/scala/views/View.scala). |
| `components/` | Reusable UI components shared across multiple views. |
| `dev/` | Development utilities (e.g. hot reload). Not included in production builds. |

## Adding a View

1. Create an object in `views/` that extends [`View`](src/main/scala/views/View.scala) and implement `content`:
   ```scala
   import com.raquo.laminar.api.L.{*, given}
   import scala.scalajs.js.annotation.JSExportTopLevel

   @JSExportTopLevel("MyView")
   object MyView extends View:
     override protected def content = div(h1("Hello!"))
   ```
2. Serve it from the server by passing the export name to `Template`:
   ```scala
   Template(viewName = "MyView", pageTitle = "My Page")
   ```

The `@JSExportTopLevel` name must match the `viewName` passed to `Template`.

## Hot Reload

In development mode, [`HotReload`](src/main/scala/dev/HotReload.scala) opens a WebSocket connection to the server.
When the connection drops (i.e. the server restarts), the page is automatically reloaded.
This is enabled by the server injecting `HotReload.enable()` into the page template.
