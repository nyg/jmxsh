package sh.jmx.jmxsh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** End-to-end integration tests for jmxsh CLI argument handling. */
class CliArgumentsE2EIT {

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
  void testAutoConnect() throws Exception {
    try (JmxshProcessHelper jmxsh = new JmxshProcessHelper("-l", "localhost:" + targetJvm.getJmxPort())) {
      jmxsh.sendCommandAndClose("domains", "quit");
      String output = jmxsh.readAllOutput(TIMEOUT);
      assertThat(output)
          .as("Expected 'JMImplementation' domain in output: " + output)
          .contains("JMImplementation");
    }
  }

  @Test
  void testSilentMode() throws Exception {
    try (JmxshProcessHelper jmxsh = new JmxshProcessHelper("-q")) {
      jmxsh.sendCommandAndClose(
          "open localhost:" + targetJvm.getJmxPort(),
          "bean test:type=TestMBean",
          "get Name",
          "quit");
      String output = jmxsh.readAllOutput(TIMEOUT);
      // In silent mode, informational messages prefixed with "#" should not appear
      for (String line : output.split("\\R")) {
        assertThat(line)
            .as("Silent mode should not produce '#' prefixed lines, but found: " + line)
            .doesNotStartWith("#");
      }
      // The attribute value should still be present
      assertThat(output).as("Expected 'default' value in output: " + output).contains("default");
    }
  }

  @Test
  void testExitOnFailure() throws Exception {
    try (JmxshProcessHelper jmxsh = new JmxshProcessHelper("-e")) {
      // Send a command that fails (getting an attribute without a connection)
      jmxsh.sendCommandAndClose("get Name");
      String output = jmxsh.readAllOutput(TIMEOUT);
      int exitCode = jmxsh.getExitCode();
      assertThat(exitCode).as("Expected non-zero exit code for failed command, output: " + output).isNotZero();
    }
  }

  @Test
  void testHelpFlag() throws Exception {
    try (JmxshProcessHelper jmxsh = new JmxshProcessHelper("-h")) {
      String output = jmxsh.readAllOutput(TIMEOUT);
      int exitCode = jmxsh.getExitCode();
      assertThat(output)
          .as("Expected usage information in output: " + output)
          .satisfiesAnyOf(
              o -> assertThat(o).contains("usage"),
              o -> assertThat(o).contains("Usage"),
              o -> assertThat(o).contains("jmxsh"));
      assertThat(exitCode).as("Help flag should result in exit code 0").isZero();
    }
  }
}
