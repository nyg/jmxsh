package sh.jmx.jmxsh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * End-to-end integration tests that launch jmxterm as a separate OS process, connect to a target
 * JVM, and verify command output.
 */
class ScriptExecutionE2EIT {

  private static final Duration TIMEOUT = Duration.ofSeconds(30);
  private static TargetJvmProcess targetJvm;

  @BeforeAll
  static void startTargetJvm() throws Exception {
    targetJvm = new TargetJvmProcess();
    targetJvm.waitUntilReady(TIMEOUT);
  }

  @AfterAll
  static void stopTargetJvm() {
    if (targetJvm != null) {
      targetJvm.close();
    }
  }

  @Test
  void testBasicCommandExecution() throws Exception {
    try (JmxshProcessHelper jmxterm = new JmxshProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open localhost:" + targetJvm.getJmxPort(), "domains", "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      // The platform MBeanServer always exposes the JMImplementation domain
      assertThat(output)
          .as("Expected 'JMImplementation' domain in output: " + output)
          .contains("JMImplementation");
    }
  }

  @Test
  void testGetAttribute() throws Exception {
    try (JmxshProcessHelper jmxterm = new JmxshProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open localhost:" + targetJvm.getJmxPort(),
          "bean test:type=TestMBean",
          "run reset",
          "get Name",
          "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      assertThat(output).as("Expected 'default' in output: " + output).contains("default");
    }
  }

  @Test
  void testSetAndGetAttribute() throws Exception {
    try (JmxshProcessHelper jmxterm = new JmxshProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open localhost:" + targetJvm.getJmxPort(),
          "bean test:type=TestMBean",
          "set Name hello",
          "get Name",
          "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      assertThat(output).as("Expected 'hello' in output: " + output).contains("hello");
    }
  }

  @Test
  void testRunOperation() throws Exception {
    try (JmxshProcessHelper jmxterm = new JmxshProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open localhost:" + targetJvm.getJmxPort(),
          "bean test:type=TestMBean",
          "run echo world",
          "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      assertThat(output).as("Expected 'echo:world' in output: " + output).contains("echo:world");
    }
  }
}
