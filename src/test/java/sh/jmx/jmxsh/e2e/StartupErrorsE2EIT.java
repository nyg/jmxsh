package sh.jmx.jmxsh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

/**
 * End-to-end tests verifying that startup failures produce a clean error message and a non-zero
 * exit code instead of a raw JVM stack trace.
 */
class StartupErrorsE2EIT {

  private static final Duration TIMEOUT = Duration.ofSeconds(15);

  /**
   * An invalid output file path causes {@code FileCommandOutput} construction to fail before
   * {@code output} is available in {@code execute()}, so the error is caught by the safety net in
   * {@code main()} and written to stderr as "# &lt;message&gt;".
   */
  @Test
  void invalidOutputFileProducesCleanError() throws Exception {
    try (JmxshProcessHelper jmxterm =
        new JmxshProcessHelper("-o", "/nonexistent/dir/output.txt")) {
      String output = jmxterm.readAllOutput(TIMEOUT);
      int exitCode = jmxterm.getExitCode();
      assertThat(exitCode).as("Invalid output file should produce non-zero exit code").isNotEqualTo(0);
      assertThat(output).as("Error should be prefixed with '#'").contains("#");
      assertThat(output)
          .as("Output should not contain a raw JVM stack trace: " + output)
          .doesNotContain("Exception in thread");
    }
  }

  /**
   * A failed auto-connect via {@code -l} throws an {@code IOException} inside {@code execute()},
   * which is caught by the new {@code catch (Exception e)} block and written through
   * {@code output.printError(e)} as "# &lt;message&gt;".
   */
  @Test
  void failedAutoConnectProducesCleanError() throws Exception {
    // Port 1 is reserved and will always refuse the connection.
    try (JmxshProcessHelper jmxterm = new JmxshProcessHelper("-l", "localhost:1")) {
      String output = jmxterm.readAllOutput(TIMEOUT);
      int exitCode = jmxterm.getExitCode();
      assertThat(exitCode).as("Failed auto-connect should produce non-zero exit code").isNotEqualTo(0);
      assertThat(output).as("Error should be prefixed with '#'").contains("#");
      assertThat(output)
          .as("Output should not contain a raw JVM stack trace: " + output)
          .doesNotContain("Exception in thread");
    }
  }
}
