package sh.jmx.jmxsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.management.remote.JMXConnector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectionTest {
  /**
   * Test the object is constructed correctly
   *
   * @throws IOException
   */
  @Test
  void construction() throws Exception {
    JMXConnector con = mock(JMXConnector.class);
    Connection c = new Connection(con, SyntaxUtils.getUrl("localhost:9991", null));
    assertThat(c.connector()).isSameAs(con);

    when(con.getConnectionId()).thenReturn("xyz");
    assertThat(c.getConnectorId()).isEqualTo("xyz");
    verify(con).getConnectionId();
  }

  @Test
  void isAliveReturnsTrueWhenConnected() throws Exception {
    JMXConnector con = mock(JMXConnector.class);
    when(con.getConnectionId()).thenReturn("abc");
    Connection c = new Connection(con, SyntaxUtils.getUrl("localhost:9991", null));
    assertThat(c.isAlive()).isTrue();
  }

  @Test
  void isAliveReturnsFalseWhenBroken() throws Exception {
    JMXConnector con = mock(JMXConnector.class);
    when(con.getConnectionId()).thenThrow(new IOException("Connection reset"));
    Connection c = new Connection(con, SyntaxUtils.getUrl("localhost:9991", null));
    assertThat(c.isAlive()).isFalse();
  }
}
