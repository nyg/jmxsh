package org.cyclopsgroup.jmxterm.cc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cyclopsgroup.jmxterm.Command;
import org.cyclopsgroup.jmxterm.CommandFactory;
import org.cyclopsgroup.jmxterm.cmd.BeanCommand;
import org.cyclopsgroup.jmxterm.cmd.BeansCommand;
import org.cyclopsgroup.jmxterm.cmd.CloseCommand;
import org.cyclopsgroup.jmxterm.cmd.DomainCommand;
import org.cyclopsgroup.jmxterm.cmd.DomainsCommand;
import org.cyclopsgroup.jmxterm.cmd.GetCommand;
import org.cyclopsgroup.jmxterm.cmd.InfoCommand;
import org.cyclopsgroup.jmxterm.cmd.JvmsCommand;
import org.cyclopsgroup.jmxterm.cmd.OpenCommand;
import org.cyclopsgroup.jmxterm.cmd.QuitCommand;
import org.cyclopsgroup.jmxterm.cmd.RunCommand;
import org.cyclopsgroup.jmxterm.cmd.SetCommand;
import org.cyclopsgroup.jmxterm.cmd.SubscribeCommand;
import org.cyclopsgroup.jmxterm.cmd.UnsubscribeCommand;
import org.cyclopsgroup.jmxterm.cmd.WatchCommand;

import picocli.CommandLine;

/**
 * Factory class of commands which knows how to create Command class with given command name.
 * Commands are discovered via their {@link CommandLine.Command} annotations.
 *
 */
class PredefinedCommandFactory implements CommandFactory {

  private static final List<Class<? extends Command>> COMMAND_CLASSES =
      List.of(
          BeanCommand.class,
          BeansCommand.class,
          CloseCommand.class,
          DomainCommand.class,
          DomainsCommand.class,
          GetCommand.class,
          InfoCommand.class,
          JvmsCommand.class,
          OpenCommand.class,
          QuitCommand.class,
          RunCommand.class,
          SetCommand.class,
          SubscribeCommand.class,
          UnsubscribeCommand.class,
          WatchCommand.class);

  private final CommandFactory delegate;

  PredefinedCommandFactory() {
    HashMap<String, Class<? extends Command>> commands = new HashMap<>();
    for (Class<? extends Command> commandClass : COMMAND_CLASSES) {
      CommandLine.Command annotation = commandClass.getAnnotation(CommandLine.Command.class);
      if (annotation == null) {
        throw new IllegalStateException(
            "@Command annotation missing on " + commandClass.getName());
      }
      commands.put(annotation.name(), commandClass);
      for (String alias : annotation.aliases()) {
        commands.put(alias, commandClass);
      }
    }
    commands.put("help", HelpCommand.class);
    delegate = new TypeMapCommandFactory(commands);
  }

  @Override
  public Command createCommand(String commandName) {
    return delegate.createCommand(commandName);
  }

  @Override
  public Map<String, Class<? extends Command>> getCommandTypes() {
    return delegate.getCommandTypes();
  }
}
