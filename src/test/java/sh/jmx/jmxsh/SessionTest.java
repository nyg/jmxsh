package sh.jmx.jmxsh;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Writer;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.attach.JavaProcessManager;
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
}
