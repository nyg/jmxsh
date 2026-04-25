package sh.jmx.jmxsh.cc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

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

/**
 * Factory that registers all built-in commands by name and alias, each paired with a constructor
 * reference so no reflection is required at instantiation time.
 */
class PredefinedCommandFactory implements CommandFactory {

  private final CommandFactory delegate;

  PredefinedCommandFactory() {
    Map<String, Supplier<Command>> commands = new HashMap<>();
    commands.put("bean",        BeanCommand::new);
    commands.put("beans",       BeansCommand::new);
    commands.put("close",       CloseCommand::new);
    commands.put("domain",      DomainCommand::new);
    commands.put("domains",     DomainsCommand::new);
    commands.put("get",         GetCommand::new);
    commands.put("info",        InfoCommand::new);
    commands.put("jvms",        JvmsCommand::new);
    commands.put("open",        OpenCommand::new);
    commands.put("quit",        QuitCommand::new);
    commands.put("exit",        QuitCommand::new);
    commands.put("bye",         QuitCommand::new);
    commands.put("run",         RunCommand::new);
    commands.put("set",         SetCommand::new);
    commands.put("subscribe",   SubscribeCommand::new);
    commands.put("unsubscribe", UnsubscribeCommand::new);
    commands.put("watch",       WatchCommand::new);
    commands.put("help",        HelpCommand::new);
    delegate = new TypeMapCommandFactory(commands);
  }

  @Override
  public Command createCommand(String commandName) {
    return delegate.createCommand(commandName);
  }

  @Override
  public Set<String> getCommandNames() {
    return delegate.getCommandNames();
  }
}
