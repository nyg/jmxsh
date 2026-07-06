package sh.jmx.jmxsh;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.attach.JavaProcess;
import sh.jmx.jmxsh.attach.JavaProcessManager;

public sealed interface JmxUrl
    permits JmxUrl.Pid, JmxUrl.JmxmpAddress, JmxUrl.HostPort, JmxUrl.ServiceUrl {

  static JmxUrl parse(String url) {
    if (url == null || url.isEmpty()) {
      throw new IllegalArgumentException("Empty URL is not allowed");
    }
    if (isDigits(url)) {
      return new Pid(url);
    }
    if (JmxmpAddress.PATTERN.matcher(url).find()) {
      return new JmxmpAddress(url);
    }
    if (HostPort.PATTERN.matcher(url).find()) {
      return new HostPort(url);
    }
    return new ServiceUrl(url);
  }

  private static JMXServiceURL toRawServiceUrl(String url) {
    try {
      return new JMXServiceURL(url);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(
          """
          Invalid connection target "%s". Accepted forms: a <PID>, <host>:<port>, \
          jmxmp://<host>:<port>, a full service:jmx:... URL, or an alias defined with \
          the alias command.\
          """.formatted(url),
          e);
    }
  }

  private static boolean isDigits(String s) {
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch < '0' || ch > '9') {
        return false;
      }
    }
    return true;
  }

  JMXServiceURL toServiceUrl(JavaProcessManager processManager) throws IOException;

  record Pid(String digits) implements JmxUrl {
    @Override
    public JMXServiceURL toServiceUrl(JavaProcessManager processManager) throws IOException {
      if (processManager == null) {
        return toRawServiceUrl(digits);
      }
      int pid = Integer.parseInt(digits);
      JavaProcess process = processManager.get(pid);
      if (process == null) {
        throw new NullPointerException("No such PID %d".formatted(pid));
      }
      if (!process.isManageable()) {
        process.startManagementAgent();
        if (!process.isManageable()) {
          throw new IllegalStateException(
              "Managed agent for PID %d couldn't start. PID %d is not manageable".formatted(pid, pid));
        }
      }
      return new JMXServiceURL(process.toUrl());
    }
  }

  record JmxmpAddress(String url) implements JmxUrl {
    private static final Pattern PATTERN = Pattern.compile("^jmxmp://(\\S+)$");

    @Override
    public JMXServiceURL toServiceUrl(JavaProcessManager processManager) throws IOException {
      return new JMXServiceURL("service:jmx:%s".formatted(url));
    }
  }

  record HostPort(String hostPort) implements JmxUrl {
    private static final Pattern PATTERN = Pattern.compile("^(\\w|\\.|\\-)+\\:\\d+$");

    @Override
    public JMXServiceURL toServiceUrl(JavaProcessManager processManager) throws IOException {
      return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://%s/jmxrmi".formatted(hostPort));
    }
  }

  record ServiceUrl(String url) implements JmxUrl {
    @Override
    public JMXServiceURL toServiceUrl(JavaProcessManager processManager) throws IOException {
      return toRawServiceUrl(url);
    }
  }
}
