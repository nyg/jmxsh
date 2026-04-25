package sh.jmx.jmxsh.cmd;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test case for {@link QuitCommand}
 *
 */
@ExtendWith(MockitoExtension.class)
class QuitCommandTest {
  @Mock
  private Session session;

  private QuitCommand command;

  /** Setup objects to test */
  @BeforeEach
  void setUp() {
    command = new QuitCommand();
  }

  /** @throws Exception */
  @Test
  void execute() throws Exception {
    when(session.getOutput()).thenReturn(new WriterCommandOutput(new StringWriter(), null));
    command.setSession(session);
    command.execute();
    verify(session).disconnect();
    verify(session).close();
  }
}
