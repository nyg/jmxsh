package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for JMX connections over the JMXMP protocol. */
class JmxmpConnectionIT {

  @RegisterExtension static EmbeddedJmxmpServer jmxmpServer = new EmbeddedJmxmpServer();

  private CommandCenter cc;
  private StringWriter resultWriter;
  private StringWriter messageWriter;

  @BeforeEach
  void setUp() throws Exception {
    resultWriter = new StringWriter();
    messageWriter = new StringWriter();
    cc = new CommandCenter(new WriterCommandOutput(resultWriter, messageWriter), null);
  }

  @AfterEach
  void tearDown() {
    cc.close();
  }

  @Test
  void testOpenWithJmxmpShorthand() {
    assertThat(cc.execute("open " + jmxmpServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("close")).isTrue();
  }

  @Test
  void testOpenWithFullJmxmpServiceUrl() {
    assertThat(cc.execute("open " + jmxmpServer.getServiceUrl())).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("open")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected JMXMP service URL in output, got: " + result)
        .contains(jmxmpServer.getServiceUrl());
  }

  @Test
  void testListDomainsOverJmxmp() {
    assertThat(cc.execute("open " + jmxmpServer.getConnectionUrl())).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("domains")).isTrue();
    String result = resultWriter.toString();
    assertThat(result).contains("test");
  }

  @Test
  void testGetAttributeOverJmxmp() {
    assertThat(cc.execute("open " + jmxmpServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("get Name")).isTrue();
    String result = resultWriter.toString();
    assertThat(result).contains("default");
  }

  @Test
  void testCloseAndReconnectOverJmxmp() {
    assertThat(cc.execute("open " + jmxmpServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("close")).isTrue();
    assertThat(cc.execute("open " + jmxmpServer.getConnectionUrl()))
        .as("Reconnection after close should succeed")
        .isTrue();
    assertThat(cc.execute("domains")).isTrue();
  }
}
