package org.cyclopsgroup.jmxterm.cc;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Command that display a help message
 *
 */
@Command(
    name = "help",
    description = "Display available commands or usage of a command",
    footer =
        "Run \"help [command1] [command2] ...\" to display usage or certain command(s). Help without argument shows list of available commands")
public class HelpCommand extends org.cyclopsgroup.jmxterm.Command {
  private List<String> argNames = Collections.emptyList();

  private CommandCenter commandCenter;

  @Override
  public void execute() {
    Objects.requireNonNull(commandCenter, "Command center hasn't been set yet");
    if (argNames.isEmpty()) {
      List<String> commandNames = commandCenter.getCommandNames().stream().sorted().toList();
      getSession().getOutput().printMessage("following commands are available to use:");
      for (String commandName : commandNames) {
        org.cyclopsgroup.jmxterm.Command cmd =
            commandCenter.createCommand(commandName);
        CommandLine cl = new CommandLine(cmd);
        String[] desc = cl.getCommandSpec().usageMessage().description();
        String description = desc.length > 0 ? String.join(" ", desc) : "";
        getSession().getOutput().println("%-8s - %s".formatted(commandName, description));
      }
    } else {
      for (String argName : argNames) {
        Class<? extends org.cyclopsgroup.jmxterm.Command> commandType =
            commandCenter.getCommandType(argName);
        if (commandType == null) {
          throw new IllegalArgumentException("Command " + argName + " is not found");
        }
        org.cyclopsgroup.jmxterm.Command cmd =
            commandCenter.createCommand(argName);
        new CommandLine(cmd).usage(new PrintWriter(System.out, true));
      }
    }
  }

  /** @param argNames Array of arguments */
  @Parameters(arity = "0..*")
  public final void setArgNames(List<String> argNames) {
    Objects.requireNonNull(argNames, "argNames can't be NULL");
    this.argNames = argNames;
  }

  /** @param commandCenter CommandCenter object that calls this help command */
  final void setCommandCenter(CommandCenter commandCenter) {
    Objects.requireNonNull(commandCenter, "commandCenter can't be NULL");
    this.commandCenter = commandCenter;
  }
}
