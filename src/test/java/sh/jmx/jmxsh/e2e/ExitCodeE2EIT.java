package sh.jmx.jmxsh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ExitCodeE2EIT {

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
  void testSuccessfulExecution() throws Exception {
    try (JmxshProcessHelper jmxsh = new JmxshProcessHelper()) {
      jmxsh.sendCommandAndClose(
          "open localhost:" + targetJvm.getJmxPort(), "domains", "quit");
      jmxsh.readAllOutput(TIMEOUT);
      assertThat(jmxsh.getExitCode()).as("Successful execution should return exit code 0").isZero();
    }
  }

  @Test
  void testExitOnFailureReturnsNegativeLineNumber() throws Exception {
    try (JmxshProcessHelper jmxsh = new JmxshProcessHelper("-e")) {
      // Line 1: valid command (help), Line 2: invalid command (get without connection/bean)
      jmxsh.sendCommandAndClose("help", "get Name");
      jmxsh.readAllOutput(TIMEOUT);
      int exitCode = jmxsh.getExitCode();
      // System.exit(-2) produces 254 on POSIX (unsigned byte: 256 - 2)
      assertThat(exitCode)
          .as("Expected non-zero exit code for failure with -e, but got: " + exitCode)
          .isNotZero();
      assertThat(exitCode)
          .as("Exit code should be -2 (or 254 unsigned), but got: " + exitCode)
          .isIn(-2, 254);
    }
  }

  @Test
  void testQuitExitCode() throws Exception {
    try (JmxshProcessHelper jmxsh = new JmxshProcessHelper()) {
      jmxsh.sendCommandAndClose("quit");
      jmxsh.readAllOutput(TIMEOUT);
      assertThat(jmxsh.getExitCode()).as("Quit command should return exit code 0").isZero();
    }
  }
}
