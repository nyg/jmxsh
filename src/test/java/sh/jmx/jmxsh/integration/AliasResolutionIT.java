package sh.jmx.jmxsh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.nio.file.Path;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.utils.AliasStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

/** Integration tests for connection alias definition, resolution and removal. */
class AliasResolutionIT {

  @RegisterExtension static EmbeddedJmxServer jmxServer = new EmbeddedJmxServer();

  @TempDir private Path tempDir;

  private CommandCenter cc;
  private Path aliasesFile;
  private StringWriter resultWriter;
  private StringWriter messageWriter;

  @BeforeEach
  void setUp() {
    resultWriter = new StringWriter();
    messageWriter = new StringWriter();
    aliasesFile = tempDir.resolve("aliases.properties");
    cc = new CommandCenter(
        new WriterCommandOutput(resultWriter, messageWriter), null, new AliasStore(aliasesFile));
  }

  @AfterEach
  void tearDown() {
    cc.close();
  }

  @Test
  void testOpenResolvesAlias() {
    assertThat(cc.execute("alias my_server " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(cc.execute("open my_server")).isTrue();
    assertThat(cc.execute("domains")).isTrue();
    assertThat(cc.execute("close")).isTrue();
  }

  @Test
  void testAliasPersistsToFile() {
    assertThat(cc.execute("alias my_server " + jmxServer.getConnectionUrl())).isTrue();
    assertThat(aliasesFile).exists();
    assertThat(new AliasStore(aliasesFile).resolve("my_server"))
        .isEqualTo(jmxServer.getConnectionUrl());
  }

  @Test
  void testAliasListAndRemove() {
    assertThat(cc.execute("alias my_server " + jmxServer.getConnectionUrl())).isTrue();
    resultWriter.getBuffer().setLength(0);
    assertThat(cc.execute("alias")).isTrue();
    assertThat(resultWriter.toString())
        .contains("my_server = " + jmxServer.getConnectionUrl());
    assertThat(cc.execute("unalias my_server")).isTrue();
    assertThat(new AliasStore(aliasesFile).names()).isEmpty();
  }

  @Test
  void testUnaliasFailsForUnknownName() {
    assertThat(cc.execute("unalias no_such_alias")).isFalse();
  }

  @Test
  void testAliasRejectsInvalidName() {
    assertThat(cc.execute("alias 1234 localhost:9991")).isFalse();
  }
}
