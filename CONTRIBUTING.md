# 📜 Contribution Guidelines

This document lays out the basic principles which govern contributions to this project.

## 🛠️ Workflow

1. Clone the repository: `git clone https://github.com/SgtSwagrid/<repository-name>.git`.
2. Create a feature branch: `git switch -c feature_<description>`.
3. Make your changes (use of [IntelliJ IDEA](https://www.jetbrains.com/idea/) is recommended but not required).
4. Reformat the code according to our style rules: `sbt scalafmtAll`.
5. Commit (`git add -A` then `git commit -m "<description>"`).
6. Push your changes, either to this repository directly if you have write access, or to a forked version of it if you don't.
7. Create a pull request to `main`, with the title given as `[<scope>] <Description>` (e.g. `[rendering] Solved inverted colours.`).
8. Before merging, the code must pass the build test (defined in [build-integrity.yml](./.github/workflows/build-integrity.yml)).

## 🎩 Code Style

Please see the [Code Style Guidelines](docs/STYLE_GUIDE.md) for details on our design principles and code style.
In brief:
- We adhere to the principles of [functional programming](https://en.wikipedia.org/wiki/Functional_programming).
- [Scalafmt](https://scalameta.org/scalafmt/) will automatically reformat your code for you with `sbt scalafmtAll`.

## 🌳 Branching Strategy

This project follows simple [trunk-based](https://trunkbaseddevelopment.com/) development.
All work happens on short-lived feature branches, which are merged into `main` by pull request.
Releases are by [tag](https://docs.github.com/en/repositories/releasing-projects-on-github/viewing-your-repositorys-releases-and-tags),
and there are no separate release branches.

## 🤖 LLM Agent Usage

The use of agents where it makes sense is allowed and encouraged.
In particular, this project is configured for optimal interoperability with [Claude Code](https://claude.com/product/claude-code).
You are however free to use whichever tooling you wish.

### Responsibility

You are still responsible for code that was written or partially written by an LLM.
You should always read and understand all changes before submitting them.

Generally, the reviewer doesn't need to know which code was generated or manually created.
Such a distinction would undermine any sense of responsibility: all of the code belongs to you.
An exception exists when an LLM was used to perform a menial transformation en masse,
in which case you might consider isolating such changes to a stand-alone commit, and sharing the prompt.

## 📞 Contact

Created by [Alec Dorrington](https://github.com/SgtSwagrid).
For questions or issues, please use the GitHub issue tracker.

## 🔁 Origin

The primary source of truth for this document can be found in the [Scala Config](https://github.com/SgtSwagrid/scala-config) repository,
from which it is automatically synchronised with [Github Graph](https://github.com/SgtSwagrid/github-graph).
This should be updated there rather than here, lest any changes be subsequently reverted.
