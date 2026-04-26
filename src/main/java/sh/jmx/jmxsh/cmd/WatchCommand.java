package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.CommandOutput;
import sh.jmx.jmxsh.io.JlineCommandInput;
import org.jline.reader.impl.LineReaderImpl;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import lombok.extern.slf4j.Slf4j;

/**
 * Command to watch an MBean attribute.
 */
@CommandLine.Command(
    name = "watch",
    description = "Watch the value of one MBean attribute constantly",
    footer = "DO NOT call this command in a script and expect decent output")
@Slf4j
public class WatchCommand extends Command {
  @FunctionalInterface
  private interface LineOutput {
    void printLine(String line) throws IOException;
  }

  private static final String BUILDING_ATTRIBUTE_NOW = "%now";

  private static final int DEFAULT_REFRESH_INTERVAL = 1;

  private List<String> attributes = new ArrayList<>();

  private String outputFormat;

  private int refreshInterval = DEFAULT_REFRESH_INTERVAL;

  private boolean report;

  private int stopAfter;

  @Override
  public List<String> doSuggestArgument() throws IOException, JMException {
    if (getSession().getBean() != null) {
      MBeanServerConnection con = getSession().getConnection().getServerConnection();
      MBeanAttributeInfo[] ais =
          con.getMBeanInfo(new ObjectName(getSession().getBean())).getAttributes();
      return Stream.concat(
              Arrays.stream(ais).map(MBeanAttributeInfo::getName),
              Stream.of(BUILDING_ATTRIBUTE_NOW))
          .toList();
    }
    return List.of();
  }

  @Override
  public void execute() throws IOException, JMException {
    if (report && stopAfter == 0) {
      throw new IllegalArgumentException(
          "When --report is sepcified, --stopafter(-s) must be specificed");
    }
    Session session = getSession();
    String domain = DomainCommand.getDomainName(null, session);
    if (domain == null) {
      throw new IllegalStateException("Please specify a domain using domain command first.");
    }
    String beanName = BeanCommand.getBeanName(null, domain, session);
    if (beanName == null) {
      throw new IllegalStateException("Please specify a bean using bean command first.");
    }

    log.debug("starting watch on {} for attributes {}", beanName, attributes);

    final ObjectName name = new ObjectName(beanName);
    final MBeanServerConnection con = session.getConnection().getServerConnection();
    final LineOutput output;
    if (report) {
      CommandOutput out = session.getOutput();
      output = out::println;
    } else {
      if (!(session.getInput() instanceof JlineCommandInput jlineInput)) {
        throw new IllegalStateException("Under current context, watch command can't execute.");
      }
      LineReaderImpl console = jlineInput.getConsole();
      output = line -> {
        console.redrawLine();
        console.getTerminal().writer().print(line);
        console.flush();
      };
      getSession().getOutput().printMessage("press any key to stop. DO NOT press Ctrl+C !!!");
    }

    final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleWithFixedDelay(
        () -> {
          try {
            printValues(name, con, output);
          } catch (IOException e) {
            getSession().getOutput().printError(e);
          }
        },
        0,
        refreshInterval,
        TimeUnit.SECONDS);
    if (stopAfter > 0) {
      executor.schedule(
          (Runnable) executor::shutdownNow,
          stopAfter,
          TimeUnit.SECONDS);
    }
    if (!report) {
      System.in.read();
      System.out.println();
      executor.shutdownNow();
      log.debug("watch stopped");
    }

    session.getOutput().println("");
  }

  private Object getAttributeValue(
      ObjectName beanName, String attributeName, MBeanServerConnection connection)
      throws IOException {
    // %now is a reserved keyword for current instant
    if (BUILDING_ATTRIBUTE_NOW.equals(attributeName)) {
      return Instant.now();
    }
    try {
      return connection.getAttribute(beanName, attributeName);
    } catch (JMException e) {
      return e.getClass().getSimpleName();
    }
  }

  private void printValues(ObjectName beanName, MBeanServerConnection connection, LineOutput output)
      throws IOException {
    String result;
    if (outputFormat == null) {
      var sb = new StringBuilder();
      boolean first = true;
      for (String attributeName : attributes) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(getAttributeValue(beanName, attributeName, connection));
      }
      result = sb.toString();
    } else {
      Object[] values = new Object[attributes.size()];
      int i = 0;
      for (String attributeNamne : attributes) {
        values[i++] = getAttributeValue(beanName, attributeNamne, connection);
      }
      result = outputFormat.formatted(values);
    }
    output.printLine(result);
  }

  /** @param attributes Name of attributes to watch */
  @Parameters(paramLabel = "attr", description = "Name of attributes to watch", arity = "1..*")
  public final void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }

  /** @param outputFormat printf-style format string to print attribute values */
  @Option(
      names = {"-f", "--format"},
      paramLabel = "expr",
      description = "printf-style format string (e.g. '%s %s') to print attribute values")
  public final void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  /** @param refreshInterval Refreshing interval in seconds */
  @Option(
      names = {"-i", "--interval"},
      paramLabel = "sec",
      description = "Optional number of seconds between consecutive poll, default is 1 second",
      defaultValue = "1")
  public final void setRefreshInterval(int refreshInterval) {
    if (refreshInterval <= 0) {
      throw new IllegalArgumentException("Invalid interval value " + refreshInterval);
    }
    this.refreshInterval = refreshInterval;
  }

  /** @param report True to output result line by line as report */
  @Option(names = {"-r", "--report"}, description = "Output result line by line as report")
  public final void setReport(boolean report) {
    this.report = report;
  }

  /** @param stopAfter After this number of seconds, stop watching */
  @Option(
      names = {"-s", "--stopafter"},
      paramLabel = "sec",
      description = "Stop after watching a number of seconds")
  public final void setStopAfter(int stopAfter) {
    if (stopAfter < 0) {
      throw new IllegalArgumentException("Invalid stop after argument " + stopAfter);
    }
    this.stopAfter = stopAfter;
  }
}
