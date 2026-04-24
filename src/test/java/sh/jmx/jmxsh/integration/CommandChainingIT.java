package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for command chaining, comments, and empty commands. */
class CommandChainingIT {

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
            new javax.management.ObjectName("test:type=TestMBean"), "reset", null, null);
  }

  @Test
  void testCommandChaining() {
    assertThat(
            cc.execute(
                "open " + jmxServer.getConnectionUrl() + " && domains && beans -d test"))
        .isTrue();
    String result = resultWriter.toString();
    assertThat(result).as("Expected 'test' domain in output, got: " + result).contains("test");
    assertThat(result)
        .as("Expected 'JMImplementation' domain in output, got: " + result)
        .contains("JMImplementation");
    assertThat(result)
        .as("Expected 'test:type=TestMBean' in output, got: " + result)
        .contains("test:type=TestMBean");
  }

  @Test
  void testCommentHandling() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("domains # this is a comment")).isTrue();
    String result = resultWriter.toString();
    assertThat(result).as("Expected domains output, got: " + result).contains("test");
  }

  @Test
  void testFullLineComment() {
    assertThat(cc.execute("# this is a comment")).isTrue();
    assertThat(resultWriter.toString()).as("Full-line comment should produce no output").isEqualTo("");
  }

  @Test
  void testEscapedHash() {
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("set -b test:type=TestMBean Name value\\#with\\#hashes")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("get -b test:type=TestMBean Name")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected value with unescaped hashes, got: " + result)
        .contains("value#with#hashes");
  }

  @Test
  void testEmptyCommand() {
    assertThat(cc.execute("")).as("Empty command should succeed").isTrue();
    assertThat(resultWriter.toString()).as("Empty command should produce no output").isEqualTo("");
  }
}
