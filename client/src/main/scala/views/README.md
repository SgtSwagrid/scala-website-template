# Views

## Show I make a view or a component?

All views and components are made using [Laminar](https://laminar.dev/) and [Airstream](https://github.com/raquo/Airstream).
However, they differ in that:

- Views are top-level constructs, with one per page of the website. They are not designed to be reusable.

- Components are smaller, can be nested, and can be reused in different contexts.
  If you want to make a component, see [components](../components) instead.

## How to make a view

To make a view, create a new file in this directory called `<Name>View.scala`.
Observe the following additional requirements:
- Every view is specified by an object that inherits from `View.scala`.
- Every view must be exported using the `JSExportTopLevel` annotation.
- Every view must override `content` to specify the page contents.
- Typically, you'll want the following import, which provides reactive [DOM](https://developer.mozilla.org/en-US/docs/Web/API/Document_Object_Model) elements from Laminar:
  ```scala
  import com.raquo.laminar.api.L.{*, given}
  ```

#### Example

```scala
import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("MapView")
object MapView extends View:

  def content = div(
    h1("Welcome to my website!"),
    p("We're still getting set up here... Stay tuned!"),
  )
```
