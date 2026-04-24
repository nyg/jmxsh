package org.cyclopsgroup.jmxterm.cc;

import lombok.experimental.UtilityClass;

import org.cyclopsgroup.jmxterm.JavaProcessManager;
import org.cyclopsgroup.jmxterm.jdk9.Jdk9JavaProcessManager;

/**
 * Internal factory class to create JPM instance
 *
 * @author <a href="mailto:jiaqi.guo@gmail.com">Jiaqi Guo</a>
 */
@UtilityClass
public class JPMFactory {

  /** @return Java process manager instance */
  static JavaProcessManager createProcessManager() {
    return new Jdk9JavaProcessManager();
  }
}
