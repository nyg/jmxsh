package sh.jmx.jmxsh.cc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.NonNull;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "help",
    description = "Display available commands or usage of a command",
    footer =
        "Run \"help [command1] [command2] ...\" to display usage or certain command(s). Help without argument shows list of available commands")
public class HelpCommand extends sh.jmx.jmxsh.Command {
  private List<String> argNames = Collections.emptyList();

  private CommandCenter commandCenter;

  @Override
  public void execute() {
    Objects.requireNonNull(commandCenter, "Command center hasn't been set yet");
    if (argNames.isEmpty()) {
      List<String> commandNames = commandCenter.getCommandNames().stream().sorted().toList();
      getSession().getOutput().printMessage("The following commands are available to use:");
      for (String commandName : commandNames) {
        sh.jmx.jmxsh.Command cmd =
            commandCenter.createCommand(commandName);
        CommandLine cl = new CommandLine(cmd);
        String[] desc = cl.getCommandSpec().usageMessage().description();
        String description = desc.length > 0 ? String.join(" ", desc) : "";
        getSession().getOutput().println("%-11s - %s".formatted(commandName, description));
      }
    } else {
      for (String argName : argNames) {
        if (!commandCenter.getCommandNames().contains(argName)) {
          throw new IllegalArgumentException("Command " + argName + " is not found");
        }
        sh.jmx.jmxsh.Command cmd =
            commandCenter.createCommand(argName);
        getSession().getOutput().print(new CommandLine(cmd).getUsageMessage());
      }
    }
  }

  @Parameters(arity = "0..*")
  public final void setArgNames(@NonNull List<String> argNames) {
    this.argNames = argNames;
  }

  final void setCommandCenter(@NonNull CommandCenter commandCenter) {
    this.commandCenter = commandCenter;
  }
}
