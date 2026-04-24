# jmxsh - Copilot Development Instructions

## Project Overview

jmxsh is an interactive command-line JMX client. Users connect to JMX-enabled Java applications to browse MBeans, get/set attributes, and invoke operations.

- **Java 25**, Maven build, no Maven wrapper (use system `mvn`)
- **Entry point**: `org.cyclopsgroup.jmxterm.boot.CliMain`
- **CI tests on**: JDK 25

## Build Commands

```bash
# Full validation (matches CI)
mvn -B -q --no-transfer-progress verify

# Compile only
mvn -B -q --no-transfer-progress compile

# Package (creates uber JAR at target/jmxsh-*-uber.jar)
mvn -B -q --no-transfer-progress package

# Run a single test class
mvn -B -q --no-transfer-progress test -Dtest=GetCommandTest

# Run a single test method
mvn -B -q --no-transfer-progress test -Dtest=GetCommandTest#testExecute

# Run the application
java -jar target/jmxsh-*-uber.jar
```

Run `mvn clean` before `mvn compile` when switching branches to avoid stale class errors.

Surefire excludes `org.cyclopsgroup.jmxterm.jdk*` tests (platform-specific JVM attach tests).

## Architecture

### Execution Flow

`CliMain` → creates `CommandOutput` + `CommandInput` → creates `CommandCenter` → REPL loop reads lines and calls `commandCenter.execute(line)`.

### Command System

Commands extend the abstract `Command` class and implement `execute()`. They are **transient** — a new instance is created per execution.

Commands are registered via the properties file `src/main/resources/META-INF/cyclopsgroup/jmxsh.properties`, which maps command names to implementation classes and defines aliases (e.g., `quit` → `exit`, `bye`). `HelpCommand` is added programmatically in `PredefinedCommandFactory`.

Arguments and options use annotations from `org.cyclopsgroup.jcli.annotation`:
- `@Cli(name="...")` on the class
- `@Option(name="x", longName="xxx", description="...")` on setter methods
- `@Argument` and `@MultiValue` on setter methods for positional args

### Session & Connection

`Session` (abstract class) holds the JMX connection state plus the currently selected domain and bean. `SessionImpl` in `cc/` is the concrete implementation. Session is **not thread-safe** — `CommandCenter` synchronizes all calls. Commands receive the session via `setSession()` before each `execute()` call.

Both RMI and JMXMP protocols are supported. URL formats:
- `host:port` — RMI shorthand (default), expands to `service:jmx:rmi:///jndi/rmi://host:port/jmxrmi`
- `jmxmp://host:port` — JMXMP shorthand, expands to `service:jmx:jmxmp://host:port`
- `service:jmx:...` — full JMX service URL (any protocol)
- `<PID>` — attaches to a local JVM process

`JMXConnectorFactory` auto-discovers the JMXMP provider at runtime via the Java service loader (`META-INF/services`).

### IO Abstraction

`CommandInput` and `CommandOutput` are abstract base classes with implementations for interactive console (JLine), files, streams, and writers. `VerboseCommandOutput` is a decorator that filters output based on `VerboseLevel` (SILENT, BRIEF, VERBOSE).

### JVM Process Discovery

`jdk9/` provides JVM discovery using the `com.sun.tools.attach` API (`VirtualMachine`/`VirtualMachineDescriptor`). Created via `JPMFactory.createProcessManager()`. Used by the `jvms` and `open` commands.

## Conventions

### Branching & Pull Requests

**Never commit directly to `master`.** Always:
1. Create a new branch (e.g. `feat/my-feature`, `fix/my-fix`, `chore/my-task`)
2. Commit your changes on that branch
3. Open a pull request against `master`

### Commit Messages

Follow **Conventional Commits**: `type(scope): description`

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
Scopes: `cmd`, `io`, `build`, `deps`, `ci`

### Testing

- **JUnit** (Jupiter) with `@Test`, `@BeforeEach` from `org.junit.jupiter.api`
- **Mockito** for mocking (`mock()`, `when()`, `verify()`)
- **AssertJ** for fluent assertions (`assertThat(...).isEqualTo(...)`)
- Test helpers: `MockSession`, `MockConnection`, `SelfRecordingCommand` in the test root package

Typical test pattern:
```java
@BeforeEach
void setUp() {
    command = new DomainsCommand();
}

@Test
void execution() throws Exception {
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    StringWriter output = new StringWriter();
    when(con.getDomains()).thenReturn(new String[] {"a", "b"});
    command.setSession(new MockSession(output, con));
    command.execute();
    verify(con).getDomains();
    assertThat(output.toString().trim()).isEqualTo("a" + System.lineSeparator() + "b");
}
```

### Adding a New Command

1. Create a class in `cmd/` extending `Command` with `@Cli(name="mycommand")`
2. Implement `execute()`, define options/arguments via annotations
3. Register in `src/main/resources/META-INF/cyclopsgroup/jmxsh.properties`
4. Add a test in `src/test/java/.../cmd/` following the Mockito pattern above

### Error Handling

- Commands throw `IllegalArgumentException` for invalid user input
- Commands throw `IllegalStateException` for invalid session state (e.g., not connected)
- `RuntimeIOException` wraps checked `IOException` as unchecked
- Error display respects `VerboseLevel`: VERBOSE shows stack traces, BRIEF shows messages prefixed with `#`, SILENT suppresses output