package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.SyntaxUtils;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@CommandLine.Command(
    name = "domain",
    description = "Display or set current selected domain. ",
    footer =
        "With a parameter, parameter defined domain is selected, otherwise it displays current selected domain."
            + " eg. domain java.lang")
@Slf4j
public class DomainCommand extends Command {
  static String getDomainName(String domain, @NonNull Session session) {
    if (session.getConnection() == null) {
      throw new IllegalArgumentException("Session isn't opened");
    }
    if (domain == null) {
      return session.getDomain();
    }
    if (SyntaxUtils.isNull(domain)) {
      return null;
    }
    HashSet<String> domains = new HashSet<>(DomainsCommand.getCandidateDomains(session));
    if (!domains.contains(domain)) {
      throw new IllegalArgumentException(
          "Domain " + domain + " doesn't exist, check your spelling");
    }
    return domain;
  }

  private String domain;

  @Override
  public List<String> doSuggestArgument() throws IOException {
    return DomainsCommand.getCandidateDomains(getSession());
  }

  @Override
  public void execute() throws IOException {
    Session session = getSession();
    if (domain == null) {
      if (session.getDomain() == null) {
        session.getOutput().printMessage("domain is not set");
        session.getOutput().println(SyntaxUtils.NULL);
      } else {
        session.getOutput().printMessage("domain = " + session.getDomain());
        session.getOutput().println(session.getDomain());
      }
      return;
    }
    String domainName = getDomainName(domain, session);
    if (domainName == null) {
      session.unsetDomain();
      session.getOutput().printMessage("domain is unset");
    } else {
      String currentBean = session.getBean();
      if (currentBean != null) {
        int colonIdx = currentBean.indexOf(':');
        String beanDomain = colonIdx > 0 ? currentBean.substring(0, colonIdx) : null;
        if (!domainName.equals(beanDomain)) {
          session.setBean(null);
          session.getOutput().printMessage("bean was unset (not part of domain " + domainName + ")");
        }
      }
      session.setDomain(domainName);
      log.debug("selected domain: {}", domainName);
      session.getOutput().printMessage("domain is set to " + session.getDomain());
    }
  }

  @Parameters(paramLabel = "domain", description = "Name of domain to set", arity = "0..1")
  public final void setDomain(String domain) {
    this.domain = domain;
  }
}
