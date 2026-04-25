package sh.jmx.jmxsh.cc;

import java.io.IOException;
import java.util.Objects;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.Connection;

/**
 * Identifies a JMX connection
 *
 */
record ConnectionImpl(JMXConnector connector, JMXServiceURL url) implements Connection {

  ConnectionImpl {
    Objects.requireNonNull(connector, "connector");
    Objects.requireNonNull(url, "url");
  }

  @Override
  public JMXServiceURL getUrl() {
    return url;
  }

  void close() throws IOException {
    connector.close();
  }

  @Override
  public String getConnectorId() throws IOException {
    return connector.getConnectionId();
  }

  @Override
  public MBeanServerConnection getServerConnection() throws IOException {
    return connector.getMBeanServerConnection();
  }
}
