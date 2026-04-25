package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.management.MBeanServerConnection;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DomainsCommandTest {
  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection con;

  private DomainsCommand command;
  private StringWriter writer;

  /** Set up objects to test */
  @BeforeEach
  void setUp() throws IOException {
    command = new DomainsCommand();
    writer = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    when(session.getConnection()).thenReturn(connection);
    when(connection.getServerConnection()).thenReturn(con);
  }

  /**
   * Test normal execution
   *
   * @throws Exception
   */
  @Test
  void execution() throws Exception {
    when(con.getDomains()).thenReturn(new String[] {"a", "b"});
    command.setSession(session);
    command.execute();
    verify(con).getDomains();
    assertThat(writer.toString().trim()).isEqualTo("a" + System.lineSeparator() + "b");
  }
}
