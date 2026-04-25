# Integration Tests

## Overview

Integration tests verify that jmxsh commands work correctly against a **real JMX server**. Unlike the unit tests (which mock `MBeanServerConnection`), these tests start an actual JMX connector server inside the test JVM and execute commands over a real JMX/RMI connection.

This means the tests exercise the full command lifecycle: argument parsing → command dispatch → JMX protocol call → result formatting → output.

## How they run

Integration tests live in:

```
src/test/java/sh/jmx/jmxsh/integration/
```

They use the `*IT.java` naming convention and are executed by the **Maven Failsafe plugin** during the `integration-test` phase. This means they run as part of `mvn verify`, not `mvn test`:

```bash
# Run all tests (unit + integration + e2e)
mvn verify

# Run a single integration test class
mvn verify -Dit.test=AttributeReadWriteIT

# Run a single integration test method
mvn verify -Dit.test=ConnectionLifecycleIT#testOpenConnection
```

Integration tests run on every PR via the existing CI workflow (`java-ci.yaml`), on JDK 25.

## Architecture

### The embedded JMX server

The key piece of infrastructure is `EmbeddedJmxServer`, a JUnit extension that:

1. Finds a free TCP port
2. Creates a dedicated `MBeanServer` (isolated from the platform MBeanServer)
3. Registers a test MBean under `test:type=TestMBean`
4. Creates an RMI registry on the chosen port
5. Starts a `JMXConnectorServer` bound to `localhost:<port>`

This all happens once per test class (`@BeforeAll`). After all tests run, the server is torn down (`@AfterAll`).

```java
@RegisterExtension
static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();
```

The server exposes helper methods used by tests:
- `getPort()` — the random port
- `getConnectionUrl()` — returns `"localhost:<port>"`, ready to pass to the `open` command
- `getServiceUrl()` — full `service:jmx:rmi:///jndi/rmi://...` URL
- `getMBeanServer()` — direct access for state reset between tests

### The test MBean

`TestMBean` / `TestMBeanImpl` is a simple MBean registered in the embedded server:

| Member | Type | Description |
|--------|------|-------------|
| `Name` | String attribute (read/write) | Initially `"default"` |
| `Count` | int attribute (read-only) | Tracks how many times `setName()` was called |
| `echo(String)` | operation | Returns `"echo:"` + input |
| `add(int, int)` | operation | Returns the sum |
| `reset()` | operation | Resets Name to `"default"` and Count to `0` |

This gives tests a controllable MBean with readable, writable, and read-only attributes, plus operations with different signatures and return types.

### Test pattern

Each test class follows this pattern:

```java
@RegisterExtension
static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();

private StringWriter resultOutput;
private StringWriter messageOutput;
private CommandCenter cc;

@BeforeEach
void setUp() throws Exception {
    resultOutput = new StringWriter();
    messageOutput = new StringWriter();
    cc = new CommandCenter(
        new WriterCommandOutput(resultOutput, messageOutput), null);
}

@AfterEach
void tearDown() {
    cc.close();
}

@Test
void testSomething() {
    cc.execute("open " + jmxServer.getConnectionUrl());
    cc.execute("bean test:type=TestMBean");
    cc.execute("get Name");
    assertThat(resultOutput.toString()).contains("default");
}
```

Key points:
- **Fresh CommandCenter per test** — avoids state leaking between tests
- **Two output writers** — `resultOutput` captures command values (what the user would see), `messageOutput` captures informational messages (prefixed with `#`)
- **Direct MBeanServer access** — some tests call `jmxServer.getMBeanServer().invoke(...)` in `@AfterEach` to reset the test MBean's state

## What is tested

### ConnectionLifecycleIT (6 tests)

Tests the JMX connection lifecycle managed by the `open` and `close` commands.

| Test | What it verifies |
|------|-----------------|
| `testOpenConnection` | A basic `open localhost:<port>` succeeds |
| `testOpenDisplaysConnectionInfo` | Running `open` with no arguments prints the current connection's service URL |
| `testCloseAndReconnect` | Opening, closing, then opening again works correctly |
| `testOpenWhenAlreadyConnected` | Opening a second connection without closing first is rejected |
| `testCloseWithoutOpen` | Closing when nothing is connected is a harmless no-op |
| `testOpenWithFullServiceUrl` | Opening with a full `service:jmx:rmi:///...` URL works |

