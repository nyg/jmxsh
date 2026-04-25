package sh.jmx.jmxsh;

import java.util.Set;

public interface CommandFactory {
  Command createCommand(String name);

  Set<String> getCommandNames();
}
