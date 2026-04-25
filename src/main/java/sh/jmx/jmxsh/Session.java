package sh.jmx.jmxsh;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;

import javax.management.remote.JMXServiceURL;

import lombok.Getter;
import sh.jmx.jmxsh.io.CommandInput;
import sh.jmx.jmxsh.io.CommandOutput;
import sh.jmx.jmxsh.io.UnimplementedCommandInput;
import sh.jmx.jmxsh.io.VerboseCommandOutput;
import sh.jmx.jmxsh.io.VerboseCommandOutputConfig;
import sh.jmx.jmxsh.io.VerboseLevel;
import sh.jmx.jmxsh.jdk9.JavaProcessManager;

/**
 * JMX communication context. This class exists for the whole lifecycle of a command execution. It
 * is NOT thread safe. The caller(CommandCenter) makes sure all calls are synchronized.
 *
 */
public abstract class Session implements VerboseCommandOutputConfig {

  @Getter
  private String bean;
  private boolean closed;
  @Getter
  private String domain;
  @Getter
  private final CommandInput input;
  private final CommandOutput output;

  @Getter
  private final JavaProcessManager processManager;

  private VerboseLevel verboseLevel = VerboseLevel.BRIEF;

  /**
   * @param output Output destination
   * @param input Command line input
   * @param processManager Process manager
   */
  protected Session(CommandOutput output, CommandInput input, JavaProcessManager processManager) {
    Objects.requireNonNull(output, "Output can't be NULL");
    Objects.requireNonNull(processManager, "Process manager can't be NULL");
    this.output = new VerboseCommandOutput(output, this);
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
  public abstract void connect(JMXServiceURL url, Map<String, Object> env) throws IOException;

  /**
   * Close JMX connector
   *
   * @throws IOException Thrown when connection can't be closed
   */
  public abstract void disconnect() throws IOException;

  /** @return Current open JMX server connection */
  public abstract Connection getConnection();

    /** @return Command output destination */
  public final CommandOutput getOutput() {
    return output;
  }

    @Override
  public VerboseLevel getVerboseLevel() {
    return verboseLevel;
  }

  /** @return True if {@link #close()} has been called */
  public final boolean isClosed() {
    return closed;
  }

  /** @return True if there's a open connection to JMX server */
  public abstract boolean isConnected();

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

  /** @param verboseLevel Level of verbose */
  public final void setVerboseLevel(VerboseLevel verboseLevel) {
    Objects.requireNonNull(verboseLevel, "Verbose level can't be NULL");
    this.verboseLevel = verboseLevel;
  }

  /** Set domain and bean to be NULL */
  public void unsetDomain() {
    bean = null;
    domain = null;
  }
}
