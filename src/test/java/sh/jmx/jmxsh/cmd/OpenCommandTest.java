package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.MockSession;
import sh.jmx.jmxsh.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case to test {@link OpenCommand}
 *
 */
class OpenCommandTest {
  private OpenCommand command;

  /** Set up command to test */
  @BeforeEach
  void setUp() {
    command = new OpenCommand();
  }

  /**
   * Test execution without URL
   *
   * @throws Exception
   */
  @Test
  void executeWithoutUrl() throws Exception {
    StringWriter output = new StringWriter();
    Session session = new MockSession(output, null);
    command.setSession(session);
    command.execute();
    assertThat(output.toString().trim())
        .isEqualTo("id,service:jmx:rmi:///jndi/rmi://localhost:9991/jmxrmi");
  }

  /** @throws Exception */
  @Test
  void executeWithUrl() throws Exception {
    command.setUrl("xyz.cyclopsgroup.org:12345");
    Session session = new MockSession(new StringWriter(), null);
    session.disconnect();
    command.setSession(session);
    command.execute();
    assertThat(session.isConnected()).isTrue();
  }
}
