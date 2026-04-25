# End-to-End (E2E) Tests

## Overview

E2E tests verify that the **fully packaged jmxsh uber JAR** works correctly as a standalone application. Unlike integration tests (which call `CommandCenter` directly in-process), E2E tests launch jmxsh as a **separate OS process**, pipe commands to its stdin, and verify its stdout output and exit codes.

This tests the real user experience: CLI argument parsing, non-interactive mode, file I/O, auto-connect, and process exit behavior — none of which can be tested in-process.

## How they run

E2E tests live in:

```
src/test/java/sh/jmx/jmxsh/e2e/
```

Like integration tests, they use the `*IT.java` naming convention and run via the Maven Failsafe plugin during `mvn verify`:

```bash
# Run all tests (unit + integration + e2e)
mvn verify

# Run a single e2e test class
mvn verify -Dit.test=ScriptExecutionE2EIT

# Run a single e2e test method
mvn verify -Dit.test=ExitCodeE2EIT#testSuccessfulExecution
```

E2E tests depend on the uber JAR existing in `target/`, which is built during the `package` phase — Failsafe's `integration-test` phase runs after `package`, so the JAR is always available.

E2E tests run on every PR via CI, on JDK 25.

## Architecture

E2E tests involve **two separate JVM processes** communicating over JMX:

```
┌─────────────────────┐         JMX/RMI          ┌──────────────────────┐
│   jmxsh process      │ ◄──────────────────────► │  target JVM process  │
│                      │    localhost:<port>       │                      │
│  (uber JAR, -n mode) │                          │  (TestTargetApp)     │
│  stdin ← test sends  │                          │  MBean: test:type=   │
│  stdout → test reads  │                          │    TestMBean         │
└─────────────────────┘                           └──────────────────────┘
         ▲                                                  ▲
         │              ┌──────────────────┐                │
         └──────────────│   JUnit test     │────────────────┘
                        │  (test process)  │
                        │  manages both    │
                        │  subprocesses    │
                        └──────────────────┘
```

### TestTargetApp — the JMX target

`TestTargetApp` is a minimal Java main class that:

1. Gets the platform `MBeanServer`
2. Registers a `TestMBean` (with String/int attributes and echo/add/reset operations)
3. Prints `"READY"` to stdout to signal it's ready for connections
4. Blocks on `System.in.read()` — stays alive until the test closes its stdin

The MBean interface and implementation are **nested inside TestTargetApp** to avoid classpath issues — since this class runs as a subprocess, it can only access classes on its own classpath.

### TargetJvmProcess — launching the target

`TargetJvmProcess` manages the `TestTargetApp` subprocess lifecycle:

1. Finds a free TCP port using `new ServerSocket(0)`
2. Launches a JVM with JMX remote enabled:
   ```
   java -cp <classpath>
        -Dcom.sun.management.jmxremote
        -Dcom.sun.management.jmxremote.port=<port>
        -Dcom.sun.management.jmxremote.authenticate=false
        -Dcom.sun.management.jmxremote.ssl=false
        -Dcom.sun.management.jmxremote.local.only=true
        org.cyclopsgroup.jmxterm.e2e.TestTargetApp
   ```
3. Waits for the `"READY"` line on stdout before returning

Tests use it via `@BeforeAll` / `@AfterAll`:

```java
private static TargetJvmProcess target;

@BeforeAll
static void startTarget() throws Exception {
    target = new TargetJvmProcess();
    target.waitUntilReady(Duration.ofSeconds(30));
}

@AfterAll
static void stopTarget() {
    if (target != null) target.close();
}
```

Key methods:
- `getJmxPort()` — the port to connect to
- `getPid()` — the OS process ID
- `close()` — forcibly destroys the process

### JmxshProcessHelper — launching jmxsh

`JmxshProcessHelper` manages a jmxsh subprocess:

1. Finds the uber JAR in `target/` (matches `jmxsh-*-uber.jar`)
2. Launches: `java -jar <uber.jar> -n [extra args]`
   - `-n` enables non-interactive mode (reads plain stdin, no JLine)
3. Provides methods to send commands and read output

```java
try (JmxshProcessHelper jmxsh = new JmxshProcessHelper("-q")) {
    jmxsh.sendCommand("open localhost:" + target.getJmxPort());
    jmxsh.sendCommand("domains");
    String output = jmxsh.readAllOutput(Duration.ofSeconds(10));
    assertThat(output).contains("JMImplementation");
}
```

