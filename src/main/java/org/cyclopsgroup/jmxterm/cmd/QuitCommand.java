package org.cyclopsgroup.jmxterm.cmd;

import java.io.IOException;
import org.cyclopsgroup.jmxterm.Command;
import org.cyclopsgroup.jmxterm.Session;

import picocli.CommandLine;

/**
 * Command to terminate the console
 *
 */
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
