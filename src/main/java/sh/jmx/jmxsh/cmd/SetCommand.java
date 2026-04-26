package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.SyntaxUtils;
import sh.jmx.jmxsh.utils.ValueFormat;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import lombok.extern.slf4j.Slf4j;

@CommandLine.Command(name = "set", description = "Set value of an MBean attribute")
@Slf4j
public class SetCommand extends DomainBeanAwareCommand {
  private List<String> arguments = Collections.emptyList();

  private String bean;

  private String domain;

  @Override
  protected List<String> doSuggestArgument() throws IOException, JMException {
    Session session = getSession();
    if (session.getBean() != null) {
      MBeanServerConnection conn = getSession().getConnection().getServerConnection();
      MBeanInfo info = conn.getMBeanInfo(new ObjectName(session.getBean()));
      MBeanAttributeInfo[] attrs = info.getAttributes();
      return Arrays.stream(attrs).map(MBeanAttributeInfo::getName).toList();
    }
    return List.of();
  }

  @Override
  public void execute() throws JMException, IOException {
    if (arguments.size() < 2) {
      throw new IllegalArgumentException("At least two arguments are required");
    }
    Session session = getSession();
    String attributeName = arguments.get(0);
    log.debug("setting attribute {} on bean {}", attributeName, bean);

    String beanName = BeanCommand.getBeanName(bean, domain, session);
    ObjectName name = new ObjectName(beanName);

    MBeanServerConnection con = session.getConnection().getServerConnection();
    MBeanInfo beanInfo = con.getMBeanInfo(new ObjectName(beanName));
    MBeanAttributeInfo attributeInfo = null;
    for (MBeanAttributeInfo i : beanInfo.getAttributes()) {
      if (i.getName().equals(attributeName)) {
        attributeInfo = i;
        break;
      }
    }
    if (attributeInfo == null) {
      throw new IllegalArgumentException("Attribute " + attributeName + " is not specified");
    }
    if (!attributeInfo.isWritable()) {
      throw new IllegalArgumentException("Attribute " + attributeName + " is not writable");
    }
    String inputValue = arguments.get(1);
    if (inputValue != null) {
      inputValue = ValueFormat.parseValue(inputValue);
    }
    Object value = SyntaxUtils.parse(inputValue, attributeInfo.getType());
    con.setAttribute(name, new Attribute(attributeName, value));
    session.getOutput().printMessage("Value of attribute " + attributeName + " is set to " + inputValue);
  }

  @Parameters(description = "name, value, value2...", arity = "2..*")
  public final void setArguments(List<String> arguments) {
    Objects.requireNonNull(arguments, "Arguments can't be NULL");
    this.arguments = arguments;
  }

  @Option(
      names = {"-b", "--bean"},
      description = "MBean name where the attribute is. Optional if bean has been set")
  public final void setBean(String bean) {
    this.bean = bean;
  }

  @Option(names = {"-d", "--domain"}, description = "Domain under which the bean is")
  public final void setDomain(String domain) {
    this.domain = domain;
  }
}
