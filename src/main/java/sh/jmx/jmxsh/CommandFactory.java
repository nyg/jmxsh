package sh.jmx.jmxsh;

import java.util.Map;

/**
 * Factory which create Command instance based on command name
 *
 */
public interface CommandFactory {
  /**
   * Create new command instance
   *
   * @param name Command name
   * @return New instance of command object
   */
  Command createCommand(String name);

  /** @return Map of command types */
  Map<String, Class<? extends Command>> getCommandTypes();
}
