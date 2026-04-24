package sh.jmx.jmxsh;

import java.io.IOException;

/**
 * Identifies a running JVM process
 *
 */
public interface JavaProcess {
  /** @return Display name of process */
  String getDisplayName();

  /** @return System process ID */
  int getProcessId();

  /** @return True if process is JMX manageable */
  boolean isManageable();

  /**
   * Start management agent
   *
   * @throws IOException Thrown when management agent couldn't be started
   */
  void startManagementAgent() throws IOException;

  /** @return Get connector URL */
  String toUrl();
}
