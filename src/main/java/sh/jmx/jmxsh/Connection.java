package sh.jmx.jmxsh;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

/**
 * Holds a live JMX connector and the URL it was opened against.
 */
public record Connection(JMXConnector connector, JMXServiceURL url) {

  public void close() throws IOException {
    connector.close();
  }

  public String getConnectorId() throws IOException {
    return connector.getConnectionId();
  }

  public MBeanServerConnection getServerConnection() throws IOException {
    return connector.getMBeanServerConnection();
  }

  /**
   * Check if the connection is still alive by performing a lightweight call.
   *
   * @return True if the connection is responsive, false if it appears broken
   */
  public boolean isAlive() {
    try {
      connector.getConnectionId();
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
