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

- 🟠 **Add `maven-enforcer-plugin` rules** — The plugin is in `pluginManagement` but has no active rules. Add: `requireMavenVersion` (≥ 3.8), `requireJavaVersion` (≥ 25), `banDuplicatePomDependencyVersions`, `dependencyConvergence`.
- 🟡 **Add static analysis plugin** — No static analysis is currently configured. Consider SpotBugs (`spotbugs-maven-plugin`), Checkstyle (`maven-checkstyle-plugin`), or ErrorProne for compile-time bug detection.
- 🟡 **Add `.editorconfig`** — Not present. Ensures consistent indentation, charset, and line endings across IDEs and editors.
- 🟡 **Add `.gitattributes`** — Not present. Normalizes line endings (`* text=auto`, `*.java text eol=lf`, `*.sh text eol=lf`) and marks binary files.

## Testing

- 🟡 **Add test coverage reporting (JaCoCo)** — No coverage tool is configured. Add `jacoco-maven-plugin` to generate reports and optionally enforce minimum coverage thresholds.

## Legacy

### Legacy Architecture

- 🟠 **Mutable static listener registry + memory leak in `SubscribeCommand`** — `listeners` is a public mutable static `ConcurrentHashMap` shared across all command instances, coupling `SubscribeCommand` and `UnsubscribeCommand` through global state. Worse: `BeanNotificationListener` is a non-static inner class that holds an implicit reference to its outer `SubscribeCommand` instance. Every registered listener permanently roots that instance (and through it, the `Session`, `Connection`, and `CommandCenter`) preventing garbage collection — a structural memory leak. (`cmd/SubscribeCommand.java`, `cmd/UnsubscribeCommand.java`)
- 🟡 **`Runtime.getRuntime().addShutdownHook(new Thread(...))` pattern** — Registering an anonymous `Thread` as a shutdown hook is the pre-virtual-thread way to handle teardown. (`boot/CliMain.java`)
- 🟡 **Raw `synchronized` locking around session state** — `CommandCenter` guards mutable session state with a raw lock object. `java.util.concurrent.locks.ReentrantLock` provides equivalent semantics with better observability and more flexible lock/unlock control. (`cc/CommandCenter.java`)

### Legacy Code Smells

- 🟡 **Lombok in a Java 25 project** — `@Getter` on simple data holders; `@RequiredArgsConstructor` instead of explicit constructors; `@NonNull` instead of `Objects.requireNonNull()`. Lombok fills language gaps that Java 25 no longer has, and its annotation-processor magic obscures what the compiler actually generates. (`jdk9/JavaProcess.java`, `utils/AppConfig.java`, and others)
- 🟢 **Null-centric API style** — Several methods return `null` to signal absence (e.g., `Completable.getCandidateValues()`, `Command.getSession()`). `Optional` or empty collections express intent more clearly and eliminate null-check boilerplate at call sites. (`Command.java`, `Completable.java`)
- 🟢 **`UnimplementedCommandInput` misleading name** — This is a deliberate stub/null-object, but the name implies unfinished work. A name like `NoopCommandInput` would better communicate intent. (`io/UnimplementedCommandInput.java`)
