# Contributing

## Workflow

1. Clone the repository: `git clone https://github.com/SgtSwagrid/scala-website-template.git`.
2. Create a feature branch: `git checkout -b feature_<description>`.
3. Make your changes and format: `sbt lint`
4. Commit (`git add -A` then `git commit -m "<description>"`) and push (`git push`).
5. Create a pull request to `main`, with the title formatted as `[<type>][<scope>] <Description>`, e.g. `[fix][render] Solved inverted colours.`
6. Before merging, the code must pass the build test (defined in [ci.yml](./.github/workflows/ci.yml)).

## Code Style

Code formatting is determined by scalafmt. You shouldn't worry about aligning things in a pretty manner, scalafmt will do this for you.
The formatting rules can be found in [.scalafmt.conf](./.scalafmt.conf).
In general, we follow a Scala 3 style with significant indentation.

### Functional Programming

In general, this project follows a [functional](https://en.wikipedia.org/wiki/Functional_programming) style. In particular, this means:

- Use of immutable [data structures](https://en.wikipedia.org/wiki/Persistent_data_structure) and variables; state updates are by duplication rather than by mutation.
- Use of pure functions without [side effects](https://en.wikipedia.org/wiki/Side_effect_(computer_science)), or with _explicit_ side effects where unavoidable.
- Very consistent and explicit use of [types](https://shekhar14.medium.com/type-theory-and-functional-programming-52f81deb36f1).

When performance constraints necessitate the use of mutable state in a limited context, this is allowed.

### Mathematical Abstractions

When it makes sense to do so, try to hide complexity behind mathematical abstractions with obvious semantics.
A well-chosen abstraction (like `Vec` or `AffineBijection` or `Signal`) makes the code read like the math it represents.

## Branching Strategy

This project follows simple [trunk-based](https://trunkbaseddevelopment.com/) development.
All development happens on short-lived feature branches, which are merged into `main` when complete.

## Contact

Created by [Alec Dorrington](https://github.com/SgtSwagrid). For questions or issues, please use the GitHub issue tracker.
