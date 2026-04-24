package sh.jmx.jmxsh.cc;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import sh.jmx.jmxsh.Connection;

/**
 * Identifies a JMX connection
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ConnectionImpl implements Connection {

  @Getter
  @NonNull
  private final JMXConnector connector;

  @Getter
  @NonNull
  private final JMXServiceURL url;

  /**
   * Close current connection
   *
   * @throws IOException Communication error
   */
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
