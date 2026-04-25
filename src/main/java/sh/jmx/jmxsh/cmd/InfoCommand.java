package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;

import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * Command that displays attributes and operations of an MBean
 *
 */
@CommandLine.Command(
    name = "info",
    description = "Display detail information about an MBean",
    footer = "If -b option is not specified, current selected MBean is applied")
public class InfoCommand extends Command {
  private static final Comparator<MBeanFeatureInfo> INFO_COMPARATOR =
      Comparator.comparing(MBeanFeatureInfo::getName).thenComparingInt(MBeanFeatureInfo::hashCode);

  private static final String TEXT_ATTRIBUTES = "# attributes";

  private static final String TEXT_NOTIFICATIONS = "# notifications";

  private static final String TEXT_OPERATIONS = "# operations";

  private String bean;

  private String domain;

  private boolean showDescription;

  private String type = "aon";

  private String operation;

  private void displayAttributes(MBeanInfo info) {
    Session session = getSession();
    MBeanAttributeInfo[] attrInfos = info.getAttributes();
    if (attrInfos.length == 0) {
      session.getOutput().printMessage("there is no attribute");
      return;
    }
    int index = 0;
    session.getOutput().println(TEXT_ATTRIBUTES);
    List<MBeanAttributeInfo> infos = Stream.of(attrInfos).sorted(INFO_COMPARATOR).toList();
    for (MBeanAttributeInfo attr : infos) {
      String rw = (attr.isReadable() ? "r" : "") + (attr.isWritable() ? "w" : "");
      session.getOutput().println(
          ("  %%%-3d - %s (%s, %s)" + (showDescription ? ", %s" : "")).formatted(
              index++,
              attr.getName(),
              attr.getType(),
              rw,
              attr.getDescription()));
    }
  }

  private void displayNotifications(MBeanInfo info) {
    Session session = getSession();
    MBeanNotificationInfo[] notificationInfos = info.getNotifications();
    if (notificationInfos.length == 0) {
      session.getOutput().printMessage("there's no notifications");
      return;
    }
    int index = 0;
    session.getOutput().println(TEXT_NOTIFICATIONS);
    for (MBeanNotificationInfo notification : notificationInfos) {
      session.getOutput().println(
          ("  %%%-3d - %s(%s)" + (showDescription ? ", %s" : "")).formatted(
              index++,
              notification.getName(),
              String.join(",", notification.getNotifTypes()),
              notification.getDescription()));
    }
  }

  private void displayOperations(MBeanInfo info) {
    Session session = getSession();
    MBeanOperationInfo[] operationInfos = info.getOperations();
    if (operationInfos.length == 0) {
      session.getOutput().printMessage("there's no operations");
      return;
    }
    List<MBeanOperationInfo> operations = Stream.of(operationInfos).sorted(INFO_COMPARATOR).toList();
    session.getOutput().println(TEXT_OPERATIONS);
    int index = 0;
    for (MBeanOperationInfo op : operations) {
      MBeanParameterInfo[] paramInfos = op.getSignature();
      List<String> paramTypes = new ArrayList<>(paramInfos.length);
      List<String> paramDescriptions = new ArrayList<>(paramInfos.length);
      for (MBeanParameterInfo paramInfo : paramInfos) {
        paramTypes.add(paramInfo.getType() + " " + paramInfo.getName());
        paramDescriptions.add("       " + paramInfo.getName() + ": " + paramInfo.getDescription());
      }
      String parameters = String.join(",", paramTypes);
      String parametersDesc =
          paramDescriptions.isEmpty() ? "" : '\n' + String.join("\n", paramDescriptions);
      session.getOutput().println(
          ("  %%%-3d - %s %s(%s)" + (showDescription ? ", %s%s" : "")).formatted(
              index++,
              op.getReturnType(),
              op.getName(),
              parameters,
              op.getDescription(),
              parametersDesc));
    }
  }

