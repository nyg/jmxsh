package sh.jmx.jmxsh.cc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.CommandFactory;
import sh.jmx.jmxsh.cmd.BeanCommand;
import sh.jmx.jmxsh.cmd.BeansCommand;
import sh.jmx.jmxsh.cmd.CloseCommand;
import sh.jmx.jmxsh.cmd.DomainCommand;
import sh.jmx.jmxsh.cmd.DomainsCommand;
import sh.jmx.jmxsh.cmd.GetCommand;
import sh.jmx.jmxsh.cmd.InfoCommand;
import sh.jmx.jmxsh.cmd.JvmsCommand;
import sh.jmx.jmxsh.cmd.OpenCommand;
import sh.jmx.jmxsh.cmd.QuitCommand;
import sh.jmx.jmxsh.cmd.RunCommand;
import sh.jmx.jmxsh.cmd.SetCommand;
import sh.jmx.jmxsh.cmd.SubscribeCommand;
import sh.jmx.jmxsh.cmd.UnsubscribeCommand;
import sh.jmx.jmxsh.cmd.WatchCommand;

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
