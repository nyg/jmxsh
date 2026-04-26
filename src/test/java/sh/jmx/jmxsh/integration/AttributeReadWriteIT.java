package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for reading and writing MBean attributes via a real JMX connection. */
class AttributeReadWriteIT {

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
  void testGetStringAttribute() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("get Name")).isTrue();
    assertThat(resultWriter.toString()).contains("default");
  }

  @Test
  void testGetIntAttribute() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("get Count")).isTrue();
    assertThat(resultWriter.toString()).contains("0");
  }

  @Test
  void testGetAllAttributes() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("get *")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected output to contain 'Name', got: " + result).contains("Name")
        .as("Expected output to contain 'Count', got: " + result).contains("Count");
  }

  @Test
  void testGetWithBeanOption() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("get -b test:type=TestMBean Name")).isTrue();
    assertThat(resultWriter.toString()).contains("default");
  }

  @Test
  void testSetAndGetAttribute() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("set -b test:type=TestMBean Name newValue")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("get -b test:type=TestMBean Name")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected 'newValue' in output, got: " + resultWriter)
        .contains("newValue");
  }

  @Test
  void testSetAndVerifyCount() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("set -b test:type=TestMBean Name changed")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("get -b test:type=TestMBean Count")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected Count to be 1 after one set, got: " + resultWriter)
        .contains("1");
  }

  @Test
  void testGetSimpleFormat() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("get -s -b test:type=TestMBean Name")).isTrue();
    String result = resultWriter.toString().trim();
    assertThat(result)
        .as("Expected simple format value 'default', got: " + result).contains("default")
        .as("Simple format should not contain 'Name =', got: " + result).doesNotContain("Name =");
  }

  @Test
  void testGetNonExistentAttribute() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    // GetCommand succeeds but produces no result output for unknown attributes
    assertThat(cc.execute("get -b test:type=TestMBean NonExistent")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected no result for non-existent attribute, got: " + resultWriter)
        .doesNotContain("NonExistent");
  }
}
