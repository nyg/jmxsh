package sh.jmx.jmxsh.e2e;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Launches the jmxsh uber JAR as a subprocess in non-interactive mode and provides methods to
 * send commands via stdin and read output from stdout.
 */
public class JmxshProcessHelper implements AutoCloseable {

  private final Process process;

  /**
   * Finds the uber JAR and launches jmxsh with {@code -n} (non-interactive) plus any extra
   * arguments.
   *
   * @param extraArgs additional CLI arguments (e.g. {@code "-q"} for quiet mode)
   * @throws IOException if the JAR is not found or the process cannot be started
   */
  public JmxshProcessHelper(String... extraArgs) throws IOException {
    Path uberJar = findUberJar();
    String javaHome = System.getProperty("java.home");
    String javaBin = javaHome + "/bin/java";

    List<String> command = new ArrayList<>();
    command.add(javaBin);
    command.add("-jar");
    command.add(uberJar.toAbsolutePath().toString());
    command.add("-n");
    Collections.addAll(command, extraArgs);

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true);
    this.process = pb.start();
  }

  /**
   * Sends a single command to jmxsh's stdin.
   *
   * @param command the command string (without newline)
   * @throws IOException if writing fails
   */
  public void sendCommand(String command) throws IOException {
    OutputStream stdin = process.getOutputStream();
    stdin.write((command + "\n").getBytes(StandardCharsets.UTF_8));
    stdin.flush();
  }

  /**
   * Sends multiple commands and then closes stdin to signal EOF.
   *
   * @param commands the commands to send
   * @throws IOException if writing fails
   */
  public void sendCommandAndClose(String... commands) throws IOException {
    OutputStream stdin = process.getOutputStream();
    for (String cmd : commands) {
      stdin.write((cmd + "\n").getBytes(StandardCharsets.UTF_8));
    }
    stdin.flush();
    stdin.close();
  }

  /**
   * Closes stdin (if still open), waits for the process to exit, and returns all stdout output.
   *
   * @param timeout maximum time to wait for the process to exit
   * @return the complete stdout/stderr output
   * @throws IOException if reading fails
   * @throws IllegalStateException if the process does not exit within the timeout
   */
  public String readAllOutput(Duration timeout) throws IOException {
    try {
      process.getOutputStream().close();
    } catch (IOException ignored) {
      // stdin may already be closed
    }

    StringBuilder sb = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

    boolean exited;
    try {
      exited = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting for jmxsh to exit", e);
    }

    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append(System.lineSeparator());
    }

    if (!exited) {
      process.destroyForcibly();
      throw new IllegalStateException(
          "jmxsh did not exit within " + timeout.toSeconds() + " seconds");
    }
    return sb.toString();
  }

  /**
   * Returns the exit code of the jmxsh process. The process must have already exited.
   *
   * @return the exit code
   */
  public int getExitCode() {
    return process.exitValue();
  }

  @Override
  public void close() {
    process.destroyForcibly();
    try {
      process.waitFor(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static Path findUberJar() throws IOException {
    Path targetDir = Path.of("target");
    try (var files = Files.list(targetDir)) {
      return files
          .filter(p -> p.getFileName().toString().matches("jmxsh-.*-uber\\.jar"))
          .findFirst()
          .orElseThrow(
              () ->
                  new IOException(
                      "Uber JAR not found in target/. Run 'mvn package' first."));
    }
  }
}
