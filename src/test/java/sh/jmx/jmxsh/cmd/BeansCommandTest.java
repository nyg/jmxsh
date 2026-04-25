package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeansCommandTest {
  private static final String EOL = System.lineSeparator();

  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection conn;

  private BeansCommand command;
  private StringWriter writer;

  /** Set up testing connection */
  @BeforeEach
  void setUp() throws IOException {
    writer = new StringWriter();
    command = new BeansCommand();
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    when(session.getConnection()).thenReturn(connection);
    when(connection.getServerConnection()).thenReturn(conn);
  }

  /**
   * Test execution and get all beans
   *
   * @throws Exception
   */
  @Test
  void executeWithAllBeans() throws Exception {
    when(conn.getDomains()).thenReturn(new String[] {"a", "b"});
    when(conn.queryNames(new ObjectName("a:*"), null))
        .thenReturn(
                new HashSet<>(
                        List.of(new ObjectName("a:type=1"), new ObjectName("a:type=2"))));
    when(conn.queryNames(new ObjectName("b:*"), null))
        .thenReturn(new HashSet<>(List.of(new ObjectName("b:type=1"))));
    command.setSession(session);
    command.execute();
    assertThat(writer).hasToString("a:type=1" + EOL + "a:type=2" + EOL + "b:type=1" + EOL);
  }

  /**
   * Test execution where domain is set in session
   *
   * @throws Exception
   */
  @Test
  void executeWithDomainInSession() throws Exception {
    when(conn.queryNames(new ObjectName("b:*"), null))
        .thenReturn(new HashSet<ObjectName>(List.of(new ObjectName("b:type=1"))));
    when(session.getDomain()).thenReturn("b");
    command.setSession(session);
    command.execute();
    assertThat(writer).hasToString("b:type=1" + EOL);
  }

  /**
   * Test execution with an domain option
   *
   * @throws Exception
   */
  @Test
  void executeWithDomainOption() throws Exception {
    command.setDomain("b");
    when(conn.getDomains()).thenReturn(new String[] {"a", "b"});
    when(conn.queryNames(new ObjectName("b:*"), null))
        .thenReturn(new HashSet<ObjectName>(List.of(new ObjectName("b:type=1"))));
    command.setSession(session);
    command.execute();
    assertThat(writer).hasToString("b:type=1" + EOL);
  }

  /**
   * Test execution with domain NULL
   *
   * @throws Exception
   */
  @Test
  void executeWithNullDomain() throws Exception {
    command.setDomain("*");
    when(conn.getDomains()).thenReturn(new String[] {"a", "b"});
    when(conn.queryNames(new ObjectName("a:*"), null))
        .thenReturn(
                new HashSet<>(
                        List.of(new ObjectName("a:type=1"), new ObjectName("a:type=2"))));
    when(conn.queryNames(new ObjectName("b:*"), null))
        .thenReturn(new HashSet<>(List.of(new ObjectName("b:type=1"))));
    command.setSession(session);
    command.execute();
    assertThat(writer).hasToString("a:type=1" + EOL + "a:type=2" + EOL + "b:type=1" + EOL);
  }
}
