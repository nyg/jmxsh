package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for JMX connection lifecycle: open, close, reconnect. */
class ConnectionLifecycleIT {

  @RegisterExtension static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();

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
  void testOpenConnection() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("close")).isTrue();
  }

  @Test
  void testOpenDisplaysConnectionInfo() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("open")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected service URL in output, got: " + result)
        .contains(jmxServer.getServiceUrl());
  }

  @Test
  void testCloseAndReconnect() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("close")).isTrue();
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl()))
        .as("Reconnection after close should succeed")
        .isTrue();
    // Verify the connection works by running a command
    assertThat(cc.execute("domains")).isTrue();
  }

  @Test
  void testOpenWhenAlreadyConnected() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl()))
        .as("Opening a second connection without closing should fail")
        .isFalse();
  }

  @Test
  void testCloseWithoutOpen() {
    assertThat(cc.execute("close"))
        .as("Closing without an open connection should succeed (no-op disconnect)")
        .isTrue();
  }

  @Test
  void testOpenWithFullServiceUrl() {
    assertThat(cc.execute("open " + jmxServer.getServiceUrl())).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("open")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected service URL in output, got: " + result)
        .contains(jmxServer.getServiceUrl());
  }
}
