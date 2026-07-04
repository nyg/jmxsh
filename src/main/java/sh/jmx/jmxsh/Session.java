package sh.jmx.jmxsh;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import sh.jmx.jmxsh.io.CommandInput;
import sh.jmx.jmxsh.io.CommandOutput;
import sh.jmx.jmxsh.io.OutputMode;
import sh.jmx.jmxsh.io.UnimplementedCommandInput;
import sh.jmx.jmxsh.io.VerboseCommandOutput;
import sh.jmx.jmxsh.attach.JavaProcessManager;
import sh.jmx.jmxsh.utils.AliasStore;

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
  private final AliasStore aliasStore;
  private OutputMode outputMode = OutputMode.BRIEF;

  public Session(@NonNull CommandOutput output, CommandInput input,
      @NonNull JavaProcessManager processManager, @NonNull AliasStore aliasStore) {
    this.output = new VerboseCommandOutput(output, () -> this.outputMode);
    this.input = input == null ? new UnimplementedCommandInput() : input;
    this.processManager = processManager;
    this.aliasStore = aliasStore;
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

  public void connect(@NonNull JMXServiceURL url, Map<String, Object> env) throws IOException {
    if (connection != null) {
      throw new IllegalStateException("Session is already opened");
    }
    log.info("connecting to {}", url);
    JMXConnector connector = doConnect(url, env);
    connection = new Connection(connector, url);
    log.info("connected to {}", url);
  }

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

  public final void setBean(String bean) {
    this.bean = bean;
  }

  public final void setDomain(@NonNull String domain) {
    this.domain = domain;
  }

  public final void setOutputMode(@NonNull OutputMode outputMode) {
    this.outputMode = outputMode;
  }

  public void unsetDomain() {
    bean = null;
    domain = null;
  }
}
