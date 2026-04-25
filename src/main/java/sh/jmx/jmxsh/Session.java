package sh.jmx.jmxsh;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import sh.jmx.jmxsh.io.CommandInput;
import sh.jmx.jmxsh.io.CommandOutput;
import sh.jmx.jmxsh.io.OutputMode;
import sh.jmx.jmxsh.io.UnimplementedCommandInput;
import sh.jmx.jmxsh.io.VerboseCommandOutput;
import sh.jmx.jmxsh.jdk9.JavaProcessManager;

/**
 * JMX communication context. This class exists for the whole lifecycle of a command execution. It
 * is NOT thread safe. The caller(CommandCenter) makes sure all calls are synchronized.
 *
 */
@Getter
@Slf4j
public class Session {

  private Connection connection;
  private String bean;
  private boolean closed;
  private String domain;
  private final CommandInput input;
  private final CommandOutput output;
  private final JavaProcessManager processManager;
  private OutputMode outputMode = OutputMode.BRIEF;

  /**
   * @param output Output destination
   * @param input Command line input
   * @param processManager Process manager
   */
  public Session(CommandOutput output, CommandInput input, JavaProcessManager processManager) {
    Objects.requireNonNull(output, "Output can't be NULL");
    Objects.requireNonNull(processManager, "Process manager can't be NULL");
    this.output = new VerboseCommandOutput(output, () -> this.outputMode);
    this.input = input == null ? new UnimplementedCommandInput() : input;
    this.processManager = processManager;
  }

  /** Close JMX terminal console. Supposedly, process terminates after this call */
  public void close() {
    if (closed) {
      return;
    }
    try {
      disconnect();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    closed = true;
  }

  /**
   * Connect to MBean server
   *
   * @param url URL to connect
   * @param env Environment variables
   * @throws IOException allows IO exceptions.
   */
  public void connect(JMXServiceURL url, Map<String, Object> env) throws IOException {
    Objects.requireNonNull(url, "URL can't be NULL");
    if (connection != null) {
      throw new IllegalStateException("Session is already opened");
    }
    log.info("connecting to {}", url);
    JMXConnector connector = doConnect(url, env);
    connection = new Connection(connector, url);
    log.info("connected to {}", url);
  }

  /**
   * Close JMX connector
   *
   * @throws IOException Thrown when connection can't be closed
   */
  public void disconnect() throws IOException {
    if (connection == null) {
      return;
    }
    log.info("disconnecting from JMX server");
    try {
      connection.close();
    } finally {
      connection = null;
    }
  }

  /**
   * Connect to MBean server
   *
   * @param url MBean server URL
   * @param env A map of environment
   * @return Connector that holds connection to MBean server
   * @throws IOException Network errors
   */
  protected JMXConnector doConnect(JMXServiceURL url, Map<String, Object> env) throws IOException {
    return JMXConnectorFactory.connect(url, env);
  }

  public Connection getConnection() {
    if (connection == null) {
      throw new IllegalStateException(
          "Connection isn't open yet. Run open command to open a connection");
    }
    return connection;
  }

  public final boolean isClosed() {
    return closed;
  }

  /** @return True if there's a open connection to JMX server */
  public boolean isConnected() {
    return connection != null;
  }

  /**
   * Set current selected bean
   *
   * @param bean Bean to select
   */
  public final void setBean(String bean) {
    this.bean = bean;
  }

  /**
   * Set current selected domain
   *
   * @param domain Domain to select
   */
  public final void setDomain(String domain) {
    Objects.requireNonNull(domain, "domain can't be NULL");
    this.domain = domain;
  }

  /** @param outputMode Output mode (BRIEF or SILENT) */
  public final void setOutputMode(OutputMode outputMode) {
    Objects.requireNonNull(outputMode, "Output mode can't be NULL");
    this.outputMode = outputMode;
  }

  /** Set domain and bean to be NULL */
  public void unsetDomain() {
    bean = null;
    domain = null;
  }
}
