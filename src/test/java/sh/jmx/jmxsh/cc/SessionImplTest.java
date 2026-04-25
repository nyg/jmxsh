package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.SyntaxUtils;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.jdk9.JavaProcessManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionImplTest {
  private JMXConnector con;

  private SessionImpl session;

  /** Set up objects to test */
  @BeforeEach
  void setUp() {
    con = mock(JMXConnector.class);
    session =
        new SessionImpl(
            new WriterCommandOutput(Writer.nullWriter()),
            null,
            new JavaProcessManager()) {
          @Override
          protected JMXConnector doConnect(JMXServiceURL url, Map<String, Object> env)
              throws IOException {
            return con;
          }
        };
  }

  /**
   * Verify connect() runs correctly
   *
   * @throws IOException
   */
  @Test
  void connect() throws Exception {
    session.connect(SyntaxUtils.getUrl("localhost:9991", null), null);
    Connection con = session.getConnection();
    assertThat(con.getUrl().toString())
        .isEqualTo("service:jmx:rmi:///jndi/rmi://localhost:9991/jmxrmi");
  }
}
