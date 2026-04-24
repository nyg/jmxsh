package sh.jmx.jmxsh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests that launch jmxterm as a separate OS process, connect to a JMXMP target JVM, and
 * verify command output.
 */
class JmxmpConnectionE2EIT {

  private static final Duration TIMEOUT = Duration.ofSeconds(30);
  private static TargetJmxmpProcess targetJvm;

  @BeforeAll
  static void startTargetJvm() throws Exception {
    targetJvm = new TargetJmxmpProcess();
    targetJvm.waitUntilReady(TIMEOUT);
  }

  @AfterAll
  static void stopTargetJvm() {
    if (targetJvm != null) {
      targetJvm.close();
    }
  }

  @Test
  void testOpenWithJmxmpShorthand() throws Exception {
    try (JmxTermProcessHelper jmxterm = new JmxTermProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open jmxmp://localhost:" + targetJvm.getJmxmpPort(), "domains", "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      assertThat(output)
          .as("Expected 'JMImplementation' domain in output: " + output)
          .contains("JMImplementation");
    }
  }

  @Test
  void testOpenWithFullJmxmpServiceUrl() throws Exception {
    try (JmxTermProcessHelper jmxterm = new JmxTermProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open service:jmx:jmxmp://localhost:" + targetJvm.getJmxmpPort(), "domains", "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      assertThat(output)
          .as("Expected 'JMImplementation' domain in output: " + output)
          .contains("JMImplementation");
    }
  }

  @Test
  void testGetAttributeOverJmxmp() throws Exception {
    try (JmxTermProcessHelper jmxterm = new JmxTermProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open jmxmp://localhost:" + targetJvm.getJmxmpPort(),
          "bean test:type=TestMBean",
          "run reset",
          "get Name",
          "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      assertThat(output).as("Expected 'default' in output: " + output).contains("default");
    }
  }

  @Test
  void testRunOperationOverJmxmp() throws Exception {
    try (JmxTermProcessHelper jmxterm = new JmxTermProcessHelper()) {
      jmxterm.sendCommandAndClose(
          "open jmxmp://localhost:" + targetJvm.getJmxmpPort(),
          "bean test:type=TestMBean",
          "run echo world",
          "quit");
      String output = jmxterm.readAllOutput(TIMEOUT);
      assertThat(output).as("Expected 'echo:world' in output: " + output).contains("echo:world");
    }
  }
}
