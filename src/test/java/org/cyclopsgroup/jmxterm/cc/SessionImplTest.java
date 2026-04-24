package org.cyclopsgroup.jmxterm.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.cyclopsgroup.jmxterm.Connection;
import org.cyclopsgroup.jmxterm.SyntaxUtils;
import org.cyclopsgroup.jmxterm.io.WriterCommandOutput;
import org.cyclopsgroup.jmxterm.jdk9.Jdk9JavaProcessManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case of {@link ConnectionImpl}
 *
 */
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
            new Jdk9JavaProcessManager()) {
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
