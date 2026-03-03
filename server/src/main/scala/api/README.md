## `server/api/`

Here, we define the specification of the HTTP API as Tapir endpoint values.
This is _only_ the specification; the actual implementations can be found in
[server/services](../services).

The endpoints are split across multiple `*Api.scala` files according to their
purpose, grouped by common theme. See [CoreApi](CoreApi.scala) for a basic
example.
