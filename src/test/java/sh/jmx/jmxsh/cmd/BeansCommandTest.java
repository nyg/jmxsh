package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.MockSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BeansCommand}
 *
 */
class BeansCommandTest {
  private static final String EOL = System.getProperty("line.separator");

  private BeansCommand command;

  private MBeanServerConnection conn;

  private StringWriter output;

  /** Set up testing connection */
  @BeforeEach
  void setUp() {
    output = new StringWriter();
    command = new BeansCommand();
    conn = mock(MBeanServerConnection.class);
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
            new HashSet<ObjectName>(
                Arrays.asList(new ObjectName("a:type=1"), new ObjectName("a:type=2"))));
    when(conn.queryNames(new ObjectName("b:*"), null))
        .thenReturn(new HashSet<ObjectName>(Arrays.asList(new ObjectName("b:type=1"))));
    command.setSession(new MockSession(output, conn));
    command.execute();
    assertThat(output.toString()).isEqualTo("a:type=1" + EOL + "a:type=2" + EOL + "b:type=1" + EOL);
  }

  /**
   * Test execution where domain is set in session
   *
   * @throws Exception
   */
  @Test
  void executeWithDomainInSession() throws Exception {
    when(conn.getDomains()).thenReturn(new String[] {"a", "b"});
    when(conn.queryNames(new ObjectName("b:*"), null))
        .thenReturn(new HashSet<ObjectName>(Arrays.asList(new ObjectName("b:type=1"))));
    MockSession session = new MockSession(output, conn);
    session.setDomain("b");
    command.setSession(session);
    command.execute();
    assertThat(output.toString()).isEqualTo("b:type=1" + EOL);
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
        .thenReturn(new HashSet<ObjectName>(Arrays.asList(new ObjectName("b:type=1"))));
    command.setSession(new MockSession(output, conn));
    command.execute();
    assertThat(output.toString()).isEqualTo("b:type=1" + EOL);
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
            new HashSet<ObjectName>(
                Arrays.asList(new ObjectName("a:type=1"), new ObjectName("a:type=2"))));
    when(conn.queryNames(new ObjectName("b:*"), null))
        .thenReturn(new HashSet<ObjectName>(Arrays.asList(new ObjectName("b:type=1"))));
    MockSession session = new MockSession(output, conn);
    session.setDomain("b");
    command.setSession(session);
    command.execute();
    assertThat(output.toString()).isEqualTo("a:type=1" + EOL + "a:type=2" + EOL + "b:type=1" + EOL);
  }
}
