package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for error handling in various failure scenarios. */
class ErrorHandlingIT {

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
  void testGetWithoutConnection() {
    assertThat(cc.execute("get Name"))
        .as("Expected failure when getting attribute without a connection")
        .isFalse();
  }

  @Test
  void testGetWithoutBean() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("get Name"))
        .as("Expected failure when getting attribute without selecting a bean")
        .isFalse();
  }

  @Test
  void testInvalidBeanName() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean invalid:name:format:::"))
        .as("Expected failure for malformed bean name")
        .isFalse();
  }

  @Test
  void testConnectToBadPort() {
    assertThat(cc.execute("open localhost:1"))
        .as("Expected failure when connecting to an invalid port")
        .isFalse();
  }

  @Test
  void testDomainWithoutConnection() {
    assertThat(cc.execute("domains"))
        .as("Expected failure when listing domains without a connection")
        .isFalse();
  }

  @Test
  void testSetReadOnlyAttribute() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("set -b test:type=TestMBean Count 5"))
        .as("Expected failure when setting read-only attribute Count")
        .isFalse();
  }
}
