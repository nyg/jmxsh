package sh.jmx.jmxsh.cc;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.CommandFactory;

/**
 * CommandFactory implementation based on a Map of command suppliers
 *
 */
class TypeMapCommandFactory implements CommandFactory {
  private final Map<String, Supplier<Command>> commandSuppliers;

  TypeMapCommandFactory(Map<String, Supplier<Command>> commandSuppliers) {
    Objects.requireNonNull(commandSuppliers, "Command suppliers can't be NULL");
    this.commandSuppliers = Collections.unmodifiableMap(commandSuppliers);
  }

  @Override
  public Command createCommand(String commandName) {
    Objects.requireNonNull(commandName, "commandName can't be NULL");
    Supplier<Command> supplier = commandSuppliers.get(commandName);
    if (supplier == null) {
      throw new IllegalArgumentException(
          "Command " + commandName + " isn't valid, run help to see available commands");
    }
    return supplier.get();
  }

  @Override
  public Set<String> getCommandNames() {
    return Collections.unmodifiableSet(commandSuppliers.keySet());
  }
}
