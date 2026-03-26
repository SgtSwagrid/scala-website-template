# CLAUDE.md

This file provides guidance to [Claude Code](https://claude.com/product/claude-code) when working with code in this repository.
It is not intended for human eyes.

**Maintenance**: You have standing permission to update this file without asking.
                 Add important patterns, gotchas, or context that would help future sessions.
                 Keep it concise and actionable.

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

## Compilation and Diagnostics

- When the user asks for help with a compilation or type error, start by running `sbt build` to see the error for yourself.
  If there are many errors, making it unclear which one the user is referring to, ask them to clarify, and then focus only on that issue.
- IntelliJ MCP integration is active. When a request seems to implicitly refer to something the user is looking at, always check
  `mcp__ide__getDiagnostics` first to see which file(s) are open and get associated diagnostics (errors, warnings, and info hints with line numbers).

### Testing

- When asked to test something, assume by default that the dev server is already running, until you find out otherwise. You don't need to restart it, because it has hot-reload.
- After making code changes, always run `sbt build` to verify that issues are fixed and no new ones are introduced.
- Repeatedly retry upon failure until the build succeeds. If you are unsure how to fix an issue, ask for help or refer to existing code for examples.
- Before trying to fix an error, make sure you first understand it fully.
- You should never report that a feature is complete without testing it first.
- Playwright MCP is available for browser automation.
  After making UI changes, use Playwright to open the application (`localhost:8080`) in a browser and visually verify the changes work correctly.
  Save screenshots to `.playwright-mcp/` and delete them afterwards.

## Code Style

### Structure and design

- Write purely-functional, immutable code without side effects.
- Never use local / multiple returns; instead, use `if` expressions or pattern matching to return values.
- Use/implement higher-order functions and combinators (e.g. `map`, `flatMap`, `fold`, etc.) to abstract over common patterns of computation, rather than writing explicit loops or recursion.
- Actively check for and avoid code duplication, and try to combine things where possible.
- Removing duplicated code will quite possibly involve rewriting existing functions with a more general interface.
- Hide complexity behind well-named mathematical abstractions where possible, so that the code reads like the math it represents.
- Write very short methods (ideally 1-3 lines) that do one thing, and compose them together. If a method is longer than 5 lines, consider breaking it up.
- Don't change interface semantics (e.g. by converting covariant or contravariant types into invariant ones) without asking, just because it fixes a problem.
  The user may which to find a different solution that preserves these semantics.

### Syntax

- Use new Scala 3 syntax in general, including significant indentation (no braces).
- Use `[X: {A as a, B, C}]` instead of `[X : B : C](using a: A[X])` for context bounds.
  In cases where the name for a context bound is specified but not used, remove the `as` clause entirely.
- Never use the `implicit` keyword, always prefer `given` and `using` instead.
- Format with `sbt scalafmtAll` before committing. In general, match your style to that defined in `.scalafmt.conf`.
- When in doubt, follow the style of existing code in the repository.

### Naming conventions

- Use Australian English spelling in comments and variable names.
- Use `camelCase` for variable and method names.
- Use `PascalCase` for class, trait, object, and type names, but also for context bound names following `as`.
- Exceptionally, `lowercase` names are allowed for short-scoped type arguments in type lambdas to avoid shadowing.
- Use short yet descriptive variable names. For example, `transform: X => Y` in `map` is better than `f: X => Y`, but `x` is better than `x1` and `start` is better than `startIndex`.

### Comments

- Add Scaladoc comments to all public members (classes, traits, objects, methods, and vals) that are not self-explanatory.
- All variable/object references in Scaladoc comments should use `[[name]]` links, not backticks.
- All literals in Scaladoc comments should be wrapped in backticks, e.g. `true`, `0`, etc.
- Be as concise as possible in comments, while still being clear.
- Write full sentences, but avoid unnecessary words and repetition.
- Parameter descriptions (i.e. with `@param`) use definite articles (e.g. "the").
- Return value descriptions (i.e. with `@return`) use indefinite articles (e.g. "a", "an").
- Start all sentences with a capital letter and end with a period. This includes `@param` tags, etc.

## Pull Requests

When asked to publish the code changes, your task is to open one or more pull requests (PRs) to merge the changes into `main` on GitHub:

- Use `git` to check what has changed as compared to the `main` branch on `origin`.
- If the changes are thematically linked, they can be published as a single PR.
- Otherwise, you'll need to divide the changes into multiple PRs using your own judgement.
- Each PR should have a singular focus, shouldn't break anything, and should be able to be merged independently.
- Ensure that all code is staged, committed and pushed. Ensure no new files are left uncommitted, and no debug code is left in the codebase.
- When creating a PR, ensure that the title and description are clear, informative, and comprehensive.
- All feature/bugfix/etc branch names should be formatted as "feature_<short description>" or "fix_<short description>" or similar.
- All PR titles should be formatted as "[<scope>] <Short summary>", e.g. "[renderer] Fixed colour inversion bug."
- You have GitHub MCP integration that can be used to do the above.

## Setup

On startup, Claude should offer to initialise the project from the template.

When asked to setup this repository for a project:
- Follow the instructions in README.md.
- Prompt the user for any information you don't yet have.
- If you can do everything yourself, then great.
  If not, then instruct the user step-by-step on the stuff you can't do.
- Update build.sbt with relevant data for the project.
- Make sure that LICENSE.md matches the license in build.sbt.
- Remove the meta content from README.md about the template itself.
- When you are done, remove this section from CLAUDE.md.
  If you find this section in CLAUDE.md later, for a project that is already setup, remove it then.
