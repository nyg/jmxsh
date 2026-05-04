package sh.jmx.jmxsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.attach.JavaProcessManager;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionTest {

  @Mock
  private JMXConnector con;

  private Session session;

  @BeforeEach
  void setUp() {
    session = new Session(new WriterCommandOutput(Writer.nullWriter()), null, new JavaProcessManager()) {
      @Override
      protected JMXConnector doConnect(JMXServiceURL url, Map<String, Object> env) {
        return con;
      }
    };
  }

  @Test
  void connect() throws Exception {
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    Connection connection = session.getConnection();
    assertThat(connection.url()).hasToString("service:jmx:rmi:///jndi/rmi://localhost:9991/jmxrmi");
  }

  @Test
  void connectStoresReconnectionParameters() throws Exception {
    assertThat(session.canReconnect()).isFalse();
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    assertThat(session.canReconnect()).isTrue();
  }

  @Test
  void disconnectClearsReconnectionParameters() throws Exception {
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    session.disconnect();
    assertThat(session.canReconnect()).isFalse();
    assertThat(session.isConnected()).isFalse();
  }

  @Test
  void disconnectClearsDomainAndBean() throws Exception {
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    session.setDomain("java.lang");
    session.setBean("java.lang:type=Runtime");
    session.disconnect();
    assertThat(session.getDomain()).isNull();
    assertThat(session.getBean()).isNull();
  }

  @Test
  void isConnectionAliveWhenConnected() throws Exception {
    when(con.getConnectionId()).thenReturn("test-id");
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    assertThat(session.isConnectionAlive()).isTrue();
  }

  @Test
  void isConnectionAliveWhenBroken() throws Exception {
    when(con.getConnectionId()).thenThrow(new IOException("broken"));
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    assertThat(session.isConnectionAlive()).isFalse();
  }

  @Test
  void isConnectionAliveWhenNotConnected() {
    assertThat(session.isConnectionAlive()).isFalse();
  }

  @Test
  void reconnectSucceedsOnFirstAttempt() throws Exception {
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    session.setDomain("java.lang");
    session.setBean("java.lang:type=Runtime");

    boolean result = session.reconnect(0, 1);

    assertThat(result).isTrue();
    assertThat(session.isConnected()).isTrue();
    // Domain and bean are preserved across reconnect
    assertThat(session.getDomain()).isEqualTo("java.lang");
    assertThat(session.getBean()).isEqualTo("java.lang:type=Runtime");
  }

  @Test
  void reconnectFailsAfterMaxAttempts() throws Exception {
    AtomicInteger connectCount = new AtomicInteger(0);
    var reconnectSession =
        new Session(new WriterCommandOutput(Writer.nullWriter()), null, new JavaProcessManager()) {
          @Override
          protected JMXConnector doConnect(JMXServiceURL url, Map<String, Object> env)
              throws IOException {
            if (connectCount.getAndIncrement() == 0) {
              return con; // initial connect succeeds
            }
            throw new IOException("Connection refused");
          }
        };

    reconnectSession.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    assertThat(reconnectSession.canReconnect()).isTrue();

    boolean result = reconnectSession.reconnect(0, 2);
    assertThat(result).isFalse();
    assertThat(reconnectSession.isConnected()).isFalse();
    assertThat(reconnectSession.canReconnect()).isFalse();
  }

  @Test
  void reconnectSucceedsAfterTransientFailure() throws Exception {
    AtomicInteger connectCount = new AtomicInteger(0);
    JMXConnector freshCon = org.mockito.Mockito.mock(JMXConnector.class);

    var reconnectSession =
        new Session(new WriterCommandOutput(Writer.nullWriter()), null, new JavaProcessManager()) {
          @Override
          protected JMXConnector doConnect(JMXServiceURL url, Map<String, Object> env)
              throws IOException {
            int count = connectCount.getAndIncrement();
            if (count == 0) {
              return con; // initial connect succeeds
            } else if (count == 1) {
              throw new IOException("Transient failure"); // first reconnect fails
            }
            return freshCon; // second reconnect succeeds
          }
        };

    reconnectSession.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    boolean result = reconnectSession.reconnect(0, 3);

    assertThat(result).isTrue();
    assertThat(reconnectSession.isConnected()).isTrue();
    assertThat(reconnectSession.canReconnect()).isTrue();
  }
}
