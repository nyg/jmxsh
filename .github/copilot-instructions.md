# jmxsh - Copilot Development Instructions

## Project Overview

jmxsh is an interactive command-line JMX client. Users connect to JMX-enabled Java applications to browse MBeans, get/set attributes, and invoke operations.

- **Java 25**, Maven build, no Maven wrapper (use system `mvn`)
- **Entry point**: `sh.jmx.jmxsh.boot.CliMain`
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

Surefire excludes `sh.jmx.jmxsh.jdk*` tests (platform-specific JVM attach tests).

## Architecture

### Execution Flow

`CliMain` → creates `CommandOutput` + `CommandInput` → creates `CommandCenter` → REPL loop reads lines and calls `commandCenter.execute(line)`.

### Command System

Commands extend the abstract `Command` class and implement `execute()`. They are **transient** — a new instance is created per execution.

Commands are registered in `PredefinedCommandFactory` via a static `COMMAND_CLASSES` list. Each command class must carry a `@CommandLine.Command` annotation (picocli); the annotation's `name` and `aliases` fields are read at startup to build the name → class map. `HelpCommand` is added separately in the same factory.

Arguments and options use picocli annotations:
- `@CommandLine.Command(name="...", aliases={"..."}, description="...")` on the class
- `@Option(names={"-x", "--xxx"}, description="...")` on fields
- `@Parameters` for positional args

### Session & Connection

`Session` is a concrete class that holds the JMX connection state plus the currently selected domain and bean. It owns the connect/disconnect lifecycle and wraps the `CommandOutput` in a `VerboseCommandOutput` decorator. Session is **not thread-safe** — `CommandCenter` synchronizes all calls. Commands receive the session via `setSession()` before each `execute()` call.

`Connection` is a record that holds the `JMXConnector` and `JMXServiceURL`. It is created inside `Session.connect()` and nulled out on `Session.disconnect()`.

Both RMI and JMXMP protocols are supported. URL formats:
- `host:port` — RMI shorthand (default), expands to `service:jmx:rmi:///jndi/rmi://host:port/jmxrmi`
- `jmxmp://host:port` — JMXMP shorthand, expands to `service:jmx:jmxmp://host:port`
- `service:jmx:...` — full JMX service URL (any protocol)
- `<PID>` — attaches to a local JVM process

`JMXConnectorFactory` auto-discovers the JMXMP provider at runtime via the Java service loader (`META-INF/services`).

### IO Abstraction

`CommandInput` and `CommandOutput` are abstract base classes with implementations for interactive console (JLine), files, streams, and writers. `VerboseCommandOutput` is a decorator that filters output based on `OutputMode` (SILENT or BRIEF).

### JVM Process Discovery

`jdk9/` provides JVM discovery using the `com.sun.tools.attach` API (`VirtualMachine`/`VirtualMachineDescriptor`). `JavaProcessManager` is instantiated directly in `CommandCenter`. Used by the `jvms` and `open` commands.

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
- **Mockito** for mocking — use `@ExtendWith(MockitoExtension.class)` with `@Mock` fields
- **AssertJ** for fluent assertions (`assertThat(...).isEqualTo(...)`)
- Test helper: `SelfRecordingCommand` in the test root package

Typical test pattern:
```java
@ExtendWith(MockitoExtension.class)
class DomainsCommandTest {
    @Mock
    private Session session;
    @Mock
    private Connection connection;
    @Mock
    private MBeanServerConnection con;

    private DomainsCommand command;
    private StringWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        command = new DomainsCommand();
        writer = new StringWriter();
        when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
        when(session.getConnection()).thenReturn(connection);
        when(connection.getServerConnection()).thenReturn(con);
    }

    @Test
    void execution() throws Exception {
        when(con.getDomains()).thenReturn(new String[] {"a", "b"});
        command.setSession(session);
        command.execute();
        verify(con).getDomains();
        assertThat(writer.toString().trim()).isEqualTo("a" + System.lineSeparator() + "b");
    }
}
```

### Adding a New Command

1. Create a class in `cmd/` extending `Command` with `@CommandLine.Command(name="mycommand")`
2. Implement `execute()`, define options/arguments via picocli annotations
3. Add the class to the `COMMAND_CLASSES` list in `PredefinedCommandFactory`
4. Add a test in `src/test/java/.../cmd/` following the Mockito pattern above

### Error Handling

- Commands throw `IllegalArgumentException` for invalid user input
- Commands throw `IllegalStateException` for invalid session state (e.g., not connected)
- `RuntimeIOException` wraps checked `IOException` as unchecked
- Error display respects `OutputMode`: BRIEF shows messages prefixed with `#`, SILENT suppresses output