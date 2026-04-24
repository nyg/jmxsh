package sh.jmx.jmxsh.e2e;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Launches {@link TestTargetApp} as a subprocess with JMX remote enabled. Provides the JMX port
 * for test connections and manages the lifecycle of the target JVM.
 */
public class TargetJvmProcess implements AutoCloseable {

  private static final int MAX_PORT_RETRIES = 10;

  private final Process process;
  private final int jmxPort;
  private volatile boolean ready;

  /**
   * Starts the target JVM with JMX remote enabled on a random port. Retries with a different port
   * if the process fails to become ready (e.g. due to a port conflict).
   *
   * @throws IOException if the process cannot be started after all retries
   */
  public TargetJvmProcess() throws IOException {
    int port = 0;
    Process started = null;
    for (int attempt = 0; attempt < MAX_PORT_RETRIES; attempt++) {
      port = ThreadLocalRandom.current().nextInt(49152, 65536);
      started = startProcess(port);
      try {
        waitUntilReady(started, Duration.ofSeconds(10));
        break;
      } catch (IllegalStateException e) {
        started.destroyForcibly();
        started = null;
        if (attempt == MAX_PORT_RETRIES - 1) {
          throw new IOException(
              "Failed to start target JVM after " + MAX_PORT_RETRIES + " attempts", e);
        }
      }
    }
    this.jmxPort = port;
    this.process = started;
    this.ready = true;
  }

  private static Process startProcess(int port) throws IOException {
    String javaHome = System.getProperty("java.home");
    String javaBin = javaHome + "/bin/java";
    String classpath = System.getProperty("java.class.path");

    ProcessBuilder pb =
        new ProcessBuilder(
            javaBin,
            "-cp",
            classpath,
            "-Dcom.sun.management.jmxremote",
            "-Dcom.sun.management.jmxremote.port=" + port,
            "-Dcom.sun.management.jmxremote.authenticate=false",
            "-Dcom.sun.management.jmxremote.ssl=false",
            "-Dcom.sun.management.jmxremote.local.only=true",
            "sh.jmx.jmxsh.e2e.TestTargetApp");
    pb.redirectErrorStream(true);
    return pb.start();
  }

  /**
   * Waits until the target JVM prints "READY" to stdout. No-op if the process is already ready
   * (readiness is established during construction via retry loop).
   *
   * @param timeout maximum time to wait (ignored if already ready)
   * @throws IllegalStateException if the timeout expires before "READY" is received
   */
  public void waitUntilReady(Duration timeout) {
    if (ready) {
      return;
    }
    waitUntilReady(process, timeout);
    ready = true;
  }

  private static void waitUntilReady(Process proc, Duration timeout) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
    CompletableFuture<Boolean> future =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                String line;
                while ((line = reader.readLine()) != null) {
                  if (line.contains("READY")) {
                    return true;
                  }
                }
                return false;
              } catch (IOException e) {
                return false;
              }
            });
    try {
      boolean ready = future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
      if (!ready) {
        throw new IllegalStateException("Target JVM exited without printing READY");
      }
    } catch (TimeoutException e) {
      throw new IllegalStateException(
          "Target JVM did not become ready within " + timeout.toSeconds() + " seconds");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting for target JVM", e);
    } catch (ExecutionException e) {
      throw new IllegalStateException("Error while waiting for target JVM", e.getCause());
    }
  }

  /** @return the JMX remote port of the target JVM */
  public int getJmxPort() {
    return jmxPort;
  }

  /** @return the OS process ID of the target JVM */
  public long getPid() {
    return process.pid();
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


}
