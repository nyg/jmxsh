# jmxsh Modernization TODO

Improvement opportunities identified by auditing the codebase, build configuration, CI pipeline,
and dependency graph. Items are organized by category. Each item is labeled with a priority:

- 🔴 **Critical** — Bugs, security issues, or blockers
- 🟠 **High** — High-value improvements with clear benefits
- 🟡 **Medium** — Worth doing but lower urgency
- 🟢 **Low** — Polish and nice-to-haves

## Java Language Modernization

- 🟢 **Evaluate `Optional` for nullable return types** — Many methods return null to signal absence; `Optional` would make intent clearer at API boundaries.
- 🟢 **Explore Java 25 features** — Structured concurrency, scoped values, string templates (if stabilized), unnamed patterns, and other features that may simplify existing code.

## Build / POM

- 🟡 **Add static analysis plugin** — No static analysis is currently configured. Consider SpotBugs (`spotbugs-maven-plugin`), Checkstyle (`maven-checkstyle-plugin`), or ErrorProne for compile-time bug detection.
- 🟡 **Add `.editorconfig`** — Not present. Ensures consistent indentation, charset, and line endings across IDEs and editors.
- 🟡 **Add `.gitattributes`** — Not present. Normalizes line endings (`* text=auto`, `*.java text eol=lf`, `*.sh text eol=lf`) and marks binary files.

## Testing

- 🟡 **Add test coverage reporting (JaCoCo)** — No coverage tool is configured. Add `jacoco-maven-plugin` to generate reports and optionally enforce minimum coverage thresholds.
- 🟢 **Address existing TODO in `WatchCommand`** — Line 28: `TODO Consider the use case for CSV file backend generation`. Decide whether to implement or remove the comment.
