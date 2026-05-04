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
import sh.jmx.jmxsh.attach.JavaProcessManager;

/**
 * JMX communication context. This class exists for the whole lifecycle of a command execution. It
 * is NOT thread safe. The caller(CommandCenter) makes sure all calls are synchronized.
 *
 */
@Getter
@Slf4j
public class Session {

  public static final int DEFAULT_RETRY_INTERVAL_SECONDS = 5;
  public static final int DEFAULT_MAX_RETRY_ATTEMPTS = 12;

  private Connection connection;
  private int retryIntervalSeconds = DEFAULT_RETRY_INTERVAL_SECONDS;
  private int maxRetryAttempts = DEFAULT_MAX_RETRY_ATTEMPTS;
  private JMXServiceURL lastUrl;
  private Map<String, Object> lastEnv;
  private String bean;
  private boolean closed;
  private String domain;
  private final CommandInput input;
  private final CommandOutput output;
  private final JavaProcessManager processManager;
  private OutputMode outputMode = OutputMode.BRIEF;

  public Session(CommandOutput output, CommandInput input, JavaProcessManager processManager) {
    Objects.requireNonNull(output, "Output can't be NULL");
    Objects.requireNonNull(processManager, "Process manager can't be NULL");
    this.output = new VerboseCommandOutput(output, () -> this.outputMode);
    this.input = input == null ? new UnimplementedCommandInput() : input;
    this.processManager = processManager;
  }

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

  public void connect(JMXServiceURL url, Map<String, Object> env) throws IOException {
    Objects.requireNonNull(url, "URL can't be NULL");
    if (connection != null) {
      throw new IllegalStateException("Session is already opened");
    }
    log.info("connecting to {}", url);
    JMXConnector connector = doConnect(url, env);
    connection = new Connection(connector, url);
    lastUrl = url;
    lastEnv = env;
    log.info("connected to {}", url);
  }

  public void disconnect() throws IOException {
    closeConnection();
    lastUrl = null;
    lastEnv = null;
    unsetDomain();
  }

  /**
   * Close the current connection without clearing reconnection state (lastUrl, lastEnv) or
   * domain/bean selection. Used internally during reconnection attempts.
   */
  private void closeConnection() {
    if (connection == null) {
      return;
    }
    log.info("disconnecting from JMX server");
    try {
      connection.close();
    } catch (IOException e) {
      // Connection is already broken — force cleanup
    } finally {
      connection = null;
    }
  }

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

  public boolean isConnected() {
    return connection != null;
  }

  /**
   * @return True if this session has enough information to attempt a reconnect (i.e., a previous
   *     connection was established and the URL was stored).
   */
  public boolean canReconnect() {
    return lastUrl != null;
  }

  public void setRetryParams(int intervalSeconds, int maxAttempts) {
    this.retryIntervalSeconds = intervalSeconds;
    this.maxRetryAttempts = maxAttempts;
  }

  /**
   * Check if the current connection is alive by performing a lightweight RMI call.
   *
   * @return True if the connection is alive, false if broken or not connected
   */
  public boolean isConnectionAlive() {
    if (connection == null) {
      return false;
    }
    return connection.isAlive();
  }

  /**
   * Attempt to reconnect to the last known JMX endpoint. Closes the current (broken) connection
   * without clearing domain/bean selection, then retries every {@code intervalSeconds} seconds up
   * to {@code maxAttempts} times. On failure, performs a full disconnect (clearing all state).
   *
   * @param intervalSeconds Seconds to wait between retry attempts
   * @param maxAttempts Maximum number of reconnection attempts
   * @return True if reconnection was successful
   */
  public boolean reconnect(int intervalSeconds, int maxAttempts) {
    closeConnection();

    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      output.printMessage(
          "Reconnection attempt %d/%d in %d seconds..."
              .formatted(attempt, maxAttempts, intervalSeconds));
      try {
        Thread.sleep(intervalSeconds * 1000L);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        output.printMessage("Reconnection interrupted.");
        fullDisconnect();
        return false;
      }
      try {
        JMXConnector connector = doConnect(lastUrl, lastEnv);
        connection = new Connection(connector, lastUrl);
        return true;
      } catch (IOException e) {
        // Will retry
      }
    }
    output.printMessage("All reconnection attempts failed.");
    fullDisconnect();
    return false;
  }

  /** Clear all connection state including reconnection parameters and domain/bean. */
  private void fullDisconnect() {
    closeConnection();
    lastUrl = null;
    lastEnv = null;
    unsetDomain();
  }

  public final void setBean(String bean) {
    this.bean = bean;
  }

  public final void setDomain(String domain) {
    Objects.requireNonNull(domain, "domain can't be NULL");
    this.domain = domain;
  }

  public final void setOutputMode(OutputMode outputMode) {
    Objects.requireNonNull(outputMode, "Output mode can't be NULL");
    this.outputMode = outputMode;
  }

  public void unsetDomain() {
    bean = null;
    domain = null;
  }
}
