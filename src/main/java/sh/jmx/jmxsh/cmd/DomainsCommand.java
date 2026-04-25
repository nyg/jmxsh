package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.RuntimeIOException;

import picocli.CommandLine;

@CommandLine.Command(name = "domains", description = "List all available domain names")
public class DomainsCommand extends Command {
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
