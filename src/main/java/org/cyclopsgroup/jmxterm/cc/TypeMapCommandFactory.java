package org.cyclopsgroup.jmxterm.cc;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

import java.util.Objects;
import org.cyclopsgroup.jmxterm.Command;
import org.cyclopsgroup.jmxterm.CommandFactory;

/**
 * CommandFactory implementation based on a Map of command types
 *
 */
public class TypeMapCommandFactory implements CommandFactory {
  private final Map<String, Class<? extends Command>> commandTypes;

  /** @param commandTypes Map of command types */
  public TypeMapCommandFactory(Map<String, Class<? extends Command>> commandTypes) {
    Objects.requireNonNull(commandTypes, "Command type can't be NULL");
    this.commandTypes = Collections.unmodifiableMap(commandTypes);
  }

  @Override
  public Command createCommand(String commandName) {
    Objects.requireNonNull(commandName, "commandName can't be NULL");
    Class<? extends Command> commandType = commandTypes.get(commandName);
    if (commandType == null) {
      throw new IllegalArgumentException(
          "Command " + commandName + " isn't valid, run help to see available commands");
    }
    try {
      return commandType.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("Can't instantiate instance", e);
    }
  }

  @Override
  public Map<String, Class<? extends Command>> getCommandTypes() {
    return commandTypes;
  }
}