Key methods:
- `sendCommand(String)` — sends a single command line to stdin
- `sendCommandAndClose(String...)` — sends multiple commands, then closes stdin (triggers EOF → exit)
- `readAllOutput(Duration timeout)` — closes stdin, waits for the process to exit, returns all stdout
- `getExitCode()` — returns the process exit code after it has terminated
- `close()` — forcibly destroys the process if still running

## What is tested

### ScriptExecutionE2EIT (4 tests)

Tests full command workflows by piping commands to the jmxsh process.

| Test | What it verifies |
|------|-----------------|
| `testBasicCommandExecution` | Opens connection, runs `domains`, verifies domain names appear in output |
| `testGetAttribute` | Opens, selects bean, runs `get Name`, verifies `"default"` in output |
| `testSetAndGetAttribute` | Sets `Name` to `"hello"`, gets it back, verifies the new value |
| `testRunOperation` | Invokes `run echo world`, verifies `"echo:world"` in output |

### CliArgumentsE2EIT (4 tests)

Tests CLI flags that control jmxsh's startup behavior.

| Test | What it verifies |
|------|-----------------|
| `testAutoConnect` | `-l localhost:<port>` auto-connects on startup; `domains` works immediately |
| `testSilentMode` | `-q` (quiet mode) suppresses all `#`-prefixed messages but still shows result values |
| `testExitOnFailure` | `-e` causes jmxsh to exit with non-zero code on first command failure |
| `testHelpFlag` | `-h` prints usage information and exits with code 0 |

### ExitCodeE2EIT (3 tests)

Tests the process exit code under different scenarios.

| Test | What it verifies |
|------|-----------------|
| `testSuccessfulExecution` | Normal flow (open → domains → quit) exits with code 0 |
| `testExitOnFailureReturnsNegativeLineNumber` | With `-e`, a failure on line N causes exit code -N (254 unsigned on POSIX) |
| `testQuitExitCode` | Just sending `quit` exits with code 0 |

### StartupErrorsE2EIT (2 tests)

Tests that startup failures (before the REPL begins) produce a clean `#`-prefixed error message and a non-zero exit code instead of a raw JVM stack trace.

| Test | What it verifies |
|------|-----------------|
| `invalidOutputFileProducesCleanError` | `-o /nonexistent/dir/output.txt` fails cleanly with a `#`-prefixed message and non-zero exit |
| `failedAutoConnectProducesCleanError` | `-l localhost:1` (always refused) fails cleanly with a `#`-prefixed message and non-zero exit |

### JmxmpConnectionE2EIT (4 tests)

Tests the JMXMP protocol end-to-end against a `TargetJmxmpProcess` subprocess.

| Test | What it verifies |
|------|-----------------|
| `testOpenWithJmxmpShorthand` | `open jmxmp://localhost:<port>` connects successfully |
| `testOpenWithFullJmxmpServiceUrl` | `open service:jmx:jmxmp://localhost:<port>` connects successfully |
| `testGetAttributeOverJmxmp` | Reads the `Name` attribute over a JMXMP connection |
| `testRunOperationOverJmxmp` | Invokes `run echo world` over a JMXMP connection |

## Port allocation

Both `TargetJvmProcess` and the test infrastructure use **random free ports** (`new ServerSocket(0)`) to avoid conflicts. This is important because:

- Multiple test classes may run in parallel
- CI runners may have other services using fixed ports
- Tests must not interfere with each other

## Process lifecycle and cleanup

Every process is forcibly destroyed in `@AfterAll` or in `close()` methods. `JmxshProcessHelper` implements `AutoCloseable` so it can be used in try-with-resources blocks.

The target JVM stays alive for all tests in a class (started in `@BeforeAll`, stopped in `@AfterAll`), while each test creates its own fresh jmxsh process to ensure clean state.

## Differences from integration tests

| Aspect | Integration tests | E2E tests |
|--------|------------------|-----------|
| JMX server | Embedded in test JVM | Separate subprocess |
| jmxsh | `CommandCenter` called directly | Uber JAR launched as process |
| Communication | In-process method calls | stdin/stdout pipes + JMX/RMI |
| What's tested | Command logic, JMX protocol | CLI args, packaging, exit codes |
| Speed | Fast (~50ms per test) | Slower (~200ms per test, process startup) |
| Failure diagnosis | Stack traces in test output | Process stdout/stderr capture |

Both tiers complement each other: integration tests catch command logic bugs quickly, while E2E tests verify the packaged application works as users would run it.
