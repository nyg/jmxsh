package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.CommandInput;
import sh.jmx.jmxsh.io.JlineCommandInput;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatchCommandTest {

  private static final String BEAN_NAME = "a:type=x";

  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection con;

  private WatchCommand command;
  private SignalingWriter writer;

  @BeforeEach
  void setUp() {
    command = new WatchCommand();
    writer = new SignalingWriter();
  }

  private void mockConnectedSession() throws Exception {
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    lenient().when(session.getDomain()).thenReturn("a");
    lenient().when(session.getBean()).thenReturn(BEAN_NAME);
    lenient().when(session.getConnection()).thenReturn(connection);
    lenient().when(connection.getServerConnection()).thenReturn(con);
  }

  private void awaitOutputContains(String expected) throws InterruptedException {
    writer.awaitContains(expected, 5000);
    assertThat(writer.toString()).contains(expected);
  }

  /**
   * A {@link StringWriter} that lets a test block until the text written from a background thread
   * contains an expected substring, using {@link Object#wait(long)} instead of polling with sleeps.
   */
  private static final class SignalingWriter extends StringWriter {
    @Override
    public synchronized void write(String str) {
      super.write(str);
      notifyAll();
    }

    synchronized void awaitContains(String expected, long timeoutMillis) throws InterruptedException {
      long deadline = System.currentTimeMillis() + timeoutMillis;
      while (!toString().contains(expected)) {
        long remaining = deadline - System.currentTimeMillis();
        if (remaining <= 0) {
          return;
        }
        wait(remaining);
      }
    }
  }

  @Test
  void executeReportModeFetchesAttributesInBulk() throws Exception {
    mockConnectedSession();
    when(con.getAttributes(new ObjectName(BEAN_NAME), new String[] {"x", "y"}))
        .thenReturn(new AttributeList(List.of(new Attribute("x", 1), new Attribute("y", 2))));

    command.setAttributes(List.of("x", "y"));
    command.setReport(true);
    command.setStopAfter(1);
    command.setRefreshInterval(5);
    command.setSession(session);
    command.execute();

    awaitOutputContains("1, 2");
  }

  @Test
  void executeReportModeWithOutputFormat() throws Exception {
    mockConnectedSession();
    when(con.getAttributes(new ObjectName(BEAN_NAME), new String[] {"x", "y"}))
        .thenReturn(new AttributeList(List.of(new Attribute("x", 1), new Attribute("y", 2))));

    command.setAttributes(List.of("x", "y"));
    command.setOutputFormat("%s|%s");
    command.setReport(true);
    command.setStopAfter(1);
    command.setRefreshInterval(5);
    command.setSession(session);
    command.execute();

    awaitOutputContains("1|2");
  }

  @Test
  void executeReportModeFallsBackWhenAttributeMissingFromBulkResult() throws Exception {
    mockConnectedSession();
    ObjectName name = new ObjectName(BEAN_NAME);
    when(con.getAttributes(name, new String[] {"x"})).thenReturn(new AttributeList());
    when(con.getAttribute(name, "x")).thenThrow(new AttributeNotFoundException("x"));

    command.setAttributes(List.of("x"));
    command.setReport(true);
    command.setStopAfter(1);
    command.setRefreshInterval(5);
    command.setSession(session);
    command.execute();

    awaitOutputContains("AttributeNotFoundException");
  }

  @Test
  void executeReportModeWithNowPseudoAttribute() throws Exception {
    mockConnectedSession();

    command.setAttributes(List.of("%now"));
    command.setReport(true);
    command.setStopAfter(1);
    command.setRefreshInterval(5);
    command.setSession(session);
    command.execute();

    awaitOutputContains("Z");
  }

  @Test
  void executeInteractiveModeWritesTrailingNewlineToTerminalNotStdout() throws Exception {
    mockConnectedSession();
    StringWriter terminalOutput = new StringWriter();
    Terminal terminal = mock(Terminal.class);
    LineReaderImpl console = mock(LineReaderImpl.class);
    when(console.getTerminal()).thenReturn(terminal);
    when(terminal.writer()).thenReturn(new PrintWriter(terminalOutput, true));
    when(session.getInput()).thenReturn(new JlineCommandInput(console, () -> "> "));

    InputStream originalIn = System.in;
    PrintStream originalOut = System.out;
    ByteArrayOutputStream capturedStdout = new ByteArrayOutputStream();
    System.setIn(new ByteArrayInputStream(new byte[] {'\n'}));
    System.setOut(new PrintStream(capturedStdout, true));
    try {
      command.setAttributes(List.of("%now"));
      command.setRefreshInterval(60);
      command.setSession(session);
      command.execute();
    } finally {
      System.setIn(originalIn);
      System.setOut(originalOut);
    }

    assertThat(capturedStdout.toString()).isEmpty();
    assertThat(terminalOutput.toString()).contains(System.lineSeparator());
  }

  @Test
  void executeReportWithoutStopAfterThrows() {
    command.setReport(true);
    command.setSession(session);

    assertThatThrownBy(command::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("--report");
  }

  @Test
  void executeWithoutDomainThrows() {
    when(session.getConnection()).thenReturn(connection);
    command.setSession(session);

    assertThatThrownBy(command::execute)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("domain");
  }

  @Test
  void executeWithoutBeanThrows() {
    when(session.getConnection()).thenReturn(connection);
    when(session.getDomain()).thenReturn("a");
    command.setSession(session);

    assertThatThrownBy(command::execute)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("bean");
  }

  @Test
  void executeWithoutConsoleInputThrows() throws Exception {
    when(session.getConnection()).thenReturn(connection);
    when(session.getDomain()).thenReturn("a");
    when(session.getBean()).thenReturn(BEAN_NAME);
    when(connection.getServerConnection()).thenReturn(con);
    CommandInput mockedCommandInput = mock(CommandInput.class);
    when(session.getInput()).thenReturn(mockedCommandInput);
    command.setSession(session);

    assertThatThrownBy(command::execute).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void suggestArgumentWithBean() throws Exception {
    when(session.getBean()).thenReturn(BEAN_NAME);
    when(session.getConnection()).thenReturn(connection);
    when(connection.getServerConnection()).thenReturn(con);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanAttributeInfo attributeInfo = mock(MBeanAttributeInfo.class);
    when(con.getMBeanInfo(new ObjectName(BEAN_NAME))).thenReturn(beanInfo);
    when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attributeInfo});
    when(attributeInfo.getName()).thenReturn("x");
    command.setSession(session);

    assertThat(command.suggestArgument(null)).containsExactly("x", "%now");
  }

  @Test
  void suggestArgumentWithNoBean() {
    when(session.getBean()).thenReturn(null);
    command.setSession(session);

    assertThat(command.suggestArgument(null)).isEmpty();
  }

  @Test
  void invalidRefreshIntervalThrows() {
    assertThatThrownBy(() -> command.setRefreshInterval(0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void invalidStopAfterThrows() {
    assertThatThrownBy(() -> command.setStopAfter(-1))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
