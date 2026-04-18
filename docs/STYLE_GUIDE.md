# 📜 Code Style Guidelines

This document lays out the basic principles which govern the coding style in this project.
The rules here are not of a strictly binding nature, and can be broken when there is good reason for doing so.
While this document is for humans too, some of the excessive specificity is for the sake of LLMs.

## 🤖 Scalafmt

The guidelines here are supplemented by [Scalafmt](https://scalameta.org/scalafmt/),
which provides automatic reformatting based on the style configuration in [.scalafmt.conf](../.scalafmt.conf).
You can reformat your local changes using `sbt scalafmtAll`, or by enabling "reformat on save" in your IDE (recommended).
You should ensure that your code is correctly formatted with Scalafmt before submitting it,
or else the CI pipeline won't allow it to be merged.

## 🎩 Code Style

### Structure and design

- This project follows a [functional](https://en.wikipedia.org/wiki/Functional_programming) style:
  - Use pure functions without [side effects](https://en.wikipedia.org/wiki/Side_effect_(computer_science)), or with _explicit_ side effects where unavoidable.
  - Use [immutable](https://en.wikipedia.org/wiki/Persistent_data_structure) data structures and variables, where state updates are by duplication rather than by mutation.
  - Consistently use fine-grained [types](https://shekhar14.medium.com/type-theory-and-functional-programming-52f81deb36f1).
  - Avoid local or multiple returns, and instead use expression-based conditionals (`if` + `else`) or pattern matching.
- When performance constraints necessitate the use of mutable state in a limited scope, this is allowed.
- Hide complexity behind well-named mathematical abstractions where possible, so that the code reads like the math it represents.
- Use higher-order functions and combinators (e.g. `map`, `flatMap`, `fold`, etc.)
  to abstract over common patterns of computation, rather than writing explicit loops or recursion.
- Actively check for and avoid code duplication, and try to unify implementations where possible.
  - Removing duplicated code will quite possibly involve rewriting existing functions with a more general interface.
- Write very short methods (ideally 1-3 lines) that do one thing, and form more complex behaviour by composing them.
  If a method is longer than 5 lines, consider breaking it up.
- When possible, prefer inheritance with `-Ops`-style traits and F-bounded polymorphism rather than type classes.

### Syntax

- Use new Scala 3 syntax in general, and in partcular:
  - Use significant indentation (without braces `{}`).
  - Use `[X: {A as a, B, C}]` instead of `[X : B : C](using a: A[X])` for context bounds.
  - Prefer `given` and `using` rather than `implicit`.
- Place `import` statements at the top of the file, not inside classes or functions.
- Only name context bounds or `given` parameters when they must be referred to by name.
- Avoid qualified names unless you have good reason to use them. Instead of `algebra.Monoid`, use `import algebra.Monoid` and just write `Monoid`.
  - Exception 1: For when the name is unclear without the surrounding context.
  - Exception 2: For when multiple in-use libraries have similar sets of names.
- When in doubt, follow the existing style of the codebase.

### Naming conventions


- Use `camelCase` for variable and method names.
- Use `PascalCase` for type, trait, class, and object names, but also for context bound names following `as`.
- It often makes sense to name a context bound `F[X]` after the type `X`, e.g. `def f[X : Numeric as X]`.
- Exceptionally, `lowercase` names are allowed for short-scoped type arguments in type lambdas to avoid shadowing.
- Use short yet descriptive variable names.
  - For example, `def map[Y](transform: X => Y)` is better than `def map[Y](f: X => Y)`, but `x` is better than `x1` and `start` is better than `startIndex`.
- Use [Australian English](https://www.macquariedictionary.com.au/) spelling in names and comments.

### Comments

- Be as concise as possible, avoiding repetition and unnecessary words, while still writing full sentences with clear meaning.
- Add [Scaladoc](https://docs.scala-lang.org/style/scaladoc.html) comments to all public members that are not self-explanatory.
- Prefer writing refences to types and functions with `[[MyType]]` rather than \``MyType`\`, so as to establish a navigable link.
- All literals in comments should be wrapped in backticks, e.g. \``true`\`, \``0`\`, etc.
- All sentences should start with a capital letter and end with a period (`.`).
  This includes after tags such as `@param` and `@return`.
- Parameter descriptions (i.e. with `@param`) use definite articles (e.g. "The").
- Return value descriptions (i.e. with `@return`) use indefinite articles (e.g. "A", "An").

## 🔁 Origin

The primary source of truth for this document can be found in the [Scala Config](https://github.com/SgtSwagrid/scala-config) repository,
from which it is automatically synchronised with [Github Graph](https://github.com/SgtSwagrid/github-graph).
This should be updated there rather than here, lest any changes be subsequently reverted.
