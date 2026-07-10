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

  public String getDisplayName() {
    String host = url.getHost();
    if (host != null && !host.isBlank()) {
      return "%s:%d".formatted(host, url.getPort());
    }
    return url.toString();
  }

  public MBeanServerConnection getServerConnection() throws IOException {
    return connector.getMBeanServerConnection();
  }
}
