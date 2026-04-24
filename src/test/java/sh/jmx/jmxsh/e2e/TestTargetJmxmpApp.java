package sh.jmx.jmxsh.e2e;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Minimal JVM application that exposes JMX over JMXMP for end-to-end testing. Unlike {@link
 * TestTargetApp} which relies on JVM flags for RMI, this app programmatically starts a JMXMP
 * connector server. Prints "READY &lt;port&gt;" to stdout. Blocks on stdin.read().
 *
 * <p>Usage: {@code java -cp <classpath> sh.jmx.jmxsh.e2e.TestTargetJmxmpApp <port>}
 */
public class TestTargetJmxmpApp {

  /** MBean interface exposed for testing. */
  public interface TestMBean {
    String getName();

    void setName(String name);

    int getCount();

    String echo(String input);

    void reset();
  }

  /** Concrete implementation of {@link TestMBean}. */
  public static class TestMBeanImpl implements TestMBean {
    private String name = "default";
    private int count = 0;

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
      this.name = name;
      this.count++;
    }

    @Override
    public int getCount() {
      return count;
    }

    @Override
    public String echo(String input) {
      return "echo:" + input;
    }

    @Override
    public void reset() {
      name = "default";
      count = 0;
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("Usage: TestTargetJmxmpApp <port>");
      System.exit(1);
    }
    int port = Integer.parseInt(args[0]);

    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    mbs.registerMBean(
        new StandardMBean(new TestMBeanImpl(), TestMBean.class),
        new ObjectName("test:type=TestMBean"));

    JMXServiceURL url = new JMXServiceURL("service:jmx:jmxmp://localhost:" + port);
    JMXConnectorServer connectorServer =
        JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
    connectorServer.start();

    System.out.println("READY " + port);
    System.out.flush();
    // Block until stdin is closed by the parent process
    System.in.read();
    connectorServer.stop();
  }
}
