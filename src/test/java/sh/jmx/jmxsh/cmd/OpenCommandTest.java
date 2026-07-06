package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.JmxUrl;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.utils.AliasStore;
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
  @Mock
  private AliasStore aliasStore;

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
    when(connection.url()).thenReturn(JmxUrl.parse("localhost:9991").toServiceUrl(null));
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim())
        .isEqualTo("id,service:jmx:rmi:///jndi/rmi://localhost:9991/jmxrmi");
  }

  /** @throws Exception */
  @Test
  void executeWithUrl() throws Exception {
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.resolve("xyz.cyclopsgroup.org:12345")).thenReturn("xyz.cyclopsgroup.org:12345");
    command.setUrl("xyz.cyclopsgroup.org:12345");
    command.setSession(session);
    command.execute();
    verify(session).connect(any(JMXServiceURL.class), isNull());
  }

  @Test
  void should_connect_to_resolved_target_when_url_is_alias() throws Exception {
    // Given
    StringWriter messageWriter = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(messageWriter));
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.resolve("my_server")).thenReturn("localhost:9991");
    command.setUrl("my_server");
    command.setSession(session);

    // When
    command.execute();

    // Then
    verify(session)
        .connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9991/jmxrmi"), null);
    assertThat(messageWriter.toString())
        .contains("Connection to my_server (localhost:9991) is opened");
  }
}
