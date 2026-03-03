# Common

The common subproject contains code that is shared between `server` and `client`.
It is [cross-compiled](https://www.scala-js.org/doc/project/cross-build.html) - once to JVM bytecode for the server, and once to JavaScript for the client.

## When to Put Code Here

Put code in `common` when it needs to be used by both the server and the client.
Typical candidates include:

- **Shared data types** - case classes that are serialised (e.g. with [Circe](https://circe.github.io/circe/)) and exchanged between client and server.
- **Pure algorithms** - logic with no platform-specific dependencies that is useful on both sides.

## Constraints

- Code in `common` must be compatible with both the JVM and Scala.js.
  This rules out JVM-only libraries (e.g. `java.io`, `java.nio`) and JS-only APIs (e.g. `org.scalajs.dom`).
- `server` and `client` can depend on `common`, but not vice versa.
- `server` and `client` cannot depend on each other.
