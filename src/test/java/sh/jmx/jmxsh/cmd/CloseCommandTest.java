package sh.jmx.jmxsh.cmd;

import static org.mockito.Mockito.verify;

import java.io.StringWriter;

import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of {@link CloseCommand}
 *
 */
@ExtendWith(MockitoExtension.class)
class CloseCommandTest {
  @Mock
  private Session session;

  private CloseCommand command;

  /** Set up classes to test */
  @BeforeEach
  void setUp() {
    command = new CloseCommand();
  }

  /**
   * Test execution
   *
   * @throws Exception
   */
  @Test
  void execute() throws Exception {
    StringWriter writer = new StringWriter();
    org.mockito.Mockito.when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    command.setSession(session);
    command.execute();
    verify(session).disconnect();
  }
}
