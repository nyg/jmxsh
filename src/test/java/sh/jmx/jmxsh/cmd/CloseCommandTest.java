package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import sh.jmx.jmxsh.MockSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link CloseCommand}
 *
 */
class CloseCommandTest {
  private CloseCommand command;

  private StringWriter output;

  /** Set up classes to test */
  @BeforeEach
  void setUp() {
    command = new CloseCommand();
    output = new StringWriter();
  }

  /**
   * Test execution
   *
   * @throws Exception
   */
  @Test
  void execute() throws Exception {
    MockSession session = new MockSession(output, null);
    command.setSession(session);
    command.execute();
    assertThat(session.isConnected()).isFalse();
  }
}
