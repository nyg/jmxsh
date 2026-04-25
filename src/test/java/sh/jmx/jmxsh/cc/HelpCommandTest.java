package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import sh.jmx.jmxsh.SelfRecordingCommand;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HelpCommandTest {
  @Mock
  private Session session;

  private HelpCommand command;
  private StringWriter writer;

  /** Set up objects to test */
  @BeforeEach
  void setUp() {
    command = new HelpCommand();
    writer = new StringWriter();
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
  }

  /**
   * Test execution with several options
   *
   * @throws IOException
   * @throws IntrospectionException
   */
  @Test
  void executeWithOption() throws Exception {
    command.setArgNames(List.of("a", "b"));
    CommandCenter cc = mock(CommandCenter.class);
    command.setCommandCenter(cc);

    when(cc.getCommandNames()).thenReturn(new HashSet<>(List.of("a", "b")));
    doReturn(new SelfRecordingCommand(new ArrayList<>())).when(cc).createCommand("a");
    doReturn(new SelfRecordingCommand(new ArrayList<>())).when(cc).createCommand("b");
    command.setSession(session);
    command.execute();
  }

  /**
   * Test execution without option
   *
   * @throws IOException
   */
  @Test
  void executeWithoutOption() throws Exception {
    CommandCenter cc = mock(CommandCenter.class);
    command.setCommandCenter(cc);
    when(cc.getCommandNames()).thenReturn(new HashSet<>(List.of("a", "b")));
    doReturn(new SelfRecordingCommand(new ArrayList<>())).when(cc).createCommand("a");
    doReturn(new SelfRecordingCommand(new ArrayList<>())).when(cc).createCommand("b");
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim())
        .isEqualTo("a        - desc" + System.lineSeparator() + "b        - desc");
  }
}
