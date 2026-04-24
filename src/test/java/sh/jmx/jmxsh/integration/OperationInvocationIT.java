package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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

  @Test
  void testRunEchoOperation() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("run echo hello")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected 'echo:hello' in output, got: " + resultWriter)
        .contains("echo:hello");
  }

  @Test
  void testRunAddOperation() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("run add 3 5")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected '8' in output, got: " + resultWriter)
        .contains("8");
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

  @Test
  void testRunWithBeanOption() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("run -b test:type=TestMBean echo world")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected 'echo:world' in output, got: " + resultWriter)
        .contains("echo:world");
  }

  @Test
  void testRunNonExistentOperation() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("run nonExistent")).isFalse();
  }

  @Test
  void testRunWithWrongParamCount() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("run echo too many params"))
        .as("Expected failure when invoking echo with wrong number of parameters")
        .isFalse();
  }
}