### DomainBeanNavigationIT (6 tests)

Tests browsing and selecting JMX domains and beans — the core navigation model of jmxsh.

| Test | What it verifies |
|------|-----------------|
| `testListDomains` | `domains` lists available domains (`test`, `JMImplementation`) |
| `testSelectDomain` | `domain test` sets the current domain; `domain` (no args) displays it |
| `testListBeans` | `beans` lists beans in the selected domain |
| `testSelectBean` | `bean test:type=TestMBean` sets the current bean; `bean` (no args) displays it |
| `testListBeansWithDomainOption` | `beans -d test` lists beans in a specific domain without changing the current domain |
| `testInfoCommand` | `info` displays MBean metadata: attributes (Name, Count) and operations (echo, add, reset) |

### AttributeReadWriteIT (8 tests)

Tests reading and writing MBean attributes via the `get` and `set` commands.

| Test | What it verifies |
|------|-----------------|
| `testGetStringAttribute` | `get Name` returns `"default"` |
| `testGetIntAttribute` | `get Count` returns `0` |
| `testGetAllAttributes` | `get *` returns all attributes |
| `testGetWithBeanOption` | `get -b test:type=TestMBean Name` works without selecting a bean first |
| `testSetAndGetAttribute` | `set Name newValue` followed by `get Name` returns `"newValue"` |
| `testSetAndVerifyCount` | After `set Name x`, the read-only `Count` attribute reflects the state change |
| `testGetSimpleFormat` | `get -s Name` prints just the value (no `Name = ...` expression) |
| `testGetNonExistentAttribute` | Getting a non-existent attribute produces no result output |

### OperationInvocationIT (6 tests)

Tests invoking MBean operations via the `run` command.

| Test | What it verifies |
|------|-----------------|
| `testRunEchoOperation` | `run echo hello` returns `"echo:hello"` |
| `testRunAddOperation` | `run add 3 5` returns `8` |
| `testRunResetOperation` | After changing state, `run reset` restores defaults |
| `testRunWithBeanOption` | `run -b test:type=TestMBean echo world` works without selecting a bean first |
| `testRunNonExistentOperation` | Invoking a non-existent operation fails |
| `testRunWithWrongParamCount` | Passing the wrong number of parameters fails |

### ErrorHandlingIT (6 tests)

Tests that invalid usage produces clear failures rather than crashes.

| Test | What it verifies |
|------|-----------------|
| `testGetWithoutConnection` | `get Name` without an open connection fails |
| `testGetWithoutBean` | `get Name` with a connection but no bean selected fails |
| `testInvalidBeanName` | Using a malformed bean name fails |
| `testConnectToBadPort` | `open localhost:1` (invalid port) fails |
| `testDomainWithoutConnection` | `domains` without a connection fails |
| `testSetReadOnlyAttribute` | `set Count 5` on a read-only attribute fails |

### CommandChainingIT (5 tests)

Tests jmxsh's command-line parsing features.

| Test | What it verifies |
|------|-----------------|
| `testCommandChaining` | `open ... && domains && beans -d test` executes all commands in sequence |
| `testCommentHandling` | `domains # comment` executes the command, ignores the comment |
| `testFullLineComment` | `# this is a comment` is silently skipped |
| `testEscapedHash` | `\#` in a value is treated as a literal `#`, not a comment marker |
| `testEmptyCommand` | An empty string is silently accepted |

### VerboseLevelIT (4 tests)

Tests how the output mode (`SILENT`, `BRIEF`) controls what the user sees.

| Test | What it verifies |
|------|-----------------|
| `testBriefMessages` | In BRIEF mode (default), informational messages are shown with a `#` prefix |
| `testSilentSuppressesMessages` | In SILENT mode, the output mode is set without errors |
| `testSilentStillShowsValues` | In SILENT mode, command result values are still printed |
| `testBriefShowsShortErrors` | In BRIEF mode, errors show a short `#`-prefixed message, not a stack trace |

## Domains available in tests

Because the embedded server uses `MBeanServerFactory.createMBeanServer()` (not the platform MBeanServer), only two domains exist:

- **`JMImplementation`** — automatically created by the MBeanServer (contains `MBeanServerDelegate`)
- **`test`** — contains our `test:type=TestMBean`

This keeps tests deterministic — no interference from platform beans like `java.lang` or `java.nio`.
