package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.SyntaxUtils;
import sh.jmx.jmxsh.io.RuntimeIOException;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@CommandLine.Command(
    name = "bean",
    description = "Display or set current selected MBean. ",
    footer = "Without any parameter, it displays current selected bean, "
            + "otherwise it selects the bean defined by the first parameter. eg. bean java.lang:type=Memory")
@Slf4j
public class BeanCommand extends Command {
  public static String getBeanName(String bean, String domain, @NonNull Session session)
      throws JMException, IOException {
    if (bean == null) {
      return session.getBean();
    }
    if (SyntaxUtils.isNull(bean)) {
      return null;
    }
    MBeanServerConnection con = session.getConnection().getServerConnection();
    if (bean.contains(":")) {
      try {
        ObjectName name = new ObjectName(bean);
        if (con.isRegistered(name)) {
          return bean;
        }
      } catch (MalformedObjectNameException _) {
        // Invalid bean name — fall through to domain-qualified lookup
      }
    }

    String domainName = DomainCommand.getDomainName(domain, session);
    if (domainName == null) {
      throw new IllegalArgumentException(
          "Please specify domain using either -d option or domain command");
    }
    try {
      ObjectName name = new ObjectName(domainName + ":" + bean);
      if (con.isRegistered(name)) {
        return domainName + ":" + bean;
      }
    } catch (MalformedObjectNameException _) {
      // Invalid bean name — fall through to throw IllegalArgumentException
    }
    throw new IllegalArgumentException("Bean name " + bean + " isn't valid");
  }

  static List<String> getCandidateBeanNames(Session session) throws MalformedObjectNameException {
    try {
      ArrayList<String> results = new ArrayList<>(BeansCommand.getBeans(session, null));
      String domain = session.getDomain();
      if (domain != null) {
        List<String> beans = BeansCommand.getBeans(session, domain);
        for (String bean : beans) {
          results.add(bean.substring(domain.length() + 1));
        }
      }
      return results;
    } catch (IOException e) {
      throw new RuntimeIOException("Couldn't find candidate bean names", e);
    }
  }

  private String bean;

  private String domain;

  @Override
  public List<String> doSuggestArgument() throws IOException, MalformedObjectNameException {
    return getCandidateBeanNames(getSession());
  }

  @Override
  public List<String> doSuggestOption(String optionName) throws IOException {
    if ("d".equals(optionName)) {
      return DomainsCommand.getCandidateDomains(getSession());
    }
    return List.of();
  }

  @Override
  public void execute() throws IOException, JMException {
    Session session = getSession();
    if (bean == null) {
      if (session.getBean() == null) {
        session.getOutput().println(SyntaxUtils.NULL);
      } else {
        session.getOutput().println(session.getBean());
      }
      return;
    }
    String beanName = getBeanName(bean, domain, session);
    if (beanName == null) {
      session.setBean(null);
      session.getOutput().printMessage("bean is unset");
      return;
    }
    session.setBean(beanName);
    log.debug("selected bean: {}", beanName);
    session.getOutput().printMessage("bean is set to " + beanName);
    int colonIdx = beanName.indexOf(':');
    if (colonIdx > 0) {
      session.setDomain(beanName.substring(0, colonIdx));
    }
  }

  @Parameters(paramLabel = "bean", description = "MBean name with or without domain", arity = "0..1")
  public final void setBean(String bean) {
    this.bean = bean;
  }

  @Option(names = {"-d", "--domain"}, description = "Domain name")
  public final void setDomain(String domain) {
    this.domain = domain;
  }
}
