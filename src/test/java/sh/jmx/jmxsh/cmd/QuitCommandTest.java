package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.MockSession;
import sh.jmx.jmxsh.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link QuitCommand}
 *
 */
class QuitCommandTest {
  private QuitCommand command;

  private StringWriter output;

  /** Setup objects to test */
  @BeforeEach
  void setUp() {
    command = new QuitCommand();
    output = new StringWriter();
  }

  /** @throws Exception */
  @Test
  void execute() throws Exception {
    Session session = new MockSession(output, null);
    command.setSession(session);
    command.execute();
    assertThat(session.isConnected()).isFalse();
    assertThat(session.isClosed()).isTrue();
  }
}
