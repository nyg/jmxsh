package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.ValueOutputFormat;
import sh.jmx.jmxsh.utils.MBeanValueParser;
import sh.jmx.jmxsh.utils.OperationArguments;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@CommandLine.Command(
    name = "run",
    description = "Invoke an MBean operation",
    footer = "Syntax is \n run <operationName> [parameter1] [parameter2]\n"
        + " run -j <json> <operationName>")
@Slf4j
public class RunCommand extends Command {
  private final MBeanValueParser valueParser = new MBeanValueParser();

  private String bean;

  private String domain;

  private String json;

  private boolean measure;

  private List<String> parameters = Collections.emptyList();

  private boolean showQuotationMarks;

  private String types;

  @Override
  public List<String> doSuggestArgument() throws IOException, JMException {
    Session session = getSession();
    if (getSession().getBean() != null) {
      MBeanInfo info =
          session
              .getConnection()
              .getServerConnection()
              .getMBeanInfo(new ObjectName(session.getBean()));
      MBeanOperationInfo[] operationInfos = info.getOperations();
      return Arrays.stream(operationInfos).map(MBeanOperationInfo::getName).toList();
    }
    return List.of();
  }

  @Override
  public void execute() throws IOException, JMException {
    Session session = getSession();
    String beanName = BeanCommand.getBeanName(bean, domain, session);
    if (beanName == null) {
      throw new IllegalArgumentException(
          "Please specify MBean to invoke either using -b option or bean command");
    }
    if (parameters.isEmpty()) {
      throw new IllegalArgumentException("At least one parameter is needed");
    }

    String operationName = parameters.getFirst();
    OperationArguments arguments = createArguments();
    String[] paramTypes = parseParamTypes(arguments.size());
    log.debug("invoking operation {} on {}", operationName, beanName);
    ObjectName name = new ObjectName(beanName);
    MBeanServerConnection con = session.getConnection().getServerConnection();
    MBeanInfo beanInfo = con.getMBeanInfo(name);

    MBeanOperationInfo operationInfo = findOperation(beanInfo, operationName, paramTypes, arguments);
    if (operationInfo == null) {
      throw noMatchingOperation(beanInfo, operationName, beanName, arguments);
    }

    MBeanParameterInfo[] paramInfos = operationInfo.getSignature();
    Object[] params = arguments.bind(paramInfos);
    String[] signatures =
        Arrays.stream(paramInfos).map(MBeanParameterInfo::getType).toArray(String[]::new);
    session.getOutput().printMessage(
        "Calling operation %s of mbean %s with params %s.".formatted(
            operationName, beanName, Arrays.deepToString(params)));

    Object result = invoke(session, con, name, operationName, params, signatures);
    session.getOutput().printMessage("Operation returns: ");
    new ValueOutputFormat(2, false, showQuotationMarks).printValue(session.getOutput(), result);
    session.getOutput().println("");
  }

  private OperationArguments createArguments() {
    if (json == null) {
      return OperationArguments.ofPositional(
          parameters.subList(1, parameters.size()), valueParser);
    }
    if (parameters.size() > 1) {
      throw new IllegalArgumentException(
          "Positional parameters cannot be combined with -j, pass only the operation name");
    }
    return OperationArguments.ofJson(json, valueParser);
  }

  private String[] parseParamTypes(int argumentCount) {
    if (types == null) {
      return new String[0];
    }
    String[] paramTypes = types.split(",");
    if (paramTypes.length != argumentCount) {
      throw new IllegalArgumentException("Signature does not match parameter count");
    }
    return paramTypes;
  }

  private MBeanOperationInfo findOperation(MBeanInfo beanInfo, String operationName,
      String[] paramTypes, OperationArguments arguments) {
    for (MBeanOperationInfo info : beanInfo.getOperations()) {
      if (operationName.equals(info.getName())
          && arguments.fits(info.getSignature())
          && parameterTypesMatch(info.getSignature(), paramTypes)) {
        return info;
      }
    }
    return null;
  }

  private boolean parameterTypesMatch(MBeanParameterInfo[] paramInfos, String[] paramTypes) {
    for (int i = 0; i < paramTypes.length && i < paramInfos.length; i++) {
      // "string" is treated specially and implies type "java.lang.String"
      boolean stringAlias = paramInfos[i].getType().equals(String.class.getName())
          && "string".equals(paramTypes[i]);
      if (!stringAlias && !paramTypes[i].equals(paramInfos[i].getType())) {
        return false;
      }
    }
    return true;
  }

  private static IllegalArgumentException noMatchingOperation(MBeanInfo beanInfo,
      String operationName, String beanName, OperationArguments arguments) {
    List<String> candidates = Arrays.stream(beanInfo.getOperations())
        .filter(info -> operationName.equals(info.getName()))
        .map(RunCommand::describeSignature)
        .toList();
    if (candidates.isEmpty()) {
      return new IllegalArgumentException(
          "Operation %s doesn't exist in bean %s".formatted(operationName, beanName));
    }
    return new IllegalArgumentException(
        "Operation %s with %d parameters doesn't exist in bean %s, known signatures are %s"
            .formatted(operationName, arguments.size(), beanName, String.join(", ", candidates)));
  }

  private static String describeSignature(MBeanOperationInfo info) {
    return "%s(%s)".formatted(
        info.getName(),
        Arrays.stream(info.getSignature())
            .map(parameter -> parameter.getType() + " " + parameter.getName())
            .collect(Collectors.joining(", ")));
  }

  private Object invoke(Session session, MBeanServerConnection con, ObjectName name,
      String operationName, Object[] params, String[] signatures) throws IOException, JMException {
    if (!measure) {
      return con.invoke(name, operationName, params, signatures);
    }
    long start = System.nanoTime();
    try {
      return con.invoke(name, operationName, params, signatures);
    } finally {
      long latency = (System.nanoTime() - start) / 1_000_000;
      session.getOutput().printMessage("Invocation took %sms.".formatted(latency));
    }
  }

  @Option(names = {"-b", "--bean"}, description = "MBean to invoke")
  public final void setBean(String bean) {
    this.bean = bean;
  }

  @Option(names = {"-d", "--domain"}, description = "Domain of MBean to invoke")
  public final void setDomain(String domain) {
    this.domain = domain;
  }

  @Option(
      names = {"-j", "--json"},
      description = "JSON array or object holding the operation parameters")
  public final void setJson(String json) {
    this.json = json;
  }

  @Option(
      names = {"-m", "--measure"},
      description = "Measure the time spent on the invocation of operation")
  public final void setMeasure(boolean measure) {
    this.measure = measure;
  }

  @Option(
      names = {"-t", "--types"},
      description = "Require parameters to have specific types (comma separated)")
  public final void setTypes(String types) {
    this.types = types;
  }

  @Parameters(
      description = "The first parameter is operation name, which is followed by list of arguments",
      arity = "1..*")
  public final void setParameters(@NonNull List<String> parameters) {
    this.parameters = parameters;
  }

  @Option(names = {"-q", "--quots"}, description = "Flag for quotation marks")
  public final void setShowQuotationMarks(boolean showQuotationMarks) {
    this.showQuotationMarks = showQuotationMarks;
  }
}
