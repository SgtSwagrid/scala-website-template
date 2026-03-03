# Server

The server subproject runs on the JVM and is responsible for handling HTTP requests and serving the website.
It is built on [Cats Effect](https://typelevel.org/cats-effect/), [Tapir](https://tapir.softwaremill.com/), and [Netty](https://netty.io/).

## Entry Point

[`Main.scala`](src/main/scala/Main.scala) starts the Netty server, binding to the host and port specified by the environment.

## Structure

| Package | Description |
|---------|-------------|
| `api/` | Tapir endpoint *specifications* (inputs, outputs, error types). |
| `services/` | Endpoint *implementations*, grouped thematically by service. |
| `assets/` | Static file serving with MD5 ETag computation and in-memory caching. |
| `html/` | The HTML page template, shared across all pages. |
| `config/` | Environment and system property configuration. |

## Adding an Endpoint

1. Declare the endpoint spec in `api/` using [Tapir's endpoint DSL](https://tapir.softwaremill.com/en/latest/endpoint/basics.html).
2. Implement it in `services/` by extending [`Service`](src/main/scala/services/Service.scala) and overriding `api`.
3. Register the service in [`Main.scala`](src/main/scala/Main.scala) by passing its endpoints to `addEndpoints`.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | Serves the index page. |
| `GET` | `/assets/*` | Serves static files (JS, images, etc.) with ETag caching. |
| `GET` | `/health` | Health check; returns `200 OK`. |
| `GET` | `/hot-reload` | WebSocket for dev hot reload (dev mode only). |
| `GET` | `/docs` | Swagger UI for exploring the API (dev mode only). |
| `GET` | `/metrics` | Prometheus metrics endpoint. |

## Configuration

| Source | Key | Description | Default |
|--------|-----|-------------|---------|
| Env var | `HOST` | Address the server binds to. | `localhost` |
| Env var | `PORT` | Port the server listens on. | `8080` |
| JVM property | `dev.mode` | Enables hot reload and Swagger UI. | `false` |
| JVM property | `app.name` | Application name (used in Swagger). | — |
| JVM property | `app.version` | Application version (used in Swagger). | — |
| JVM property | `assets.dir` | Directory from which static assets are served. | — |

JVM properties are set automatically by sbt when running via `sbt dev` or `sbt prod`.
