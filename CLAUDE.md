# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

**Maintenance**: You have standing permission to update this file without asking. Add important patterns, gotchas, or
context that would help future sessions. Keep it concise and actionable.

## IDE Integration

- IntelliJ MCP integration is active. When a request seems to refer to something the user is looking at, always check
  `mcp__ide__getDiagnostics` first to see which file(s) are open and their current diagnostics (errors, warnings, and
  info hints with line numbers).
- After making code changes, always check `mcp__ide__getDiagnostics` on the affected files to verify no new errors were
  introduced. If there are new errors, fix them and re-check in a loop until all new errors are resolved.
- Playwright MCP is available for browser automation. After making UI changes, use Playwright to open the dev server (
  `localhost:8080`) in a browser and visually verify the changes work correctly. Save screenshots to `.screenshots/` and
  delete them afterwards.

## Build Commands

```bash
sbt build      # Compile all modules (alias for compile).
sbt dev        # Start dev server with hot reload (alias for ~server/reStart).
sbt prod       # Build with full Scala.js optimisation and run server without hot reload.
sbt assemble   # Build a self-contained fat JAR at app.jar (for Docker / deployment).
sbt lint       # Apply linting to all files (alias for scalafmtAll).
sbt lint-check # Check linting without updating anything (alias for scalafmtCheckAll).
```

The dev server runs at `localhost:8080` by default. Set `HOST` and `PORT` environment variables to override.

## Architecture

This is a full-stack Scala 3 web application with three modules:

- **server**: Netty HTTP server using Tapir endpoints and Cats Effect. Entry point is `Main.scala`.
- **client**: Scala.js frontend using Laminar for reactive UI. Compiled JS is automatically copied to server resources
  during build.
- **common**: Cross-compiled (JVM/JS) module containing shared code.

### API Pattern

API endpoints are defined in `server/src/main/scala/api/` using Tapir's endpoint DSL. Server implementations live in
`server/src/main/scala/services/`. Swagger docs (`/docs`) and Prometheus metrics (`/metrics`) are available.

### Client Structure

Views extend the `View` base class and implement `content`.

## Code Style

- Use Scala 3 significant indentation syntax (no braces)
- Write purely-functional, immutable code
- Use Australian English spelling
- Format with scalafmt before committing
- Scaladoc comments for public APIs with `[[name]]` syntax for references
- Parameter descriptions use definite articles; return values use indefinite articles
- Actively check for and avoid code duplication, and try to combine things where possible.
- Never use local / multiple returns; instead, use `if` expressions or pattern matching to return values.
- Hide complexity behind well-named mathematical abstractions where possible, so that the code reads like the math it represents.
- When in doubt, follow the style of existing code in the repository.

## Pull Requests

When asked to publish any changes:

- Where appropriate, break up the changes into separate, self-contained PRs, each with its own feature branch and
  description.
- Ensure that all code is staged, committed and pushed. Never push directly to main.
- All feature branch names should be formatted as "feature_<short description>".
- All PR titles should be formatted as "[<type>][<scope>] <Short summary>", e.g. [fix][rendering] Fixed bug that
  inverted all the colours.

## Testing

- When asked to test something, assume by default that the dev server is already running, until you find out otherwise. You don't need to restart it, because it has hot-reload.
- When implementing a new feature or changing something, ensure that the build completes successfully with "sbt build". If not, repeat on a loop until it is fixed.

## Setup

On startup, Claude should offer to initialise the project from the template.

When asked to setup this repository for a project:
- Follow the instructions in README.md.
- Prompt the user for any information you don't yet have.
- If you can generate PGP keys and access the GitHub and Sonatype config yourself, then great.
  If not, then instruct the user step-by-step on the stuff you can't do.
- Update build.sbt with relevant data for the project.
- Make sure that LICENSE.md matches the license in build.sbt.
- Remove the contents of README.md entirely and replace it with something project-specific.
- When you are done, remove this section from CLAUDE.md.
  If you find this section in CLAUDE.md later, for a project that is already setup, remove it then.
