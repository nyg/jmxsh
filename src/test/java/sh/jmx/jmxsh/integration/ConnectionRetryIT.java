package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.StringWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for the automatic reconnect behavior when a JMX connection drops. */
class ConnectionRetryIT {

  @RegisterExtension
  static EmbeddedJmxmpServer jmxmpServer = new EmbeddedJmxmpServer();

  private CommandCenter cc;
  private StringWriter resultWriter;
  private StringWriter messageWriter;

  @BeforeEach
  void setUp() throws Exception {
    resultWriter = new StringWriter();
    messageWriter = new StringWriter();
    cc = new CommandCenter(new WriterCommandOutput(resultWriter, messageWriter), null);
    cc.execute("open " + jmxmpServer.getConnectionUrl());
    cc.setRetryParams(1, 3);
    // Reset writers so assertions only cover the test body, not setUp output
    resultWriter.getBuffer().setLength(0);
    messageWriter.getBuffer().setLength(0);
  }

  @AfterEach
  void tearDown() throws Exception {
    cc.close();
    if (!jmxmpServer.getConnectorServer().isActive()) {
      jmxmpServer.restart();
    }
  }

  @Test
  void reconnectFailsAndSessionIsFullyDisconnectedWhenServerDoesNotReturn() throws Exception {
    jmxmpServer.stop();
    awaitConnectionDrop();

    boolean result = cc.execute("domains");

    assertThat(result).isFalse();
    assertThat(cc.getSession().isConnected()).isFalse();
    assertThat(cc.getSession().canReconnect()).isFalse();
    assertThat(messageWriter.toString()).contains("Connection lost");
    assertThat(messageWriter.toString()).contains("All reconnection attempts failed");
  }

  @Test
  void sessionReconnectsTransparentlyWhenServerComesBackUp() throws Exception {
    jmxmpServer.stop();
    awaitConnectionDrop();

    // Restart the server after 1.5 s — within the 3-attempt window (each attempt waits 1 s)
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    Future<?> restartTask =
        scheduler.schedule(
            () -> {
              try {
                jmxmpServer.restart();
              } catch (Exception e) {
                throw new RuntimeException("Server restart failed", e);
              }
            },
            1500,
            TimeUnit.MILLISECONDS);

    try {
      // First call: command fails (not auto-retried), but reconnect succeeds in background
      boolean firstResult = cc.execute("domains");
      restartTask.get(5, TimeUnit.SECONDS); // surface any restart exception

      assertThat(firstResult).isFalse();
      assertThat(cc.getSession().isConnected()).isTrue();
      assertThat(messageWriter.toString()).contains("Connection lost");
      assertThat(messageWriter.toString()).contains("Reconnected to");
      assertThat(messageWriter.toString()).contains("Please retry your command");

      // Second call: should succeed on the restored connection
      assertThat(cc.execute("domains")).isTrue();
    } finally {
      scheduler.shutdownNow();
    }
  }

  /** Polls until the session detects a broken connection, or fails the test on timeout. */
  private void awaitConnectionDrop() throws InterruptedException {
    for (int i = 0; i < 50; i++) {
      Thread.sleep(100);
      if (!cc.getSession().isConnectionAlive()) {
        return;
      }
    }
    fail("Connection did not drop within 5 seconds after server stop");
  }
}