  private void displaySingleOperation(MBeanInfo info) {
    Session session = getSession();
    MBeanOperationInfo[] operationInfos = info.getOperations();
    if (operationInfos.length == 0) {
      session.getOutput().printMessage("there's no operations");
      return;
    }
    session.getOutput().println(TEXT_OPERATIONS);
    int index = 0;
    boolean found = false;
    for (MBeanOperationInfo op : operationInfos) {
      String opName = op.getName();
      if (opName.equals(operation)) {
        found = true;
        MBeanParameterInfo[] paramInfos = op.getSignature();
        List<String> paramTypes = new ArrayList<>(paramInfos.length);
        StringBuilder paramsDesc =
            new StringBuilder("             parameters:" + System.lineSeparator());
        for (MBeanParameterInfo paramInfo : paramInfos) {
          String parameter = paramInfo.getName();
          paramsDesc.append(
              ("                 + %-20s : %s" + System.lineSeparator()).formatted(
                  parameter,
                  paramInfo.getDescription()));
          paramTypes.add(paramInfo.getType() + " " + parameter);
        }
        session.getOutput().println(
            "  %%%-3d - %s %s(%s), %s".formatted(
                index++,
                op.getReturnType(),
                opName,
                String.join(",", paramTypes),
                op.getDescription()));
        session.getOutput().println(paramsDesc.toString());
      }
    }
    if (!found) {
      session.getOutput().printMessage(
          "The operation '%s' is not found in the bean.".formatted(operation));
    }
  }

  @Override
  public void execute() throws IOException, JMException {
    Session session = getSession();
    String beanName = BeanCommand.getBeanName(bean, domain, session);
    if (beanName == null) {
      throw new IllegalArgumentException(
          "Please specify a bean using either -b option or bean command");
    }
    ObjectName name = new ObjectName(beanName);
    MBeanServerConnection con = session.getConnection().getServerConnection();
    MBeanInfo info = con.getMBeanInfo(name);
    session.getOutput().printMessage("mbean = " + beanName);
    session.getOutput().printMessage("class name = " + info.getClassName());
    if (this.showDescription) {
      session.getOutput().printMessage("description: " + info.getDescription());
    }
    if (operation == null) {
      for (char t : type.toCharArray()) {
        switch (t) {
          case 'a' -> displayAttributes(info);
          case 'o' -> displayOperations(info);
          case 'n' -> displayNotifications(info);
          default -> throw new IllegalArgumentException(
              "Unrecognizable character " + t + " in type option " + type);
        }
      }
    } else {
      session.getOutput().printMessage("operation = " + operation);
      displaySingleOperation(info);
    }
  }

  /** @param bean Bean for which information is displayed */
  @Option(names = {"-b", "--bean"}, description = "Name of MBean")
  public final void setBean(String bean) {
    this.bean = bean;
  }

  /**
   * Given domain
   *
   * @param domain Domain name
   */
  @Option(names = {"-d", "--domain"}, description = "Domain for bean")
  public final void setDomain(String domain) {
    this.domain = domain;
  }

  /** @param showDescription True to show detail description */
  @Option(names = {"-e", "--detail"}, description = "Show description")
  public final void setShowDescription(boolean showDescription) {
    this.showDescription = showDescription;
  }

  /** @param type Type of detail to display */
  @Option(
      names = {"-t", "--type"},
      description =
          "Types(a|o|u) to display, for example aon for all attributes, operations and notifications")
  public void setType(String type) {
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Type can't be NULL");
    }
    if (!Pattern.matches("^a?o?n?$", type)) {
      throw new IllegalArgumentException("Type must be a?|o?|n?");
    }
    this.type = type;
  }

  @Option(
      names = {"-o", "--op"},
      description = "Show a single operation with more details (including parameters information)")
  public void setOperation(String operation) {
    if (operation == null || operation.isEmpty()) {
      throw new IllegalArgumentException("Operation can't be NULL");
    }
    this.operation = operation;
  }
}
