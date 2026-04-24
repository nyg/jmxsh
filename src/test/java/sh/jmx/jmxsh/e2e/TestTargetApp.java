package sh.jmx.jmxsh.e2e;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

/**
 * Minimal JVM application that exposes JMX for end-to-end testing. Launched as a subprocess with
 * JMX remote enabled. Prints "READY" to stdout when MBeans are registered. Blocks on stdin.read()
 * — closes when parent closes stdin or sends input.
 */
public class TestTargetApp {

  /** MBean interface exposed for testing. Embedded to avoid classpath issues in subprocess. */
  public interface TestMBean {
    String getName();

    void setName(String name);

    int getCount();

    String echo(String input);

    int add(int a, int b);

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
    public int add(int a, int b) {
      return a + b;
    }

    @Override
    public void reset() {
      name = "default";
      count = 0;
    }
  }

  public static void main(String[] args) throws Exception {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    mbs.registerMBean(
        new StandardMBean(new TestMBeanImpl(), TestMBean.class),
        new ObjectName("test:type=TestMBean"));
    System.out.println("READY");
    System.out.flush();
    // Block until stdin is closed by the parent process
    System.in.read();
  }
}
