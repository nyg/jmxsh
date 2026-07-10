package sh.jmx.jmxsh.cmd;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;

import picocli.CommandLine;

@CommandLine.Command(name = "quit", aliases = {"exit", "bye"}, description = "Terminate console and exit")
public class QuitCommand extends Command {
  @Override
  public void execute() {
    Session session = getSession();
    session.close();
    session.getOutput().printMessage("Bye.");
  }
}
