package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;

import picocli.CommandLine;
import picocli.CommandLine.Option;

@CommandLine.Command(
    name = "beans",
    description = "List available beans under a domain or all domains",
    footer =
        "Without -d option, current select domain is applied. If there's no domain specified, all beans are listed. Example:\n beans\n beans -d java.lang")
public class BeansCommand extends Command {
  public static List<String> getBeans(Session session, String domainName)
      throws MalformedObjectNameException, IOException {
    ObjectName queryName = domainName == null ?  null : new ObjectName(domainName + ":*");
    return session.getConnection()
        .getServerConnection()
        .queryNames(queryName, null).stream()
        .map(ObjectName::getCanonicalName)
        .sorted()
        .toList();
  }

  private String domain;

  @Override
  public List<String> doSuggestOption(String optionName) throws IOException {
    if ("d".equals(optionName)) {
      return DomainsCommand.getCandidateDomains(getSession());
    }
    return List.of();
  }

  @Override
  public void execute() throws MalformedObjectNameException, IOException {
    Session session = getSession();
    String domainName = DomainCommand.getDomainName(domain, session);
    List<String> domains = new ArrayList<>();
    if (domainName == null) {
      domains.addAll(DomainsCommand.getCandidateDomains(session));
    } else {
      domains.add(domainName);
    }
    for (String d : domains) {
      session.getOutput().printMessage("domain = " + d + ":");
      for (String bean : getBeans(session, d)) {
        session.getOutput().println(bean);
      }
    }
  }

  @Option(
      names = {"-d", "--domain"},
      paramLabel = "domain",
      description = "Name of domain under which beans are listed")
  public final void setDomain(String domain) {
    this.domain = domain;
  }
}
