package org.cyclopsgroup.jmxterm.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import org.cyclopsgroup.jmxterm.cc.CommandCenter;
import org.cyclopsgroup.jmxterm.io.VerboseLevel;
import org.cyclopsgroup.jmxterm.io.WriterCommandOutput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** Integration tests for verbose level filtering of output and error messages. */
class VerboseLevelIT {

  @RegisterExtension static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();

  private CommandCenter cc;
  private StringWriter resultWriter;
  private StringWriter messageWriter;

  @BeforeEach
  void setUp() throws Exception {
    resultWriter = new StringWriter();
    messageWriter = new StringWriter();
    cc = new CommandCenter(new WriterCommandOutput(resultWriter, messageWriter), null);
  }

  @AfterEach
  void tearDown() {
    cc.close();
  }

  @Test
  void testBriefMessages() {
    // Default level is BRIEF — messages should appear with '#' prefix
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    String messages = messageWriter.toString();
    assertThat(messages)
        .as("Expected '#' prefixed messages in BRIEF mode, got: " + messages)
        .contains("#");
    assertThat(messages).as("Expected connection message, got: " + messages).contains("Connection to");
  }

  @Test
  void testSilentSuppressesMessages() {
    cc.setVerboseLevel(VerboseLevel.SILENT);
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("get Name")).isTrue();
    String messages = messageWriter.toString();
    assertThat(messages).as("Expected no messages in SILENT mode, got: " + messages).isEmpty();
  }

  @Test
  void testSilentStillShowsValues() {
    cc.setVerboseLevel(VerboseLevel.SILENT);
    assertThat(cc.execute("open " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("bean test:type=TestMBean")).isTrue();
    assertThat(cc.execute("get Name")).isTrue();
    assertThat(resultWriter.toString())
        .as("Expected result value 'default' even in SILENT mode, got: " + resultWriter)
        .contains("default");
  }

  @Test
  void testBriefShowsShortErrors() {
    // Default level is BRIEF
    assertThat(cc.execute("get Name")).isFalse();
    String messages = messageWriter.toString();
    assertThat(messages)
        .as("Expected '#' prefixed error in BRIEF mode, got: " + messages)
        .contains("#");
    assertThat(messages)
        .as("Expected no stack trace in BRIEF mode, got: " + messages)
        .doesNotContain("\tat ");
  }
}
