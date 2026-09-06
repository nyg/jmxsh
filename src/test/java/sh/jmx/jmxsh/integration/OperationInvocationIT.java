package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.StringWriter;
import java.util.stream.Stream;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Integration tests for invoking MBean operations via a real JMX connection. */
class OperationInvocationIT {

  @RegisterExtension static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();

  private CommandCenter cc;
  private StringWriter resultWriter;
  private StringWriter messageWriter;

  @BeforeEach
  void setUp() throws Exception {
    resetMBeanState();
    resultWriter = new StringWriter();
    messageWriter = new StringWriter();
    cc = new CommandCenter(new WriterCommandOutput(resultWriter, messageWriter), null);
  }

  @AfterEach
  void tearDown() throws Exception {
    cc.close();
    resetMBeanState();
  }

  private void resetMBeanState() throws Exception {
    jmxServer
        .getMBeanServer()
        .invoke(
            new javax.management.ObjectName("test:type=TestMBean"),
            "reset",
            null,
            null);
  }

  static Stream<Arguments> testRunOperation() {
    return Stream.of(
        arguments("run echo hello", "echo:hello"),
        arguments("run add 3 5", "8"),
        arguments("run -j [3,5] add", "8"),
        arguments("run -j '{\"p1\":3,\"p2\":5}' add", "8"),
        arguments("run at 2026-01-01T00:00:00Z", "at:2026-01-01T00:00:00Z"),
        arguments("run -j '[\"2026-01-01T00:00:00Z\"]' at", "at:2026-01-01T00:00:00Z"),
        arguments("run sum [1,2,3]", "6"),
        arguments("run -j [[1,2,3]] sum", "6"));
  }

  @ParameterizedTest
  @MethodSource
  void testRunOperation(String command, String expectedResult) {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute(command)).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected '%s' in the output of '%s', got: %s", expectedResult, command, resultWriter)
        .contains(expectedResult);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "run nonExistent",
      "run echo too many params",
      "run -j [3,5] add 7",
      "run -j nope add"})
  void testRunInvalidInvocation(String command) {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute(command)).as("Expected '%s' to fail", command).isFalse();
  }

  @Test
  void testRunWithBeanOption() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("run -b test:type=TestMBean echo world")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected 'echo:world' in output, got: " + resultWriter)
        .contains("echo:world");
  }

  @Test
  void testRunResetOperation() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("set Name changed")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("get Name")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected 'changed' after set, got: " + resultWriter)
        .contains("changed");

    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("run reset")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("get Name")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected 'default' after reset, got: " + resultWriter)
        .contains("default");
  }
}
