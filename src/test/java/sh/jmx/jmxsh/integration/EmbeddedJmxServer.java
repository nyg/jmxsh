package sh.jmx.jmxsh.integration;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
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
 * JUnit 5 extension that starts an embedded JMX connector server for integration testing. The
 * server listens on a random available port and registers a {@link TestMBeanImpl} under the {@code
 * test:type=TestMBean} ObjectName.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * @RegisterExtension
 * static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();
 * }</pre>
 */
public class EmbeddedJmxServer implements BeforeAllCallback, AfterAllCallback {

  private static final int MAX_PORT_RETRIES = 10;

  private MBeanServer mBeanServer;
  private JMXConnectorServer connectorServer;
  private Registry registry;
  private int port;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    // Create RMI registry with retry to avoid TOCTOU race on port selection
    registry = createRegistryOnRandomPort();

    // Create a dedicated MBeanServer (not the platform one, to avoid side effects)
    mBeanServer = MBeanServerFactory.createMBeanServer("test-" + port);

    // Register test MBean
    mBeanServer.registerMBean(
        new StandardMBean(new TestMBeanImpl(), TestMBean.class),
        new ObjectName("test:type=TestMBean"));

    // Start JMX connector server
    JMXServiceURL url =
        new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
    connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
    connectorServer.start();
  }

  private Registry createRegistryOnRandomPort() throws Exception {
    for (int attempt = 0; attempt < MAX_PORT_RETRIES; attempt++) {
      int candidatePort = ThreadLocalRandom.current().nextInt(49152, 65536);
      try {
        Registry reg = LocateRegistry.createRegistry(candidatePort);
        this.port = candidatePort;
        return reg;
      } catch (ExportException _) {
        // Port already in use, retry with a different port
      }
    }
    throw new IllegalStateException(
        "Failed to create RMI registry after " + MAX_PORT_RETRIES + " attempts");
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    if (connectorServer != null) {
      connectorServer.stop();
    }
    if (registry != null) {
      UnicastRemoteObject.unexportObject(registry, true);
    }
    if (mBeanServer != null) {
      MBeanServerFactory.releaseMBeanServer(mBeanServer);
    }
  }

  /** @return The port the JMX connector server is listening on */
  public int getPort() {
    return port;
  }

  /** @return A connection URL in {@code localhost:PORT} format suitable for the open command */
  public String getConnectionUrl() {
    return "localhost:" + port;
  }

  /** @return The full JMX service URL string */
  public String getServiceUrl() {
    return "service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi";
  }

  /** @return The underlying MBeanServer for direct manipulation in tests */
  public MBeanServer getMBeanServer() {
    return mBeanServer;
  }
}
