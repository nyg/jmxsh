package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.SyntaxUtils;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenCommandTest {
  @Mock
  private Session session;
  @Mock
  private Connection connection;

  private OpenCommand command;
  private StringWriter writer;

  /** Set up command to test */
  @BeforeEach
  void setUp() {
    command = new OpenCommand();
    writer = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
  }

  /**
   * Test execution without URL
   *
   * @throws Exception
   */
  @Test
  void executeWithoutUrl() throws Exception {
    when(session.getConnection()).thenReturn(connection);
    when(connection.getConnectorId()).thenReturn("id");
    when(connection.url()).thenReturn(SyntaxUtils.getUrl("localhost:9991", null));
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim())
        .isEqualTo("id,service:jmx:rmi:///jndi/rmi://localhost:9991/jmxrmi");
  }

  /** @throws Exception */
  @Test
  void executeWithUrl() throws Exception {
    command.setUrl("xyz.cyclopsgroup.org:12345");
    command.setSession(session);
    command.execute();
    verify(session).connect(any(JMXServiceURL.class), isNull());
  }
}
