<div align="center">
  <h1>🌐 Scala Website Template</h1>
  <p>A reusable template for full stack websites in Scala.
</div>

## ❗ Disclaimer

This template is opinionated, with many pre-made decisions about which tooling, code style, and workflow to use.
If you don't like that, don't use a template.

### Rationale

The primary goal of this template is to provide a starting point for people who want to build a full stack website in Scala,
but don't want to spend time setting up the build config and picking out libraries.
The reference point for decisions made herein are my own projects and preferences.

### Platform support

Furthermore, this template has only been tested on Windows 11.
Other operating systems will likely work, but don't be surprised if platform-specific adjustments are necessary.
In case you find yourself having to make adjustments, please consider contributing those adjustments back to the template.

## 📋 What's included?

1. Everything from [`SgtSwagrid/scala-config`](https://github.com/SgtSwagrid/scala-config), including reasonable [Scalafmt](https://scalameta.org/scalafmt/) settings, CI piplines for build integrity, and some IDE config.
2. A collection of common libraries that work well together (see [Dependencies](#-dependencies)).
3. Example build configuration and setup instructions. In particular, this demonstrates how to serve compiled Scala code to the client as a static JavaScript file.

## 🔨 How to use this template

### 1. Create your repository

Click '[**Use this template**](https://github.com/new?template_name=scala-website-template&template_owner=SgtSwagrid)' on GitHub, and follow the instructions to create a new repository for your website.
All files herein will be copied as-is.

### 2. Configure [build.sbt](build.sbt)

You'll need to set the `name`, `organization`, and `version` fields in [build.sbt](./build.sbt) and `projectRoot` in [Subprojects.scala](./project/Subprojects.scala).

### 3. Add repository secrets

You may also wish to set the `GH_TOKEN` local environment variable _and_ GitHub repository secret (see [Environment Variables](#environment-variables)) to help with agentic and CI workflows.

Secrets can be added from the GitHub web interface by nagivating as follows from your repository's page:

> **Settings → Secrets and variables → Actions**

### Done!

Thereafter, the template should work out-of-the-box, and you can start building your website immediately.
If you haven't already, you may also want to read the documentation on [Laminar](https://laminar.dev/).

## 👮‍♂️ License

The included MIT license should be considered only as part of the template, and is not binding.
This repository is hereby released to the public domain, to be used freely.
In particular, and contra [`LICENSE.md`](LICENSE.md), you may remove the license text from copies.

## 🤝 Contributing

[`CONTRIBUTING.md`](CONTRIBUTING.md) is also part of the template, and does not _necessarily_ apply to contributions to the template itself.
The most important thing to know is that many of the configuration files are automatically synced from [`scala-config`](https://github.com/SgtSwagrid/scala-config), and should be updated there rather than here.

## 👁️ See also

Check out [`scala-library-template`](https://github.com/SgtSwagrid/scala-library-template) for a similar template to quickly start a new Scala library.

<br/><br/><br/><br/>
<h3 align="center">⬆️ Delete • Keep ⬇️</h3>
<br/><br/><br/><br/>

<div align="center">
  
  <h1>✨ My Website</h1>
  <p>A very cool website that does something great.</p>

  <!-- Update the following URLS to show live build status in your README. -->
  <span>
    <a href="https://github.com/SgtSwagrid/scala-website-template/actions/workflows/ci.yml"><img src="https://github.com/SgtSwagrid/scala-website-template/actions/workflows/ci.yml/badge.svg" alt="Build status" /></a>
  </span>
  
</div>

## 🚧 Requirements

In order to locally run or work on this project, you'll need to have the following installed:

- **[Scala 3](https://www.scala-lang.org/)*** - a [functional](https://en.wikipedia.org/wiki/Functional_programming) programming language on the [JVM](https://en.wikipedia.org/wiki/Java_virtual_machine).
- **[sbt 1.12.4](https://www.scala-sbt.org/) or newer** - the preeminent build tool of the Scala ecosystem (configured in [build.sbt](./build.sbt)).
- **[JDK 21](https://www.oracle.com/java/technologies/downloads/)** - a runtime environment for the JVM (compatibility of JDK 25 is uncertain).
- **[Git](https://git-scm.com/)** - for version control.
- **[Node.js](https://nodejs.org/)** - used by the [Playwright](https://playwright.dev/) MCP server for browser automation during development (optional).

*Installing Scala separately isn't strictly required, as the correct version (currently [3.8.2](https://www.scala-lang.org/download/3.8.2.html)) will be fetched by sbt in the build process.
However, it may still be helpful for testing.

## ⬇️ Installation

### Clone the repository
```bash
git clone https://github.com/SgtSwagrid/scala-website-template.git
cd scala-website-template
```

## 💻 Local development

The following commands are defined in [Commands.scala](./project/Commands.scala):

### Run a local development server
```bash
sbt dev
```
- Hot-reload is enabled by default, meaning the server and client will automatically restart when code changes are detected.
- The local website will be available at [localhost:8080](http://localhost:8080).

### Compile all subprojects
```bash
sbt build
```
- This is not necessary for running a development server, as builds are automatic.

### Format all code according to style rules
```bash
sbt lint
```
- If using IntelliJ, it is advised to instead enable [reformat-on-save](https://www.jetbrains.com/help/idea/reformat-and-rearrange-code.html#reformat-on-save).
- Linting is also automatically applied when submitting a PR.
- Alternatively use `sbt lint-check` to check for violations _without_ automatically fixing them.

## ⚙️ Environment variables

The following environment variables can be set to configure your local instance:

- `GH_TOKEN` - your [personal access token (PAT)](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens) from GitHub, used by [Claude](https://claude.com/product/claude-code) with GitHub's [MCP server](https://github.com/github/github-mcp-server) to interact with GitHub on your behalf, and by the Initialise Repository workflow. Click [here](https://github.com/settings/personal-access-tokens) to create a new PAT (only allow access to this repository, and disallow all destructive actions). Also add this as a GitHub [repository secret](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets) named `GH_TOKEN`.
- `HOST` - the [address](https://en.wikipedia.org/wiki/Hostname) on which the development server listens for requests (default: `localhost`).
- `PORT` - the [port](https://en.wikipedia.org/wiki/Port_(computer_networking)) on which the development server listens for requests (default: `8080`).

## 🛠️ Recommended tooling

While it isn't mandatory, use of the following tooling is advised:

- **[IntelliJ IDEA](https://www.jetbrains.com/idea/)** - the main IDE for Scala development.
- **[Claude Code](https://code.claude.com/docs/en/overview)** - an AI agent for automating tasks (optional).

Shared configuration for the above is checked into the repository for a seamless experience.

## 🏗️ Architecture

The codebase is partitioned into three subprojects.
All subprojects are written in [Scala](https://www.scala-lang.org/), but they are compiled in different ways.
Build configurations for the subprojects can be found in [Subprojects.scala](./project/Subprojects.scala).

### The [server](./server) subproject

* Code which is deployed to a JVM-based backend server.
* Responsible for database management and serving [HTTP](https://en.wikipedia.org/wiki/HTTP) or [WebSocket](https://en.wikipedia.org/wiki/WebSocket) requests.
* This includes providing the *client* code (see below) to the user.
* All web pages use a single minimalistic template, whose only job is to invoke the (appropriate part of the) *client* code.
* The primary entry point is found in [Main.scala](./server/src/main/scala/Main.scala).

### The [client](./client) subproject

* Code which is [transpiled](https://en.wikipedia.org/wiki/Source-to-source_compiler) to a [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript)-based frontend and delivered to the user's browser.
* Responsible for the user interface.
* We exclusively use [client-side rendering (CSR)](https://developer.mozilla.org/en-US/docs/Glossary/CSR), meaning that all [DOM](https://developer.mozilla.org/en-US/docs/Web/API/Document_Object_Model) elements are constructed dynamically by the client.
* All client code is packaged into a single source file (`main.js`) by the build system.

### The [common](./common) subproject

* Code which is common to both the *server* and *client*. That is, modules in *common* are freely available to the other subprojects.
* Conversely, code in *server* or *client* cannot be referenced by any other subproject.
* Code from *common* is compiled twice; once into JVM bytecode for the *server*, and once into JS for the *client*.
* Used for defining common data types, algorithms, or communication protocols that are (or in principle could be) used by either.
* Avoids source duplication: one implementation is enough.

## 📋 Dependencies

The following open-source libraries are used. You do not need to install these manually, as they are managed by sbt.
Dependencies are defined in [Subprojects.scala](./project/Dependencies.scala).

- **[scalafmt](https://scalameta.org/scalafmt/)** - a code formatter to ensure a consistent style (configured by [.scalafmt.conf](https://github.com/SgtSwagrid/fairmap/blob/main/.scalafmt.conf)).
- **[Tapir 1.13](https://tapir.softwaremill.com/en/latest/)** - a library to define and implement HTTP APIs.
- **[Netty](https://netty.io/)** - an asynchronous web server for the JVM (integrated with Tapir, among other [options](https://tapir.softwaremill.com/en/latest/server/overview.html)).
- **[Swagger](https://swagger.io/)** - a UI for exploring and testing server endpoints (integrated with Tapir, see [localhost:8080/docs](localhost:8080/docs)).
- **[Prometheus](https://prometheus.io/)** - for metrics and performance monitoring (integrated with Tapir, not yet enabled).
- **[asset-loader](https://github.com/SgtSwagrid/asset-loader)** - for serving static assets.
- **[Scala.js](https://www.scala-js.org/)** - the official Scala-to-JS transpiler.
- **[Laminar 17](https://laminar.dev/)** - a library for building reactive web-based user interfaces (and extended by [laminext](https://github.com/tulz-app/laminext)).
- **[Airstream](https://github.com/raquo/airstream)** - a [functional reactive programming](https://en.wikipedia.org/wiki/Functional_reactive_programming) library (required by Laminar).
- **[Circe](https://circe.github.io/circe/)** - automatic serialisation/deserialisation to/from [JSON](https://www.json.org/json-en.html) (for client-server communication).
- **[Cats](https://typelevel.org/cats/)** - provides abstractions for functional programming (alongside [Cats Effect 3](https://typelevel.org/cats-effect/) and others).
