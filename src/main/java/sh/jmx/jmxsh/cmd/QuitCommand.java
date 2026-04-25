package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;

import picocli.CommandLine;

@CommandLine.Command(name = "quit", aliases = {"exit", "bye"}, description = "Terminate console and exit")
public class QuitCommand extends Command {
  @Override
  public void execute() throws IOException {
    Session session = getSession();
    session.disconnect();
    session.close();
    session.getOutput().printMessage("bye");
  }
}
