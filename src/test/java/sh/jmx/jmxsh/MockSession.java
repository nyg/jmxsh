package sh.jmx.jmxsh;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.jdk9.Jdk9JavaProcessManager;

/**
 * Mocked version of {@link Session} implementation for testing purpose only
 *
 */
public class MockSession extends Session {
  private boolean connected = true;

  private final MockConnection connection;

  /**
   * @param output Output writer
   * @param con MBean service connection
   */
  public MockSession(Writer output, MBeanServerConnection con) throws IOException {
    super(new WriterCommandOutput(output, null), null, new Jdk9JavaProcessManager());
    connection = new MockConnection(SyntaxUtils.getUrl("localhost:9991", null), con);
  }

  @Override
  public void connect(JMXServiceURL url, Map<String, Object> env) throws IOException {
    connected = true;
  }

  @Override
  public void disconnect() throws IOException {
    connected = false;
  }

  @Override
  public Connection getConnection() {
    return connection;
  }

  @Override
  public boolean isConnected() {
    return connected;
  }
}
