package sh.jmx.jmxsh.cc;

import lombok.experimental.UtilityClass;

import sh.jmx.jmxsh.JavaProcessManager;
import sh.jmx.jmxsh.jdk9.Jdk9JavaProcessManager;

/**
 * Internal factory class to create JPM instance
 *
 */
@UtilityClass
public class JPMFactory {

  /** @return Java process manager instance */
  static JavaProcessManager createProcessManager() {
    return new Jdk9JavaProcessManager();
  }
}
