package sh.jmx.jmxsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.attach.JavaProcessManager;
import sh.jmx.jmxsh.utils.AliasStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionTest {

  @Mock
  private JMXConnector con;

  @Mock
  private AliasStore aliasStore;

  private Session session;

  @BeforeEach
  void setUp() {
    session = new Session(new WriterCommandOutput(Writer.nullWriter()), null, new JavaProcessManager(), aliasStore) {
      @Override
      protected JMXConnector doConnect(JMXServiceURL url, Map<String, Object> env) {
        return con;
      }
    };
  }

  @Test
  void connect() throws Exception {
    session.connect(JmxUrl.parse("localhost:9991").toServiceUrl(null), null);
    Connection connection = session.getConnection();
    assertThat(connection.url()).hasToString("service:jmx:rmi:///jndi/rmi://localhost:9991/jmxrmi");
  }

  @Test
  void constructorThrowsWhenOutputNull() {
    JavaProcessManager processManager = new JavaProcessManager();
    assertThatThrownBy(() -> new Session(null, null, processManager, aliasStore))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorThrowsWhenProcessManagerNull() {
    WriterCommandOutput output = new WriterCommandOutput(Writer.nullWriter());
    assertThatThrownBy(() -> new Session(output, null, null, aliasStore))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorThrowsWhenAliasStoreNull() {
    WriterCommandOutput output = new WriterCommandOutput(Writer.nullWriter());
    JavaProcessManager processManager = new JavaProcessManager();
    assertThatThrownBy(() -> new Session(output, null, processManager, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void connectThrowsWhenUrlNull() {
    assertThatThrownBy(() -> session.connect(null, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void should_clear_connection_domain_and_bean_when_disconnected() throws Exception {
    // Given
    session.connect(JmxUrl.parse("localhost:9991").toServiceUrl(null), null);
    session.setDomain("java.lang");
    session.setBean("java.lang:type=Memory");

    // When
    session.disconnect();

    // Then
    assertThat(session.isConnected()).isFalse();
    assertThat(session.getDomain()).isNull();
    assertThat(session.getBean()).isNull();
    verify(con).close();
  }

  @Test
  void should_clear_state_and_not_throw_when_connector_close_fails() throws Exception {
    // Given
    session.connect(JmxUrl.parse("localhost:9991").toServiceUrl(null), null);
    session.setDomain("java.lang");
    session.setBean("java.lang:type=Memory");
    doThrow(new IOException("Connection refused")).when(con).close();

    // When
    assertThatCode(session::disconnect).doesNotThrowAnyException();

    // Then
    assertThat(session.isConnected()).isFalse();
    assertThat(session.getDomain()).isNull();
    assertThat(session.getBean()).isNull();
  }

  @Test
  void should_close_without_throwing_when_connector_close_fails() throws Exception {
    // Given
    session.connect(JmxUrl.parse("localhost:9991").toServiceUrl(null), null);
    doThrow(new IOException("Connection refused")).when(con).close();

    // When
    assertThatCode(session::close).doesNotThrowAnyException();

    // Then
    assertThat(session.isClosed()).isTrue();
    assertThat(session.isConnected()).isFalse();
  }

  @Test
  void setDomainThrowsWhenNull() {
    assertThatThrownBy(() -> session.setDomain(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void setOutputModeThrowsWhenNull() {
    assertThatThrownBy(() -> session.setOutputMode(null))
        .isInstanceOf(NullPointerException.class);
  }
}
