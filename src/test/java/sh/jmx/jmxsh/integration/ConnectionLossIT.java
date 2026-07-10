package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.StringWriter;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.Test;

/** Integration tests for behavior when the JMX server dies while a session is connected. */
class ConnectionLossIT {

  @Test
  void should_print_friendly_message_and_clear_state_when_connection_is_lost() throws Exception {
    // Given
    EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();
    jmxServer.start();
    StringWriter resultWriter = new StringWriter();
    StringWriter messageWriter = new StringWriter();
    CommandCenter cc = new CommandCenter(new WriterCommandOutput(resultWriter, messageWriter), null);
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("domain test")).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    jmxServer.stop();

    // When
    boolean result = cc.execute("beans");

    // Then
    assertThat(result).isFalse();
    assertThat(messageWriter.toString())
        .contains("was lost. Disconnected.")
        .doesNotContain("ConnectException")
        .doesNotContain("nested exception");
    assertThat(cc.getSession().isConnected()).isFalse();
    assertThat(cc.getSession().getDomain()).isNull();
    assertThat(cc.getSession().getBean()).isNull();
    assertThatCode(cc::close).doesNotThrowAnyException();
  }

  @Test
  void should_quit_cleanly_when_connection_is_lost() throws Exception {
    // Given
    EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();
    jmxServer.start();
    StringWriter resultWriter = new StringWriter();
    StringWriter messageWriter = new StringWriter();
    CommandCenter cc = new CommandCenter(new WriterCommandOutput(resultWriter, messageWriter), null);
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    jmxServer.stop();

    // When
    boolean result = cc.execute("quit");

    // Then
    assertThat(result).isTrue();
    assertThat(cc.isClosed()).isTrue();
    assertThat(messageWriter.toString())
        .contains("Bye.")
        .doesNotContain("ConnectException")
        .doesNotContain("nested exception");
    assertThatCode(cc::close).doesNotThrowAnyException();
  }
}
