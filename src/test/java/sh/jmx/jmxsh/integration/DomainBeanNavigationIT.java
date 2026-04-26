package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for browsing domains, beans, and MBean info via a real JMX connection. */
class DomainBeanNavigationIT {

  @RegisterExtension static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();

  private CommandCenter cc;
  private StringWriter resultWriter;
  private StringWriter messageWriter;

  @BeforeEach
  void setUp() {
    resultWriter = new StringWriter();
    messageWriter = new StringWriter();
    cc = new CommandCenter(new WriterCommandOutput(resultWriter, messageWriter), null);
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
  }

  @AfterEach
  void tearDown() {
    cc.close();
  }

  @Test
  void testListDomains() {
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("domains")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected 'test' domain, got: " + result).contains("test")
        .as("Expected 'JMImplementation' domain, got: " + result).contains("JMImplementation");
  }

  @Test
  void testSelectDomain() {
    assertThat(cc.execute("domain test")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("domain")).isTrue();
    String result = resultWriter.toString();
    assertThat(result).as("Expected current domain 'test', got: " + result).contains("test");
  }

  @Test
  void testListBeans() {
    assertThat(cc.execute("domain test")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("beans")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected 'test:type=TestMBean' in beans output, got: " + result)
        .contains("test:type=TestMBean");
  }

  @Test
  void testSelectBean() {
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("bean")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected current bean 'test:type=TestMBean', got: " + result)
        .contains("test:type=TestMBean");
  }

  @Test
  void testListBeansWithDomainOption() {
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("beans -d test")).isTrue();
    String result = resultWriter.toString();
    assertThat(result)
        .as("Expected 'test:type=TestMBean' in beans output, got: " + result)
        .contains("test:type=TestMBean");
  }

  @Test
  void testInfoCommand() {
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("info")).isTrue();
    String result = resultWriter.toString();
    // Verify attributes are listed
    assertThat(result)
        .as("Expected attribute 'Name' in info, got: " + result).contains("Name")
        .as("Expected attribute 'Count' in info, got: " + result).contains("Count")
        // Verify operations are listed
        .as("Expected operation 'echo' in info, got: " + result).contains("echo")
        .as("Expected operation 'add' in info, got: " + result).contains("add")
        .as("Expected operation 'reset' in info, got: " + result).contains("reset");
  }
}
