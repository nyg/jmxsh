package sh.jmx.jmxsh.integration;

import java.util.concurrent.ThreadLocalRandom;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that starts an embedded JMXMP connector server for integration testing. Unlike
 * {@link EmbeddedJmxServer} (RMI), JMXMP does not require an RMI registry — it listens directly on
 * a TCP port.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * @RegisterExtension
 * static EmbeddedJmxmpServer jmxmpServer = new EmbeddedJmxmpServer();
 * }</pre>
 */
public class EmbeddedJmxmpServer implements BeforeAllCallback, AfterAllCallback {

  private static final int MAX_PORT_RETRIES = 10;

  private MBeanServer mBeanServer;
  private JMXConnectorServer connectorServer;
  private int port;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    mBeanServer = MBeanServerFactory.createMBeanServer("test-jmxmp-" + hashCode());

    mBeanServer.registerMBean(
        new StandardMBean(new TestMBeanImpl(), TestMBean.class),
        new ObjectName("test:type=TestMBean"));

    // Try random ports until one succeeds (JMXMP binds directly, no RMI registry)
    for (int attempt = 0; attempt < MAX_PORT_RETRIES; attempt++) {
      int candidatePort = ThreadLocalRandom.current().nextInt(49152, 65536);
      try {
        JMXServiceURL url = new JMXServiceURL("service:jmx:jmxmp://localhost:" + candidatePort);
        connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
        connectorServer.start();
        this.port = candidatePort;
        return;
      } catch (Exception e) {
        // Port already in use or bind failure, retry with a different port
      }
    }
    throw new IllegalStateException(
        "Failed to start JMXMP connector server after " + MAX_PORT_RETRIES + " attempts");
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    if (connectorServer != null) {
      connectorServer.stop();
    }
    if (mBeanServer != null) {
      MBeanServerFactory.releaseMBeanServer(mBeanServer);
    }
  }

  /** @return The port the JMXMP connector server is listening on */
  public int getPort() {
    return port;
  }

  /** @return A connection URL in {@code jmxmp://localhost:PORT} shorthand format */
  public String getConnectionUrl() {
    return "jmxmp://localhost:" + port;
  }

  /** @return The full JMX service URL string */
  public String getServiceUrl() {
    return "service:jmx:jmxmp://localhost:" + port;
  }

  /** @return The underlying MBeanServer for direct manipulation in tests */
  public MBeanServer getMBeanServer() {
    return mBeanServer;
  }
}
