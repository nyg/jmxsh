package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.openmbean.CompositeDataSupport;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.ValueOutputFormat;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import lombok.extern.slf4j.Slf4j;

@CommandLine.Command(
    name = "get",
    description = "Get value of MBean attribute(s)",
    footer = "* stands for all attributes. eg. get Attribute1 Attribute2 or get *")
@Slf4j
public class GetCommand extends Command {
  private List<String> attributes = new ArrayList<>();

  private String bean;

  private String domain;

  private boolean singleLine;

  private String delimiter = "";

  private boolean showDescription;

  private boolean showQuotationMarks;

  private boolean simpleFormat;

  private boolean completeLine;

  private void displayAttributes() throws IOException, JMException {
    Session session = getSession();
    String beanName = BeanCommand.getBeanName(bean, domain, session);
    ObjectName name = new ObjectName(beanName);
    session.getOutput().printMessage("mbean = " + beanName + ":");
    MBeanServerConnection con = session.getConnection().getServerConnection();
    MBeanAttributeInfo[] ais = con.getMBeanInfo(name).getAttributes();
    Map<String, MBeanAttributeInfo> attributeNames =
        new LinkedHashMap<>();
    if (attributes.contains("*")) {
      for (MBeanAttributeInfo ai : ais) {
        attributeNames.put(ai.getName(), ai);
      }
    } else {
      for (String arg : attributes) {
        String[] attributeNameElements = arg.split("\\.");

        String firstPath = attributeNameElements[0];

        for (MBeanAttributeInfo ai : ais) {
          if (ai.getName().equals(firstPath)) {
            attributeNames.put(arg, ai);
            break;
          }
        }
      }
    }
    ValueOutputFormat format = new ValueOutputFormat(2, showDescription, showQuotationMarks);
    for (Map.Entry<String, MBeanAttributeInfo> entry : attributeNames.entrySet()) {
      String attributeName = entry.getKey();
      MBeanAttributeInfo i = entry.getValue();
      if (i.isReadable()) {
        String[] attributeNameElements = attributeName.split("\\.");

        String attributeNameToRequest = attributeName;
        if (attributeNameElements.length > 1) {
          attributeNameToRequest = attributeNameElements[0];
        }

        Object result = null;

        try {
          result = con.getAttribute(name, attributeNameToRequest);
        } catch (RuntimeMBeanException e) {
          session.getOutput().printMessage(
              "Could not get attribute " + attributeNameToRequest + ": " + e.getMessage());
        }

        if (result instanceof CompositeDataSupport support && attributeNameElements.length > 1) {
            result = support.get(attributeNameElements[1]);
        }

        if (simpleFormat) {
          format.printValue(session.getOutput(), result);
        } else if (completeLine) {
          format.printValue(
              session.getOutput(),
              "mbean = %s # %s = %s".formatted(beanName, attributeName, result));
        } else {
          format.printExpression(session.getOutput(), attributeName, result, i.getDescription());
        }
        session.getOutput().print(delimiter);
        if (!singleLine) {
          session.getOutput().println("");
        }
      } else {
        session.getOutput().printMessage(i.getName() + " is not readable");
      }
    }
  }

  @Override
  public List<String> doSuggestArgument() throws IOException, JMException {
    if (getSession().getBean() != null) {
      MBeanServerConnection con = getSession().getConnection().getServerConnection();
      MBeanAttributeInfo[] ais =
          con.getMBeanInfo(new ObjectName(getSession().getBean())).getAttributes();
      return Arrays.stream(ais).map(MBeanAttributeInfo::getName).toList();
    }
    return null;
  }

  @Override
  protected List<String> doSuggestOption(String optionName) throws JMException {
    if ("d".equals(optionName)) {
      return DomainsCommand.getCandidateDomains(getSession());
    } else if ("b".equals(optionName)) {
      return BeanCommand.getCandidateBeanNames(getSession());
    } else {
      return null;
    }
  }

  @Override
  public void execute() throws JMException, IOException {
    if (attributes.isEmpty()) {
      throw new IllegalArgumentException("Please specify at least one attribute");
    }
    log.debug("getting attribute(s) {} from bean {}", attributes, bean);
    displayAttributes();
  }

  @Parameters(paramLabel = "attr", description = "Name of attributes to select", arity = "1..*")
  public final void setAttributes(List<String> attributes) {
    Objects.requireNonNull(attributes, "Attributes can't be NULL");
    this.attributes = attributes;
  }

  @Option(
      names = {"-b", "--bean"},
      description = "MBean name where the attribute is. Optional if bean has been set")
  public final void setBean(String bean) {
    this.bean = bean;
  }

  @Option(names = {"-d", "--domain"}, description = "Domain of bean, optional")
  public final void setDomain(String domain) {
    this.domain = domain;
  }

  @Option(names = {"-i", "--info"}, description = "Show detail information of each attribute")
  public final void setShowDescription(boolean showDescription) {
    this.showDescription = showDescription;
  }

  @Option(names = {"-q", "--quots"}, description = "Quotation marks around value")
  public final void setShowQuotationMarks(boolean noQuotationMarks) {
    this.showQuotationMarks = noQuotationMarks;
  }

  @Option(
      names = {"-s", "--simple"},
      description = "Print simple expression of value without full expression")
  public final void setSimpleFormat(boolean simpleFormat) {
    this.simpleFormat = simpleFormat;
  }

  @Option(
      names = {"-f", "--completeLine"},
      description = "Print expression with bean and value in single line with '#' delimiter.")
  public final void setCompleteLine(boolean completeLine) {
    this.completeLine = completeLine;
  }

  @Option(
      names = {"-l", "--delimiter"},
      description = "Sets an optional delimiter to be printed after the value")
  public final void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Option(
      names = {"-n", "--singleLine"},
      description = "Prints result without a newline - default is false")
  public final void setSingleLine(boolean singleLine) {
    this.singleLine = singleLine;
  }
}
