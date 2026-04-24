package org.cyclopsgroup.jmxterm.cmd;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.cyclopsgroup.jmxterm.Command;
import org.cyclopsgroup.jmxterm.Session;
import org.cyclopsgroup.jmxterm.io.RuntimeIOException;

import picocli.CommandLine;

/**
 * List domains for JMX connection
 *
 */
@CommandLine.Command(name = "domains", description = "List all available domain names")
public class DomainsCommand extends Command {
  /**
   * Gets list of domains for current JMX connection.
   *
   * @param session The current session.
   * @return List of available domain names.
   */
  static List<String> getCandidateDomains(Session session) {
    String[] domains;
    try {
      domains = session.getConnection().getServerConnection().getDomains();
    } catch (IOException e) {
      throw new RuntimeIOException("Couldn't get candate domains", e);
    }
    return Stream.of(domains).sorted().toList();
  }

  @Override
  public void execute() throws IOException {
    Session session = getSession();

    session.getOutput().printMessage("following domains are available");
    for (String domain : getCandidateDomains(session)) {
      session.getOutput().println(domain);
    }
  }
}
